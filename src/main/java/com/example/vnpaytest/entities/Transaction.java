package com.example.vnpaytest.entities;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.criteria.CriteriaBuilder.In;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.CustomLog;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(schema = "vnpay_db_vnpt",name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "transaction_code",unique = true)
    private String transactionCode;

    @Column(name = "transaction_res_code")
    private String transationResCode;

    @Column(name = "transaction_status")
    private Integer transactionStatus; // 0= pending, 1= success, 2= success

    @Column(name = "transaction_no")
    private Long transactionNo;

    @Column(name = "bank_tran_no")
    private String bankTranNo;

    @Column(name = "bank_code")
    private String bankCode;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "pay_date")
    private Timestamp payDate;
}
