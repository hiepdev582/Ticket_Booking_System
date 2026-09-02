package com.ticketbooking.search.service;

import com.ticketbooking.common.enums.SeatStatus;
import com.ticketbooking.common.enums.VehicleType;
import com.ticketbooking.search.entity.Trip;
import com.ticketbooking.search.entity.TripSeat;
import com.ticketbooking.search.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final TripRepository tripRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (tripRepository.count() > 0) {
            log.info("DataSeeder: Chuyến đi đã tồn tại trong DB, bỏ qua khởi tạo.");
            return;
        }

        log.info("DataSeeder: Đang khởi tạo dữ liệu mẫu cho hệ thống đặt vé...");

        List<Trip> mockTrips = new ArrayList<>();

        // Chuyến 1: Sài Gòn -> Đà Lạt (Xe FUTA Limousine VIP 34 chỗ)
        Trip trip1 = Trip.builder()
                .id("trip-001")
                .tripCode("FUTA-SG-DL-01")
                .operatorName("Phương Trang (FUTA Bus)")
                .routeName("Sài Gòn ➔ Đà Lạt (Cao tốc)")
                .departureLocation("Bến xe Miền Đông Mới, TP.HCM")
                .arrivalLocation("Bến xe Liên Tỉnh Đà Lạt, Lâm Đồng")
                .departureTime(LocalDateTime.now().plusDays(1).withHour(23).withMinute(0).withSecond(0))
                .arrivalTime(LocalDateTime.now().plusDays(2).withHour(5).withMinute(30).withSecond(0))
                .vehicleType(VehicleType.BUS)
                .totalSeats(34)
                .availableSeats(34)
                .price(new BigDecimal("320000"))
                .build();
        generateSeatsForBus(trip1, 34);
        mockTrips.add(trip1);

        // Chuyến 2: Hà Nội -> Sa Pa (Xe Sao Việt Royal VIP 24 Cabin)
        Trip trip2 = Trip.builder()
                .id("trip-002")
                .tripCode("SAOVIET-HN-SP-02")
                .operatorName("Sao Việt Limousine")
                .routeName("Hà Nội ➔ Sa Pa (Cao tốc Nội Bài - Lào Cai)")
                .departureLocation("Bến xe Mỹ Đình, Hà Nội")
                .arrivalLocation("Bến xe Sa Pa, Lào Cai")
                .departureTime(LocalDateTime.now().plusDays(1).withHour(7).withMinute(30).withSecond(0))
                .arrivalTime(LocalDateTime.now().plusDays(1).withHour(13).withMinute(0).withSecond(0))
                .vehicleType(VehicleType.BUS)
                .totalSeats(24)
                .availableSeats(24)
                .price(new BigDecimal("450000"))
                .build();
        generateSeatsForBus(trip2, 24);
        mockTrips.add(trip2);

        // Chuyến 3: Đà Nẵng -> Huế (Tàu Hỏa SE2 Heritage)
        Trip trip3 = Trip.builder()
                .id("trip-003")
                .tripCode("DSVN-SE2-DNH")
                .operatorName("Đường Sắt Việt Nam (VNR)")
                .routeName("Đà Nẵng ➔ Huế (Đoàn tàu Di sản)")
                .departureLocation("Ga Đà Nẵng")
                .arrivalLocation("Ga Huế")
                .departureTime(LocalDateTime.now().plusDays(2).withHour(8).withMinute(0).withSecond(0))
                .arrivalTime(LocalDateTime.now().plusDays(2).withHour(11).withMinute(15).withSecond(0))
                .vehicleType(VehicleType.TRAIN)
                .totalSeats(28)
                .availableSeats(28)
                .price(new BigDecimal("180000"))
                .build();
        generateSeatsForTrain(trip3, 28);
        mockTrips.add(trip3);

        tripRepository.saveAll(mockTrips);
        log.info("DataSeeder: Đã nạp thành công 3 chuyến xe/tàu mẫu kèm sơ đồ ghế chi tiết!");
    }

    private void generateSeatsForBus(Trip trip, int count) {
        List<TripSeat> seats = new ArrayList<>();
        int half = count / 2;
        // Tầng 1: A01 -> A(half)
        for (int i = 1; i <= half; i++) {
            String seatNum = String.format("A%02d", i);
            seats.add(TripSeat.builder()
                    .id(UUID.randomUUID().toString())
                    .trip(trip)
                    .seatNumber(seatNum)
                    .deck(1)
                    .seatType(i <= 4 ? "VIP" : "STANDARD")
                    .status(SeatStatus.AVAILABLE)
                    .price(trip.getPrice())
                    .build());
        }
        // Tầng 2: B01 -> B(half)
        for (int i = 1; i <= half; i++) {
            String seatNum = String.format("B%02d", i);
            seats.add(TripSeat.builder()
                    .id(UUID.randomUUID().toString())
                    .trip(trip)
                    .seatNumber(seatNum)
                    .deck(2)
                    .seatType(i <= 4 ? "VIP" : "STANDARD")
                    .status(SeatStatus.AVAILABLE)
                    .price(trip.getPrice())
                    .build());
        }
        trip.setSeats(seats);
    }

    private void generateSeatsForTrain(Trip trip, int count) {
        List<TripSeat> seats = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            String seatNum = String.format("T%02d", i);
            seats.add(TripSeat.builder()
                    .id(UUID.randomUUID().toString())
                    .trip(trip)
                    .seatNumber(seatNum)
                    .deck(1)
                    .seatType(i % 4 == 0 ? "VIP_WINDOW" : "STANDARD")
                    .status(SeatStatus.AVAILABLE)
                    .price(trip.getPrice())
                    .build());
        }
        trip.setSeats(seats);
    }
}
