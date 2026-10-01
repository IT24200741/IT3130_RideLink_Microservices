package com.ridelink.payment.dto;

import com.ridelink.payment.model.Payment;
import com.ridelink.payment.model.PaymentMethod;
import com.ridelink.payment.model.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private String id;
    private String rideId;
    private String passengerId;
    private String driverId;
    private double amount;
    private String currency;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private String transactionReference;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    public static PaymentResponse fromEntity(Payment payment) {
        if (payment == null) return null;
        return PaymentResponse.builder()
                .id(payment.getId())
                .rideId(payment.getRideId())
                .passengerId(payment.getPassengerId())
                .driverId(payment.getDriverId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency() != null ? payment.getCurrency() : "LKR")
                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .transactionReference(payment.getTransactionReference())
                .note(payment.getNote())
                .createdAt(payment.getCreatedAt())
                .completedAt(payment.getCompletedAt())
                .build();
    }
}
