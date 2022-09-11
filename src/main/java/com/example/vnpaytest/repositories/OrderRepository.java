package com.example.vnpaytest.repositories;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.example.vnpaytest.entities.Order;

@Repository
@Transactional
public interface OrderRepository extends JpaRepository<Order,Long> {

    @Query(value = "select * from orders o where \n"
        + "(:orderId = -1 or o.order_id = :orderId) \n "
        + "and ( :transactionCode ='ALL' or o.transaction_code = :transactionCode) \n"
        + "and ( :transactionRef ='ALL' or o.transaction_ref = :transactionRef) \n"
        + "order by o.order_status asc , o.order_id asc"
        ,nativeQuery = true)
    Page<Order> getByCreterias(Long orderId, String transactionCode,String transactionRef, Pageable pageable);


    @Query(value =" select * from orders o where o.transaction_ref = :transactionRef" ,nativeQuery = true)
    Optional<Order> getByTransactionRef(String transactionRef);

}
