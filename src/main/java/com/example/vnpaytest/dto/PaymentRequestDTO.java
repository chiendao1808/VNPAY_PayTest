package com.example.vnpaytest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class PaymentRequestDTO {

    private  String vnp_OrderType ;

    private  Long vnp_Amount;

    private String vnp_OrderInfo;

    private String vnp_Locate ;

    private String vnp_BankCode;


    public PaymentRequestDTO(String orderType, Long amount, String OrderInfo, String locate, String bankCode)
    {
        this.vnp_OrderType=orderType;
        this.vnp_Amount=amount*100;
        this.vnp_OrderInfo =OrderInfo;
        this.vnp_Locate=locate;
        this.vnp_BankCode = bankCode;
    }


}
