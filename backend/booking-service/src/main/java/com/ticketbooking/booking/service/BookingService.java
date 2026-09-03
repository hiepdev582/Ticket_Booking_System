package com.ticketbooking.booking.service;

import com.ticketbooking.booking.entity.Booking;
import com.ticketbooking.booking.entity.BookingItem;
import com.ticketbooking.booking.entity.TripSeat;
import com.ticketbooking.booking.repository.BookingItemRepository;
import com.ticketbooking.booking.repository.BookingRepository;
import com.ticketbooking.booking.repository.TripSeatRepository;
import com.ticketbooking.common.constants.KafkaTopicConstants;
import com.ticketbooking.common.constants.RedisKeyConstants;
import com.ticketbooking.common.constants.WebSocketConstants;
import com.ticketbooking.common.dto.*;
import com.ticketbooking.common.enums.BookingStatus;
import com.ticketbooking.common.enums.SeatStatus;
import com.ticketbooking.common.events.OrderPaidEvent;
import com.ticketbooking.common.events.SeatHoldInitiatedEvent;
import com.ticketbooking.common.events.SeatReleasedEvent;
import com.ticketbooking.common.exception.AppException;
import com.ticketbooking.common.exception.ResourceNotFoundException;
import com.ticketbooking.common.exception.SeatAlreadyHeldException;
import com.ticketbooking.common.exception.SeatConflictException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final RedissonClient redissonClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final BookingRepository bookingRepository;
    private final BookingItemRepository bookingItemRepository;
    private final TripSeatRepository tripSeatRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${booking.hold-duration-minutes:5}")
    private int holdDurationMinutes;

    @Transactional
    public HoldSeatResponse holdSeats(HoldSeatRequest request) {
        String tripId = request.getTripId();
        String userId = request.getUserId();
        List<String> seatNumbers = request.getSeatNumbers();

        if (seatNumbers == null || seatNumbers.isEmpty()) {
            throw new AppException("Vui lòng chọn ít nhất 1 ghế!");
        }

        log.info("Bắt đầu xử lý giữ ghế trip={}, seats={}, user={}", tripId, seatNumbers, userId);

        List<RLock> acquiredLocks = new ArrayList<>();
        try {
            // 1. Áp dụng Redisson Distributed Lock cho từng ghế
            for (String seatNumber : seatNumbers) {
                String lockKey = RedisKeyConstants.getLockSeatKey(tripId, seatNumber);
                RLock lock = redissonClient.getLock(lockKey);
                // Chờ lấy lock 3 giây, tự giải phóng lock sau 5 giây để tránh deadlock
                boolean isLocked = lock.tryLock(3, 5, TimeUnit.SECONDS);
                if (!isLocked) {
                    throw new SeatConflictException("Ghế " + seatNumber + " đang được người khác thao tác, vui lòng thử lại sau!");
                }
                acquiredLocks.add(lock);
            }

            // 2. Kiểm tra tính khả dụng của ghế trong Redis và Database
            List<TripSeat> seatsInDb = tripSeatRepository.findByTripIdAndSeatNumberIn(tripId, seatNumbers);
            if (seatsInDb.size() != seatNumbers.size()) {
                throw new ResourceNotFoundException("Một số ghế đã chọn không tồn tại trong hệ thống!");
            }

            for (TripSeat seat : seatsInDb) {
                String holdKey = RedisKeyConstants.getHoldSeatKey(tripId, seat.getSeatNumber());
                Boolean isHeldInRedis = redisTemplate.hasKey(holdKey);
                if (Boolean.TRUE.equals(isHeldInRedis) || seat.getStatus() != SeatStatus.AVAILABLE) {
                    throw new SeatAlreadyHeldException("Ghế " + seat.getSeatNumber() + " đã có người giữ chỗ hoặc đã được đặt!");
                }
            }

            // 3. Tính toán tổng tiền và tạo đơn đặt giữ chỗ (5 phút)
            BigDecimal totalAmount = seatsInDb.stream()
                    .map(TripSeat::getPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            String bookingId = UUID.randomUUID().toString();
            String bookingCode = "BK-" + System.currentTimeMillis() % 10000000;
            LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(holdDurationMinutes);

            Booking booking = Booking.builder()
                    .id(bookingId)
                    .bookingCode(bookingCode)
                    .userId(userId)
                    .customerName(request.getCustomerName())
                    .customerPhone(request.getCustomerPhone())
                    .customerEmail(request.getCustomerEmail())
                    .tripId(tripId)
                    .totalAmount(totalAmount)
                    .status(BookingStatus.PENDING_PAYMENT)
                    .expiresAt(expiresAt)
                    .build();

            List<BookingItem> items = seatsInDb.stream()
                    .map(s -> BookingItem.builder()
                            .id(UUID.randomUUID().toString())
                            .booking(booking)
                            .seatNumber(s.getSeatNumber())
                            .price(s.getPrice())
                            .build())
                    .collect(Collectors.toList());
            booking.setItems(items);
            bookingRepository.save(booking);

            // 4. Lưu TTL 5 phút (300 giây) vào Redis và cập nhật trạng thái DB
            for (TripSeat seat : seatsInDb) {
                String holdKey = RedisKeyConstants.getHoldSeatKey(tripId, seat.getSeatNumber());
                redisTemplate.opsForValue().set(holdKey, bookingId, holdDurationMinutes * 60L, TimeUnit.SECONDS);

                seat.setStatus(SeatStatus.HELD);
                seat.setHeldByUserId(userId);
                seat.setHeldUntil(expiresAt);
            }
            tripSeatRepository.saveAll(seatsInDb);

            // 5. Broadcast cập nhật trạng thái ghế qua WebSocket STOMP
            SeatUpdateMessage updateMessage = SeatUpdateMessage.builder()
                    .tripId(tripId)
                    .seatNumbers(seatNumbers)
                    .status(SeatStatus.HELD)
                    .userId(userId)
                    .eventType("SEAT_HELD")
                    .build();
            messagingTemplate.convertAndSend(WebSocketConstants.getTripSeatsTopic(tripId), updateMessage);

            // 6. Phát sự kiện sang Apache Kafka
            SeatHoldInitiatedEvent event = SeatHoldInitiatedEvent.builder()
                    .bookingId(bookingId)
                    .bookingCode(bookingCode)
                    .tripId(tripId)
                    .userId(userId)
                    .seatNumbers(seatNumbers)
                    .totalAmount(totalAmount)
                    .expiresAt(expiresAt)
                    .build();
            kafkaTemplate.send(KafkaTopicConstants.SEAT_HOLD_TOPIC, bookingId, event);

            log.info("Giữ ghế thành công đơn #{}, hết hạn lúc: {}", bookingCode, expiresAt);

            return HoldSeatResponse.builder()
                    .bookingId(bookingId)
                    .bookingCode(bookingCode)
                    .tripId(tripId)
                    .userId(userId)
                    .seatNumbers(seatNumbers)
                    .totalAmount(totalAmount)
                    .status(BookingStatus.PENDING_PAYMENT)
                    .expiresAt(expiresAt)
                    .remainingSeconds(holdDurationMinutes * 60L)
                    .build();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AppException("Lỗi ngắt luồng xử lý khóa ghế");
        } finally {
            // Luôn giải phóng Redlock an toàn
            for (RLock lock : acquiredLocks) {
                if (lock.isHeldByCurrentThread()) {
                    lock.unlock();
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public BookingDto getBookingById(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn đặt với ID: " + bookingId));
        return mapToBookingDto(booking);
    }

    @Transactional
    public void releaseSeatsForBooking(String bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null || booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            return;
        }

        log.info("Giải phóng ghế cho đơn đặt #{} do: {}", booking.getBookingCode(), reason);
        booking.setStatus(BookingStatus.EXPIRED);
        bookingRepository.save(booking);

        List<BookingItem> items = bookingItemRepository.findByBookingId(bookingId);
        List<String> seatNumbers = items.stream().map(BookingItem::getSeatNumber).collect(Collectors.toList());

        List<TripSeat> seatsInDb = tripSeatRepository.findByTripIdAndSeatNumberIn(booking.getTripId(), seatNumbers);
        for (TripSeat seat : seatsInDb) {
            String holdKey = RedisKeyConstants.getHoldSeatKey(booking.getTripId(), seat.getSeatNumber());
            redisTemplate.delete(holdKey);

            seat.setStatus(SeatStatus.AVAILABLE);
            seat.setHeldByUserId(null);
            seat.setHeldUntil(null);
        }
        tripSeatRepository.saveAll(seatsInDb);

        // Broadcast nhả ghế qua WebSocket
        SeatUpdateMessage updateMessage = SeatUpdateMessage.builder()
                .tripId(booking.getTripId())
                .seatNumbers(seatNumbers)
                .status(SeatStatus.AVAILABLE)
                .eventType("SEAT_RELEASED")
                .build();
        messagingTemplate.convertAndSend(WebSocketConstants.getTripSeatsTopic(booking.getTripId()), updateMessage);

        // Bắn event giải phóng sang Kafka
        SeatReleasedEvent event = SeatReleasedEvent.builder()
                .bookingId(bookingId)
                .tripId(booking.getTripId())
                .seatNumbers(seatNumbers)
                .reason(reason)
                .build();
        kafkaTemplate.send(KafkaTopicConstants.SEAT_RELEASED_TOPIC, bookingId, event);
    }

    @KafkaListener(topics = KafkaTopicConstants.ORDER_PAID_TOPIC, groupId = "booking-service-group")
    @Transactional
    public void handleOrderPaid(OrderPaidEvent event) {
        log.info("Nhận sự kiện thanh toán thành công cho đơn #{}", event.getBookingCode());
        Booking booking = bookingRepository.findById(event.getBookingId()).orElse(null);
        if (booking == null) return;

        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setConfirmedAt(LocalDateTime.now());
        bookingRepository.save(booking);

        List<String> seatNumbers = (event.getSeatNumbers() != null && !event.getSeatNumbers().isEmpty())
                ? event.getSeatNumbers()
                : booking.getItems().stream().map(BookingItem::getSeatNumber).collect(Collectors.toList());

        List<TripSeat> seats = tripSeatRepository.findByTripIdAndSeatNumberIn(booking.getTripId(), seatNumbers);
        for (TripSeat seat : seats) {
            String holdKey = RedisKeyConstants.getHoldSeatKey(booking.getTripId(), seat.getSeatNumber());
            redisTemplate.delete(holdKey); // Xóa key giữ chỗ tạm thời

            seat.setStatus(SeatStatus.BOOKED);
            seat.setHeldUntil(null);
        }
        tripSeatRepository.saveAll(seats);

        // Broadcast trạng thái đã bán (BOOKED) qua WebSocket
        SeatUpdateMessage updateMessage = SeatUpdateMessage.builder()
                .tripId(booking.getTripId())
                .seatNumbers(seatNumbers)
                .status(SeatStatus.BOOKED)
                .eventType("SEAT_BOOKED")
                .build();
        messagingTemplate.convertAndSend(WebSocketConstants.getTripSeatsTopic(booking.getTripId()), updateMessage);
    }

    private BookingDto mapToBookingDto(Booking booking) {
        List<String> seatNumbers = booking.getItems().stream()
                .map(BookingItem::getSeatNumber)
                .collect(Collectors.toList());

        return BookingDto.builder()
                .id(booking.getId())
                .bookingCode(booking.getBookingCode())
                .userId(booking.getUserId())
                .customerName(booking.getCustomerName())
                .customerPhone(booking.getCustomerPhone())
                .customerEmail(booking.getCustomerEmail())
                .tripId(booking.getTripId())
                .totalAmount(booking.getTotalAmount())
                .status(booking.getStatus())
                .expiresAt(booking.getExpiresAt())
                .createdAt(booking.getCreatedAt())
                .seatNumbers(seatNumbers)
                .build();
    }
}
