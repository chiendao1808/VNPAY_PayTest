package com.example.vnpaytest.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.example.vnpaytest.configurations.RestTemplateConfig;
import com.example.vnpaytest.dto.PaymentRequestDTO;
import com.example.vnpaytest.services.VNPAYRequestService;

@RestController
@RequestMapping(path = "")
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
    public ResponseEntity<?> requestPay( @RequestBody PaymentRequestDTO paymentRequestDTO,
                                        HttpServletRequest request)
    {
        String requestURL = vnpayRequestService.doPay(request,paymentRequestDTO);
      //  restTemplate.postForObject(requestURL,paymentRequestDTO,String.class);
        return ResponseEntity.ok(requestURL);
    }


    @GetMapping("/IPN")
    public ResponseEntity<?> doIPN (@RequestParam(name = "vnp_TmnCode") String tmnCode,
                                    @RequestParam(name = "vnp_Amount") Long amount,
                                    @RequestParam(name = "vnp_BankCode") String bankCode,
                                    @RequestParam(name = "vnp_BankTranNo") String bankTranNo,
                                    @RequestParam(name = "vnp_CardType", required = false) String cardType,
                                    @RequestParam(name = "vnp_PayDate", required = false) Long payDate,
                                    @RequestParam(name = "vnp_OrderInfo") String orderInfo,
                                    @RequestParam(name = "vnp_TransactionNo") Long transactionNo,
                                    @RequestParam(name = "vnp_ResponseCode") Integer responseCode,
                                    @RequestParam(name = "vnp_TransactionStatus") Integer transactionStatus,
                                    @RequestParam(name = "vnp_TxnRef") String txnRef,
                                    @RequestParam(name = "vnp_SecureHashType",required = false) String secureHashType,
                                    @RequestParam(name = "vnp_SecureHash") String secureHash,
                                    HttpServletRequest request)
    {
        return ResponseEntity.ok("");
    }

    @GetMapping("/ReturnUrl")
    public ResponseEntity<?> returnPaymentInfo( @RequestParam(name = "vnp_TmnCode") String tmnCode,
                                                @RequestParam(name = "vnp_Amount") Long amount,
                                                @RequestParam(name = "vnp_BankCode") String bankCode,
                                                @RequestParam(name = "vnp_BankTranNo") String bankTranNo,
                                                @RequestParam(name = "vnp_CardType", required = false) String cardType,
                                                @RequestParam(name = "vnp_PayDate", required = false) Long payDate,
                                                @RequestParam(name = "vnp_OrderInfo") String orderInfo,
                                                @RequestParam(name = "vnp_TransactionNo") Long transactionNo,
                                                @RequestParam(name = "vnp_ResponseCode") Integer responseCode,
                                                @RequestParam(name = "vnp_TransactionStatus") Integer transactionStatus,
                                                @RequestParam(name = "vnp_TxnRef") String txnRef,
                                                @RequestParam(name = "vnp_SecureHashType",required = false) String secureHashType,
                                                @RequestParam(name = "vnp_SecureHash") String secureHash,
                                                HttpServletRequest request)
    {
        return ResponseEntity.ok("");
    }

}
