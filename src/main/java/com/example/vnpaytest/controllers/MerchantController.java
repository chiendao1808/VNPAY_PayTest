package com.example.vnpaytest.controllers;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.example.vnpaytest.dto.OrderRequestDTO;
import com.example.vnpaytest.dto.RefundRequestDTO;
import com.example.vnpaytest.serviceimpls.VNPAYRequestService;

@RestController
@RequestMapping
public class MerchantController {

    @Autowired
    private VNPAYRequestService vnpayRequestService;


    @Autowired
    private RestTemplate restTemplate;

    @GetMapping(path = "/test")
    public ResponseEntity<?> testRestTemplate(@RequestParam(name = "url") String url)
    {
        Object result = restTemplate.getForObject(url,Object.class);
        return  ResponseEntity.ok(result);
    }


    @PostMapping(path = "/pay/direct/vnpay")
    public ResponseEntity<?> requestPay( @RequestBody OrderRequestDTO orderRequestDTO,
                                        HttpServletRequest request)
    {
       //restTemplate.postForObject(requestURL,paymentRequestDTO,String.class);
        return ResponseEntity.ok(vnpayRequestService.doPay(request,orderRequestDTO));
    }


    @GetMapping("/IPN")
    public ResponseEntity<?> processIPN (HttpServletRequest request)
    {
        return ResponseEntity.ok(vnpayRequestService.doIPN(request));
    }

    @GetMapping("/ReturnUrl")
    public ResponseEntity<?> processReturn(HttpServletRequest request)
    {
        return ResponseEntity.ok(vnpayRequestService.doReturn(request));
    }

    @PostMapping("/api/refund")
    public ResponseEntity<?> requestRefund(@RequestBody RefundRequestDTO refundRequestDTO,HttpServletRequest request)
    {
        return ResponseEntity.ok(vnpayRequestService.doRefund(refundRequestDTO,request));
    }

}
