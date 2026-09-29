package com.ridelink.payment.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
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
}