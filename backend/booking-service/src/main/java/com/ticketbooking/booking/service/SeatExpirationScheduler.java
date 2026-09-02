package com.ticketbooking.booking.service;

import com.ticketbooking.booking.entity.Booking;
import com.ticketbooking.booking.repository.BookingRepository;
import com.ticketbooking.common.enums.BookingStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SeatExpirationScheduler {

    private final BookingRepository bookingRepository;
    private final BookingService bookingService;

    /**
     * Quét các đơn đặt chưa thanh toán đã quá thời hạn 5 phút (Chạy mỗi 10 giây).
     */
    @Scheduled(fixedRate = 10000)
    public void scanAndReleaseExpiredBookings() {
        LocalDateTime now = LocalDateTime.now();
        List<Booking> expiredBookings = bookingRepository.findByStatusAndExpiresAtBefore(BookingStatus.PENDING_PAYMENT, now);

        if (!expiredBookings.isEmpty()) {
            log.info("SeatExpirationScheduler: Phát hiện {} đơn đặt giữ chỗ đã hết hạn 5 phút.", expiredBookings.size());
            for (Booking booking : expiredBookings) {
                try {
                    bookingService.releaseSeatsForBooking(booking.getId(), "EXPIRED_5_MINUTES");
                } catch (Exception e) {
                    log.error("Lỗi khi tự động giải phóng ghế cho đơn #{}: {}", booking.getBookingCode(), e.getMessage());
                }
            }
        }
    }
}
