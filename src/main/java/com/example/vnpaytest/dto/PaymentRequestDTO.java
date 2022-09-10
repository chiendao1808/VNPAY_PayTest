package com.example.vnpaytest.dto;

import java.sql.Timestamp;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDTO {

    private String message;

    private String paymentDirectURL;

    private Timestamp createdTime;
}
