package com.example.vnpaytest.entities;

import java.sql.Timestamp;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.context.annotation.Configuration;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "vnpay_db_vnpt",name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id",unique = true,nullable = false)
    private Long orderId;

    @Column(name = "order_code",unique = true)
    private String orderCode;

    @Column(name = "order_type")
    private String orderType ;

    @Column(name = "order_info")
    private String orderInfo;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "currency_code")
    private String currencyCode;

    @Column(name = "order_status")
    private Integer orderStatus;  // 0 = pending , 1= success , 2 = fail

    @Column(name = "order_locale")
    private String orderLocale;

    @Column(name = "create_date")
    private Timestamp createDate;

    @Column(name = "transaction_ref")
    private String transactionRef; // define a order

    @Column(name = "expire_date")
    private Timestamp expireDate;

    @Column(name = "cancelled")
    private Boolean cancelled;


}
