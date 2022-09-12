package com.example.vnpaytest.dto;

import java.sql.Timestamp;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefundResponseDTO {

    private String message;

    private String requestURL ;

    private final String method ="POST";

    private Date createdTime;
}
