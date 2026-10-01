package com.ridelink.payment.service;

import com.ridelink.payment.dto.*;
import com.ridelink.payment.exception.PaymentNotFoundException;
import com.ridelink.payment.model.Fare;
import com.ridelink.payment.model.Payment;
import com.ridelink.payment.model.PaymentStatus;
import com.ridelink.payment.repository.FareRepository;
import com.ridelink.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final FareRepository fareRepository;

    public PaymentService(PaymentRepository paymentRepository, FareRepository fareRepository) {
        this.paymentRepository = paymentRepository;
        this.fareRepository = fareRepository;
    }

    public PaymentResponse createPayment(CreatePaymentRequest request) {
        // Check if a payment for this ride already exists
        paymentRepository.findByRideId(request.getRideId()).ifPresent(p -> {
            if (p.getStatus() == PaymentStatus.COMPLETED) {
                throw new IllegalStateException("Payment for ride ID " + request.getRideId() + " has already been completed");
            }
        });

        Payment payment = Payment.builder()
                .rideId(request.getRideId())
                .passengerId(request.getPassengerId())
                .driverId(request.getDriverId())
                .amount(request.getAmount())
                .currency("LKR")
                .paymentMethod(request.getPaymentMethod())
                .status(PaymentStatus.PENDING)
                .note(request.getNote())
                .createdAt(LocalDateTime.now())
                .build();

        Payment saved = paymentRepository.save(payment);
        return PaymentResponse.fromEntity(saved);
    }

    public PaymentResponse processPayment(String paymentId, ProcessPaymentRequest request) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + paymentId));

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Payment " + paymentId + " is already marked as COMPLETED");
        }

        if (request != null && request.getPaymentMethod() != null) {
            payment.setPaymentMethod(request.getPaymentMethod());
        }

        if (request != null && request.getTransactionNote() != null) {
            payment.setNote(request.getTransactionNote());
        }

        // Generate unique transaction reference (e.g. TXN-RL-8F3A29B1)
        String txnRef = "TXN-RL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        payment.setTransactionReference(txnRef);
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setCompletedAt(LocalDateTime.now());

        Payment saved = paymentRepository.save(payment);
        return PaymentResponse.fromEntity(saved);
    }

    public PaymentResponse getPaymentById(String id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + id));
        return PaymentResponse.fromEntity(payment);
    }

    public PaymentResponse getPaymentByRideId(String rideId) {
        Payment payment = paymentRepository.findByRideId(rideId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found for ride ID: " + rideId));
        return PaymentResponse.fromEntity(payment);
    }

    public List<PaymentResponse> getPaymentsByPassengerId(String passengerId) {
        return paymentRepository.findByPassengerId(passengerId)
                .stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<PaymentResponse> getAllPayments(PaymentStatus status) {
        List<Payment> list = (status != null)
                ? paymentRepository.findByStatus(status)
                : paymentRepository.findAll();

        return list.stream().map(PaymentResponse::fromEntity).collect(Collectors.toList());
    }

    public PaymentReceiptResponse getReceipt(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + paymentId));

        FareDetailResponse fareBreakdown = fareRepository.findByRideId(payment.getRideId())
                .map(FareDetailResponse::fromEntity)
                .orElse(null);

        String receiptNumber = "REC-" + payment.getId().substring(Math.max(0, payment.getId().length() - 8)).toUpperCase();

        return PaymentReceiptResponse.builder()
                .receiptNumber(receiptNumber)
                .paymentId(payment.getId())
                .rideId(payment.getRideId())
                .passengerId(payment.getPassengerId())
                .driverId(payment.getDriverId())
                .totalAmount(payment.getAmount())
                .currency(payment.getCurrency() != null ? payment.getCurrency() : "LKR")
                .paymentMethod(payment.getPaymentMethod())
                .paymentStatus(payment.getStatus())
                .transactionReference(payment.getTransactionReference())
                .fareBreakdown(fareBreakdown)
                .issuedAt(payment.getCompletedAt() != null ? payment.getCompletedAt() : LocalDateTime.now())
                .build();
    }
}
