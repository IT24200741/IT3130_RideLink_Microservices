package com.ridelink.payment.model;

import lombok.*;
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

    private double amount;

    private String paymentMethod;

    private String status;

    private LocalDateTime paymentDate;
}