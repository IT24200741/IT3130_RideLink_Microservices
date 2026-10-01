package com.ridelink.payment.controller;

import com.ridelink.payment.dto.*;
import com.ridelink.payment.model.PaymentStatus;
import com.ridelink.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // POST /api/payments — Create a new payment request (status = PENDING)
    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createPayment(request));
    }

    // POST /api/payments/{id}/process — Process / Settle payment (PENDING -> COMPLETED)
    @PostMapping("/{id}/process")
    public ResponseEntity<PaymentResponse> processPayment(
            @PathVariable String id,
            @RequestBody(required = false) ProcessPaymentRequest request
    ) {
        return ResponseEntity.ok(paymentService.processPayment(id, request));
    }

    // GET /api/payments/{id} — Get payment details by ID
    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable String id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    // GET /api/payments/ride/{rideId} — Get payment record for a ride
    @GetMapping("/ride/{rideId}")
    public ResponseEntity<PaymentResponse> getPaymentByRideId(@PathVariable String rideId) {
        return ResponseEntity.ok(paymentService.getPaymentByRideId(rideId));
    }

    // GET /api/payments/passenger/{passengerId} — Get payment history for a passenger
    @GetMapping("/passenger/{passengerId}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByPassengerId(@PathVariable String passengerId) {
        return ResponseEntity.ok(paymentService.getPaymentsByPassengerId(passengerId));
    }

    // GET /api/payments — List all payments (with optional ?status= filter)
    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments(@RequestParam(required = false) PaymentStatus status) {
        return ResponseEntity.ok(paymentService.getAllPayments(status));
    }

    // GET /api/payments/{id}/receipt — Generate printable receipt breakdown
    @GetMapping("/{id}/receipt")
    public ResponseEntity<PaymentReceiptResponse> getReceipt(@PathVariable String id) {
        return ResponseEntity.ok(paymentService.getReceipt(id));
    }
}
