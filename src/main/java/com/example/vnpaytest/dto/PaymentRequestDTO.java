package com.example.vnpaytest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class PaymentRequestDTO {

    private  String vnpOrderType ;

    private  Long vnpAmount;

    private String vnpOrderInfo;

    private String vnpLocale ;

    private String vnpBankCode;


    public PaymentRequestDTO(String orderType, Long amount, String OrderInfo, String locate, String bankCode)
    {
        this.vnpOrderType=orderType;
        this.vnpAmount=amount;
        this.vnpOrderInfo =OrderInfo;
        this.vnpLocale=locate;
        this.vnpBankCode = bankCode;
    }


}
