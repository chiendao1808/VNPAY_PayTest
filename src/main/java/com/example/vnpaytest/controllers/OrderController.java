package com.example.vnpaytest.controllers;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.vnpaytest.services.OrderService;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<?> get(@RequestParam(name = "order_id",required = false,defaultValue = "-1") Long orderId,
                                @RequestParam(name = "txn_code",required = false,defaultValue = "ALL") String transactionCode,
                                @RequestParam(name = "txn_ref",required = false,defaultValue = "ALL")String transactionRef,
                                @RequestParam(name = "page",required = false,defaultValue = "1") Integer page,
                                @RequestParam(name = "size",required = false,defaultValue = "1") Integer size,
                                Pageable pageable,
                                HttpServletRequest request)
    {
        return ResponseEntity.ok(orderService.getByCreterias(orderId,transactionCode,transactionRef,pageable));
    }

}
