package com.example.vnpaytest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefundRequestDTO {

    private String transactionType; // vnp_TransactionType

    private String transactionCode; // transaction code -> transaction -> transaction ref -> vnp_TxnRef

    private Long amount; // vnp_amount

    private String refundInfo; // vnp_orderInfo

    private String requestBy; // user request refunds





}
