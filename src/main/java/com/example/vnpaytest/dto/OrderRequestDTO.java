package com.example.vnpaytest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequestDTO {

    private  String vnpOrderType ;

    private  Long vnpAmount;

    private String vnpOrderInfo;

    private String vnpLocale ;

    private String vnpBankCode;

    private String vnpCardType;

    private String vnpCommand;


}
