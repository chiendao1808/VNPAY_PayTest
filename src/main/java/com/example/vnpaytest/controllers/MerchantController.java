package com.example.vnpaytest.controllers;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.example.vnpaytest.dto.OrderRequestDTO;
import com.example.vnpaytest.dto.PaymentRequestDTO;
import com.example.vnpaytest.services.OrderService;
import com.example.vnpaytest.services.VNPAYRequestService;

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
    public ResponseEntity<?> processIPN (@RequestParam(name = "vnp_TmnCode") String tmnCode,
                                    @RequestParam(name = "vnp_Amount") Long amount,
                                    @RequestParam(name = "vnp_BankCode") String bankCode,
                                    @RequestParam(name = "vnp_BankTranNo",required = false) String bankTranNo,
                                    @RequestParam(name = "vnp_CardType", required = false) String cardType,
                                    @RequestParam(name = "vnp_PayDate", required = false) Long payDate,
                                    @RequestParam(name = "vnp_OrderInfo") String orderInfo,
                                    @RequestParam(name = "vnp_TransactionNo") Long transactionNo,
                                    @RequestParam(name = "vnp_ResponseCode") String responseCode,
                                    @RequestParam(name = "vnp_TransactionStatus") String transactionStatus,
                                    @RequestParam(name = "vnp_TxnRef") String txnRef,
                                    @RequestParam(name = "vnp_SecureHashType",required = false,defaultValue = "HmacSHA512") String secureHashType,
                                    @RequestParam(name = "vnp_SecureHash") String secureHash,
                                    HttpServletRequest request)
    {
        return ResponseEntity.ok(vnpayRequestService.doIPN(request));
    }

    @GetMapping("/ReturnUrl")
    public ResponseEntity<?> processReturn(     @RequestParam(name = "vnp_TmnCode") String tmnCode,
                                                @RequestParam(name = "vnp_Amount") Long amount,
                                                @RequestParam(name = "vnp_BankCode") String bankCode,
                                                @RequestParam(name = "vnp_BankTranNo",required = false) String bankTranNo,
                                                @RequestParam(name = "vnp_CardType", required = false) String cardType,
                                                @RequestParam(name = "vnp_PayDate", required = false) Long payDate,
                                                @RequestParam(name = "vnp_OrderInfo") String orderInfo,
                                                @RequestParam(name = "vnp_TransactionNo") Long transactionNo,
                                                @RequestParam(name = "vnp_ResponseCode") String responseCode,
                                                @RequestParam(name = "vnp_TransactionStatus") String transactionStatus,
                                                @RequestParam(name = "vnp_TxnRef") String txnRef,
                                                @RequestParam(name = "vnp_SecureHashType",required = false,defaultValue = "HmacSHA512") String secureHashType,
                                                @RequestParam(name = "vnp_SecureHash") String secureHash,
                                                HttpServletRequest request)
    {
        //fake VNPAY send to IPN in merchant
        vnpayRequestService.doIPN(request);
        return ResponseEntity.ok(vnpayRequestService.doReturn(request));
    }

}
