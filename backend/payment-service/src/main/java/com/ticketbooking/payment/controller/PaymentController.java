package com.ticketbooking.payment.controller;

import com.ticketbooking.common.dto.ApiResponse;
import com.ticketbooking.common.dto.PaymentRequest;
import com.ticketbooking.common.dto.PaymentResponse;
import com.ticketbooking.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<PaymentResponse>> processCheckout(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.processPayment(request);
        return ResponseEntity.ok(ApiResponse.ok("Thanh toán thành công", response));
    }

    @PostMapping("/webhook")
    public ResponseEntity<ApiResponse<String>> handlePaymentWebhook(@RequestBody PaymentRequest payload) {
        // Giả lập webhook tiếp nhận từ cổng thanh toán bên thứ ba (VNPay/MoMo IPN)
        PaymentResponse response = paymentService.processPayment(payload);
        return ResponseEntity.ok(ApiResponse.ok("Webhook đã xử lý thành công", response.getTransactionId()));
    }
}
