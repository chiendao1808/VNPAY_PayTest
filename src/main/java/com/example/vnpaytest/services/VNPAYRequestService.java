package com.example.vnpaytest.services;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;


/*
* For process params required by VNPAY to process payment
* */
@Service
@Slf4j
public class VNPAYRequestService{


    // do Pay code impl
    public void doPay(HttpServletRequest request)
    {

    }


    // IPN code impl
    public void doIPN(HttpServletRequest request)
    {

    }


    // return-pay code impl
    public void doReturn(HttpServletRequest request)
    {

    }

}
