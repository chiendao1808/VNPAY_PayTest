package com.example.vnpaytest.repositories;

import java.util.List;
import java.util.Optional;
import javax.swing.text.html.Option;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.example.vnpaytest.entities.Transaction;

@Repository
@Transactional
public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    @Query(value = "select * from transations tran where \n"
        + "(:transactionId =-1 or tran.transaction_id = : transaction_id) \n"
        + "and (:transactionCode ='ALL' or tran.transaction_code = :transactionCode) \n"
        + "and (:transactionNo ='ALL' or tran.transaction_no = :transactionNo) \n"
        + "and (:transactionStatus =-1 or tran.transaction_status = :transactionStatus) \n"
        + "and (:orderId =-1 or tran.order_id = :orderId)",nativeQuery = true)
    Page<Transaction> getByCreterias(Long transactionId, String transactionCode,String transactionNo
                                    ,Long orderId, Integer transactionStatus, Pageable pageable);

    @Query(value = "select * from transactions tran where tran.transaction_code = :transactionCode",nativeQuery = true)
    Optional<Transaction> getByTransactionCode(String transactionCode);

    @Query(value = "select * from transactions tran where \n"
        + "tran.order_id = :orderId and tran.transaction_status = 1 order by tran.pay_time desc limit 1", nativeQuery = true)
    Optional<Transaction> getSuccessTransOfOrder(String orderId);

}
