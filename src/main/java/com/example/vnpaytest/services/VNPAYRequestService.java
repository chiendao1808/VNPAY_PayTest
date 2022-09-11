package com.example.vnpaytest.services;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import org.apache.catalina.util.URLEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.example.vnpaytest.configurations.VNPAYConfigs;
import com.example.vnpaytest.constants.VNPAYConsts;
import com.example.vnpaytest.dto.IPNResponse;
import com.example.vnpaytest.dto.OrderRequestDTO;
import com.example.vnpaytest.dto.PaymentRequestDTO;
import com.example.vnpaytest.dto.UserResponseDTO;
import com.example.vnpaytest.entities.Order;
import com.example.vnpaytest.repositories.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import utils.VNPAYUtils;


/*
* For process params required by VNPAY to process payment
* */
@Service
@Slf4j
public class VNPAYRequestService{

    @Autowired
    private OrderRepository orderRepository;


    // do Pay code impl -> return a request url
    public PaymentRequestDTO doPay(HttpServletRequest request, OrderRequestDTO orderRequestDTO)
    {
        try {
            StringBuilder query = new StringBuilder(VNPAYConsts.vnpayURL);
            query.append("?");
            String vnp_Version = "2.1.0";
            String vnp_Command = orderRequestDTO.getVnpCommand();
            String vnp_TmnCode = VNPAYConsts.vnp_tnmCode;
            String vnp_OrderInfo = orderRequestDTO.getVnpOrderInfo();
            String vnp_OrderType = orderRequestDTO.getVnpOrderType();
            String vnp_TxnRef = VNPAYConfigs.getRandomNum(8);
            String vnp_IpAddr = VNPAYConfigs.getIPAddress(request);
            // get host address
            int thirdSlashIndex = request.getRequestURL().indexOf("/",8);
            String vnp_ReturnUrl = request.getRequestURL().toString().substring(0,thirdSlashIndex) +"/ReturnUrl";

            Long vnp_Amount = orderRequestDTO.getVnpAmount()*100;
            String vnp_Locale = orderRequestDTO.getVnpLocale();
            String vnp_BankCode = orderRequestDTO.getVnpBankCode();
            String vnp_CurrCode = "VND";
            String vnp_CardType = orderRequestDTO.getVnpCardType();

            // get createdate and expire date
            int addTime = request.getLocale().getCountry().equals("VN")?0:7; // adapt add time to server
            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));

            calendar.add(Calendar.HOUR,addTime); // sync with  server
            Timestamp createDate = new Timestamp(calendar.getTimeInMillis());
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(calendar.getTime());

