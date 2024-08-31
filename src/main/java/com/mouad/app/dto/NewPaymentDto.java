package com.mouad.app.dto;

import com.mouad.app.entities.PaymentType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NewPaymentDto {
    private double amount;
    private PaymentType type;
    private LocalDate date;
    private String studentCode;
}
