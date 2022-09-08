package com.example.vnpaytest.services;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.example.vnpaytest.configurations.VNPAYConfigs;
import com.example.vnpaytest.constants.VNPAYConsts;
import com.example.vnpaytest.dto.PaymentRequestDTO;
import lombok.extern.slf4j.Slf4j;


/*
* For process params required by VNPAY to process payment
* */
@Service
@Slf4j
public class VNPAYRequestService{


    // do Pay code impl -> return a request url
    public String doPay(HttpServletRequest request, PaymentRequestDTO paymentRequestDTO)
    {
        try {
            StringBuilder query = new StringBuilder(VNPAYConsts.vnpayURL);
            query.append("?");
            String vnp_Version = "2.1.0";
            String vnp_Command = "pay";
            String vnp_TmnCode = VNPAYConsts.vnp_tnmCode;
            String vnp_OrderInfo = paymentRequestDTO.getVnpOrderInfo();
            String vnp_OrderType = paymentRequestDTO.getVnpOrderType();
            String vnp_TxnRef = VNPAYConfigs.getRandomNum(8);
            String vnp_IpAddr = VNPAYConfigs.getIPAddress(request);
            String vnp_ReturnUrl = VNPAYConsts.returnURL +"/ReturnUrl";
            Long vnp_Amount = paymentRequestDTO.getVnpAmount();
            String vnp_Locate = paymentRequestDTO.getVnpLocale();
            String vnp_BankCode = paymentRequestDTO.getVnpBankCode();

            // get createdate and expire date
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            calendar.setTime(new Date(System.currentTimeMillis()));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(calendar.getTime());

            calendar.add(Calendar.MINUTE,+15);
            String vnp_ExpireDate = formatter.format(calendar.getTime());

            // Put params to params map
            Map vnp_Params = new HashMap();
            vnp_Params.put("vnp_Version",vnp_Version);
            vnp_Params.put("vnp_Command",vnp_Command);
            vnp_Params.put("vnp_TmnCode",vnp_TmnCode);
            vnp_Params.put("vnp_Amount",vnp_Amount);
            vnp_Params.put("vnp_BankCode",vnp_BankCode);
            vnp_Params.put("vnp_OrderInfo",vnp_OrderInfo);
            vnp_Params.put("vnp_OrderType",vnp_OrderType);
            vnp_Params.put("vnp_TxnRef",vnp_TxnRef);
            vnp_Params.put("vnp_IpAddr",vnp_IpAddr);
            vnp_Params.put("vnp_ReturnUrl",vnp_ReturnUrl);
            vnp_Params.put("vnp_CreateDate",vnp_CreateDate);
            vnp_Params.put("vnp_ExpireDate",vnp_ExpireDate);
            vnp_Params.put("vnp_Locale",vnp_Locate);
            vnp_Params.put("vnp_CurrCode","VND");

            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
           // Collections.sort(fieldNames);
            Collections.sort(fieldNames,(o1, o2) -> o1.compareTo(o2));
            StringBuilder hashData  = new StringBuilder();
            Iterator iterator = fieldNames.iterator();

            // add params to query
            while (iterator.hasNext())
            {
                String fieldName = (String) iterator.next();
                String fieldVal = String.valueOf(vnp_Params.get(fieldName));
                if(fieldName!=null && StringUtils.hasText(fieldVal))
                {
                    // concat param
                    hashData.append(fieldName).append("=").append(URLEncoder.encode(fieldVal, StandardCharsets.US_ASCII));
                    query.append(fieldName).append("=").append(URLEncoder.encode(fieldVal,StandardCharsets.US_ASCII));
                }
                if(iterator.hasNext())
                {
                    hashData.append("&");
                    query.append("&");
                }
            }
            String vnp_SecureHash = VNPAYConfigs.hmacSHA512(VNPAYConsts.vnp_HashSecret,hashData.toString());
            query.append("&vnp_SecureHash").append("=").append(vnp_SecureHash);
            return  query.toString();
        } catch (Exception ex)
        {
            log.error("Do payment error",ex);
            return null;
        }
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
