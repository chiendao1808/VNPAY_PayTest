package com.example.vnpaytest.entities;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(schema = "vnpay_db_vnpt",name = "refunds")
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "request_id", unique = true, nullable = false)
    private String requestId;

    @Column(name = "transaction_ref",nullable = false)
    private String transactionRef; // same as txn_ref in orders

    @Column(name = "transaction_no")
    private String transactionNo ;

    @Column(name = "transaction_type")
    private String transactionType; // refund type 02, 03 -> type

    @Column(name = "amount")
    private Long amount ;

    @Column(name = "request_info")
    private String requestInfo;

    @Column(name = "created_by")
    private String createdBy; // the person requested refund

    @Column(name = "create_date")
    private Timestamp createDate ;

    @Column(name = "transaction_date")
    private Timestamp transactionDate;

    @Column(name = "transaction_res_code")
    private String transactionResCode;

    @Column(name = "transaction_status")
    private String transactionStatus;

    @Column(name = "pay_date")
    private Timestamp payDate;

}
