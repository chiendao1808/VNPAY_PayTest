package com.example.vnpaytest.serviceimpls;

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
   public Page<Order> getByCreterias(Long orderId,String orderLocale, Pageable pageable)
    {
        try{
            return orderRepository.getByCreterias(orderId,orderLocale,pageable);
        } catch (Exception ex)
        {
            log.error("Get orders by creterias error", ex);
            return Page.empty();
        }
    }
}