            calendar.add(Calendar.MINUTE,+15);
            Timestamp expireDate = new Timestamp(calendar.getTimeInMillis());
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
            vnp_Params.put("vnp_Locale",vnp_Locale);
            vnp_Params.put("vnp_CurrCode",vnp_CurrCode);

            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
           // Collections.sort(fieldNames);
            Collections.sort(fieldNames);
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
                    hashData.append(fieldName).append("=").append(java.net.URLEncoder.encode(fieldVal,StandardCharsets.US_ASCII.toString()));
                    query.append(fieldName).append("=").append(java.net.URLEncoder.encode(fieldVal,StandardCharsets.US_ASCII.toString()));
                }
                if(iterator.hasNext())
                {
                    hashData.append("&");
                    query.append("&");
                }
            }
            String vnp_SecureHash = VNPAYConfigs.hmacSHA512(VNPAYConsts.vnp_HashSecret,hashData.toString());
           // System.out.println("First secureHash:"+ vnp_SecureHash);
            query.append("&vnp_SecureHash").append("=").append(vnp_SecureHash);
            // create new order in db
            Order newOrder = Order.builder().orderInfo(vnp_OrderInfo)
                                            .orderType(vnp_OrderType)
                                            .orderLocale(vnp_Locale)
                                            .orderStatus(0)
                                            .amount(vnp_Amount/100)
                                            .secureHash(vnp_SecureHash)
                                            .bankCode(vnp_BankCode)
                                            .createDate(createDate)
                                            .expireDate(expireDate)
                                            .transactionRef(vnp_TxnRef)
                                            .currencyCode(vnp_CurrCode)
                                            .build();
            Optional<Order> addedOrder = Optional.ofNullable(orderRepository.save(newOrder));
            if(addedOrder.isPresent())
            {
                Order updateOrder = addedOrder.get();
                updateOrder.setTransactionCode(VNPAYUtils.generateTranCode(updateOrder.getOrderId()));
                orderRepository.save(updateOrder);
            } else  throw  new RuntimeException("Add order error");
            return  PaymentRequestDTO.builder().paymentDirectURL(query.toString())
                                                .message("Yêu cầu thanh toán đã được tạo!")
                                                .createdTime(createDate).build();
        } catch (Exception ex)
        {
            log.error("Do payment error",ex);
            return null;
        }
    }


    // IPN code impl
    public IPNResponse doIPN(HttpServletRequest request)
    {
        try{
            // get fields in request
            Map fields = new HashMap();
            for(Enumeration params = request.getParameterNames(); params.hasMoreElements();)
            {
                String fieldKey = java.net.URLEncoder.encode(params.nextElement().toString(),StandardCharsets.US_ASCII.toString());
                String fieldVal = java.net.URLEncoder.encode(request.getParameter(fieldKey),StandardCharsets.US_ASCII.toString());
                if(fieldKey != null && StringUtils.hasText(fieldVal))
                {
                    fields.put(fieldKey,fieldVal);
                  //  System.out.println(fieldKey +":" + fieldVal);
                }
            }
            // remove not hashed
            if(fields.containsKey("vnp_SecureHash")) fields.remove("vnp_SecureHash");
            if(fields.containsKey("vnp_SecureHashType")) fields.remove("vnp_SecureHashType");
            String secureHash = VNPAYConfigs.hashAllFields(fields);
            String vnp_SecureHash = request.getParameter("vnp_SecureHash");
            //get order with vnp_TxnRef
            Optional<Order> orderOptional = orderRepository.getByTransactionRef(request.getParameter("vnp_TxnRef"));
            if(!orderOptional.isPresent())
                throw new RuntimeException("Order not found");
            Order order = orderOptional.get();
            if(secureHash.equals(vnp_SecureHash))
            {
                // set transaction's infos
                order.setTransactionResCode(fields.get("vnp_ResponseCode").toString());
                order.setTransactionStatus(fields.get("vnp_TransactionStatus").toString());
                order.setTransactionNo(Long.valueOf(fields.get("vnp_TransactionNo").toString()));
                order.setBankTranNo(fields.get("vnp_BankTranNo").toString());
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                order.setPayDate(new Timestamp(formatter.parse(fields.get("vnp_PayDate").toString()).getTime()));

                // check conditions
                boolean checkOrderId = order.getTransactionRef().equals(request.getParameter("vnp_TxnRef"));
                boolean checkAmount = order.getAmount() ==  Long.valueOf(request.getParameter("vnp_Amount"))/100;
                order.setAmount(order.getAmount()/100);
                boolean checkOrderStatus = order.getOrderStatus() ==0;
                if(checkOrderId)
                {
                    if(checkAmount)
                    {
                        if(checkOrderStatus)
                        {
                            if(request.getParameter("vnp_ResponseCode").equals("00"))
                                order.setOrderStatus(1);
                            else order.setOrderStatus(2);
                            if(orderRepository.save(order) == null)
                                throw new RuntimeException("Update order status error");
                            return IPNResponse.builder().RspCode("00").Message("Confirm Success").build();
                        }
                        else return IPNResponse.builder().RspCode("02").Message("Order already confirmed").build();
                    }
                    else return IPNResponse.builder().RspCode("04").Message("Invalid Amount").build();
                }
                else return IPNResponse.builder().RspCode("01").Message("Order not Found").build();
            }
            else return IPNResponse.builder().RspCode("97").Message("Invalid Checksum").build();
        } catch (Exception ex)
        {
            log.error("IPN process error", ex);
            return IPNResponse.builder().RspCode("99").Message("Unknow error").build();
        }
    }


    // return-pay code impl
    public UserResponseDTO doReturn(HttpServletRequest request)
    {
        try{
            UserResponseDTO userResponseDTO = new UserResponseDTO();
            Map fields = new HashMap();
            for(Enumeration params = request.getParameterNames(); params.hasMoreElements();)
            {
                String fieldKey = java.net.URLEncoder.encode(params.nextElement().toString(),StandardCharsets.US_ASCII.toString());
                String fieldVal = java.net.URLEncoder.encode(request.getParameter(fieldKey),StandardCharsets.US_ASCII.toString());
                if(fieldKey != null && StringUtils.hasText(fieldVal))
                {
                    fields.put(fieldKey,fieldVal);
                    System.out.println(fieldKey +":" + fieldVal);
                }
            }
            String vnp_SecureHash = request.getParameter("vnp_SecureHash");
            if(fields.containsKey("vnp_SecureHash")) fields.remove("vnp_SecureHash");
            if(fields.containsKey("vnp_SecureHashType")) fields.remove("vnp_SecureHashType");
            // gen checksum
            String secureHash = VNPAYConfigs.hashAllFields(fields);

            Optional<Order> orderOptional = orderRepository.getByTransactionRef(request.getParameter("vnp_TxnRef"));
            if(!orderOptional.isPresent())
                throw new RuntimeException("Order not found");
           // Order order = orderOptional.get();
            String responseCode = request.getParameter("vnp_ResponseCode");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            Timestamp proccessTime = new Timestamp(formatter.parse(fields.get("vnp_PayDate").toString()).getTime() + 7*60*60*1000L);
            if(secureHash.equals(vnp_SecureHash))
            {
                if(responseCode.equals("00"))
                {
                    userResponseDTO.setMessage(VNPAYUtils.processResponseCodeStatusCode("00"));
                    userResponseDTO.setDetail("Thực hiện thanh toán thành công cho đơn hàng có mã tham chiếu :"+" "+request.getParameter("vnp_TxnRef"));
                    userResponseDTO.setPaymentStatus("Success");
                    userResponseDTO.setProccessTime(proccessTime);
                } else {
                    userResponseDTO.setMessage(VNPAYUtils.processResponseCodeStatusCode(responseCode));
                    userResponseDTO.setDetail("Thực hiện thanh toán thất bại cho đơn hàng có mã tham chiếu :"+" "+request.getParameter("vnp_TxnRef"));
                    userResponseDTO.setPaymentStatus("Fail");
                    userResponseDTO.setProccessTime(proccessTime);
                }
            } else {
                userResponseDTO.setMessage(VNPAYUtils.processResponseCodeStatusCode("97"));
                userResponseDTO.setDetail("Đã xảy ra lỗi trong quá trình xử lý thanh toán với mã lỗi: 97" );
                userResponseDTO.setPaymentStatus("Fail");
                userResponseDTO.setProccessTime(proccessTime);
            }
            return userResponseDTO;
        } catch (Exception ex)
        {
            log.error("Return process error", ex);
            return null;
        }
    }
}
