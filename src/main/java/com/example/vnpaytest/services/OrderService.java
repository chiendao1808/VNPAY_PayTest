package com.example.vnpaytest.services;

import java.util.Enumeration;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.vnpaytest.entities.Order;
import com.example.vnpaytest.repositories.OrderRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // get, filter orders
    Page<Order> getByCreterias(Long orderId,String secureHash,String transactionRef, Pageable pageable)
    {
        try{
            return orderRepository.getByCreterias(orderId,secureHash,transactionRef,pageable);
        } catch (Exception ex)
        {
            log.error("Get orders by creterias error", ex);
            return Page.empty();
        }
    }

    // get order by transaction_ref
    Optional<Order> getByTransactionRef(String transactionRef)
    {
        try{
            return orderRepository.getByTransactionRef(transactionRef);
        } catch (Exception ex)
        {
            log.error("Get order by transaction ref error",ex );
            return Optional.empty();
        }
    }


}
