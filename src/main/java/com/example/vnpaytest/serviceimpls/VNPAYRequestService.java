package com.example.vnpaytest.serviceimpls;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.example.vnpaytest.configurations.VNPAYConfigs;
import com.example.vnpaytest.constants.VNPAYConsts;
import com.example.vnpaytest.dto.IPNResponse;
import com.example.vnpaytest.dto.OrderRequestDTO;
import com.example.vnpaytest.dto.PaymentRequestDTO;
import com.example.vnpaytest.dto.RefundRequestDTO;
import com.example.vnpaytest.dto.RefundResponseDTO;
import com.example.vnpaytest.dto.UserResponseDTO;
import com.example.vnpaytest.entities.Order;
import com.example.vnpaytest.entities.Transaction;
import com.example.vnpaytest.repositories.OrderRepository;
import com.example.vnpaytest.repositories.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import com.example.vnpaytest.utils.VNPAYUtils;


/*
* For process params required by VNPAY to process payment
* */
@Service
@Slf4j
public class VNPAYRequestService{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TransactionRepository transactionRepository;


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

            Optional<Order> addedOrder = Optional.ofNullable(orderRepository
                                                                    .save(Order.builder().orderInfo(vnp_OrderInfo)
                                                                                        .orderType(vnp_OrderType)
                                                                                        .orderLocale(vnp_Locale)
                                                                                        .orderStatus(0)
                                                                                        .amount(vnp_Amount/100)
                                                                                        .createDate(createDate)
                                                                                        .expireDate(expireDate)
                                                                                        .transactionRef(vnp_TxnRef)
                                                                                        .currencyCode(vnp_CurrCode)
                                                                                        .cancelled(false)
                                                                                        .build()));
            if(addedOrder.isPresent())
            {
                Order updateOrder = addedOrder.get();
                updateOrder.setOrderCode("DH"+VNPAYUtils.generateTranAndOrderCode(updateOrder.getOrderId(),8));
                orderRepository.save(updateOrder);
            } else throw  new RuntimeException("Add order error");

            return  PaymentRequestDTO.builder().paymentDirectURL(query.toString())
                                                .message("Yêu cầu thanh toán đã được tạo!")
                                                .createdTime(createDate).build();
        } catch (Exception ex)
        {
            log.error("Do payment error",ex);
            return PaymentRequestDTO.builder()
                .message("Error in creating payment")
                .paymentDirectURL(null)
                .createdTime(new Timestamp(System.currentTimeMillis()+7*60*60*1000L)).build();
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
                }
            }
            // remove not hashed
            if(fields.containsKey("vnp_SecureHash")) fields.remove("vnp_SecureHash");
            if(fields.containsKey("vnp_SecureHashType")) fields.remove("vnp_SecureHashType");
            String secureHash = VNPAYConfigs.hashAllFields(fields);
            String vnp_SecureHash = request.getParameter("vnp_SecureHash");

            // date format
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");

            if(secureHash.equals(vnp_SecureHash))
            {
                String txnRef = request.getParameter("vnp_TxnRef");
                Order order = orderRepository.getByTranRef(txnRef).get();
                int addTime = request.getLocale().getCountry().equals("VN")?0:7;
                if(order.getExpireDate().before(new Timestamp(Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7")).getTimeInMillis()+addTime*60*60*1000L)))
                    order.setCancelled(true);

                // check conditions
                boolean checkOrderId = order !=null;
                boolean checkAmount = order.getAmount() ==  Long.valueOf(request.getParameter("vnp_Amount"))/100;
                order.setAmount(order.getAmount());
                boolean checkOrderStatus = order.getOrderStatus() ==0 && order.getCancelled() ==false;
                if(checkOrderId)
                {
                    if(checkAmount)
                    {
                        if(checkOrderStatus)
                        {
                            Transaction transaction = transactionRepository.save(Transaction.builder()
                                .transationResCode(request.getParameter("vnp_ResponseCode"))
                                .bankCode(request.getParameter("vnp_BankCode"))
                                .bankTranNo(request.getParameter("vnp_BankTranNo"))
                                .transactionNo(Long.valueOf(request.getParameter("vnp_TransactionNo")))
                                .order(order)
                                .amount(order.getAmount())
                                .currencyCode(order.getCurrencyCode())
                                .build());
                            if(request.getParameter("vnp_ResponseCode").equals("00")
                                && request.getParameter("vnp_TransactionStatus").equals("00")
                                && transaction !=null)
                            {
                                order.setOrderStatus(1);
                                transaction.setTransactionCode("GD"+VNPAYUtils.generateTranAndOrderCode(transaction.getTransactionId(),13));
                                transaction.setTransactionStatus(1);
                                transaction.setPayDate(new Timestamp(formatter.parse(request.getParameter("vnp_PayDate")).getTime()+addTime*60*60*1000L));
                            }
                            else {
                               // order.setOrderStatus(2);
                                transaction.setTransactionCode("GD"+VNPAYUtils.generateTranAndOrderCode(transaction.getTransactionId(),13));
                                transaction.setTransactionStatus(2);
                                transaction.setPayDate(new Timestamp(formatter.parse(request.getParameter("vnp_PayDate")).getTime()+addTime*60*60*1000L));
                            }
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
            return IPNResponse.builder().RspCode("99").Message("Unknown error").build();
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
                }
            }
            String vnp_SecureHash = request.getParameter("vnp_SecureHash");
            if(fields.containsKey("vnp_SecureHash")) fields.remove("vnp_SecureHash");
            if(fields.containsKey("vnp_SecureHashType")) fields.remove("vnp_SecureHashType");
            // gen checksum
            String secureHash = VNPAYConfigs.hashAllFields(fields);
            String txnRef = request.getParameter("vnp_TxnRef");
            Optional<Order> orderOptional = orderRepository.getByTranRef(txnRef);
            if(!orderOptional.isPresent())
                throw new RuntimeException("Order not found");
            String responseCode = request.getParameter("vnp_ResponseCode");
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            Timestamp proccessTime = new Timestamp(formatter.parse(fields.get("vnp_PayDate").toString()).getTime() + 7*60*60*1000L);
            if(secureHash.equals(vnp_SecureHash))
            {
                String orderCode = orderOptional.get().getOrderCode();
                if(responseCode.equals("00"))
                {
                    userResponseDTO.setMessage(VNPAYUtils.processResponseCodeStatusCode("00"));
                    userResponseDTO.setDetail("Thực hiện thanh toán thành công cho đơn hàng " + orderCode +"!");
                    userResponseDTO.setOrderCode(orderCode);
                    userResponseDTO.setPaymentStatus("Success");
                    userResponseDTO.setProccessTime(proccessTime);
                } else {
                    userResponseDTO.setMessage(VNPAYUtils.processResponseCodeStatusCode(responseCode));
                    userResponseDTO.setDetail("Thực hiện thanh toán thất bại cho đơn hàng "+ orderCode+"!");
                    userResponseDTO.setOrderCode(orderCode);
                    userResponseDTO.setPaymentStatus("Fail");
                    userResponseDTO.setProccessTime(proccessTime);
                }
            } else {
                userResponseDTO.setMessage(VNPAYUtils.processResponseCodeStatusCode("97"));
                userResponseDTO.setDetail("Đã xảy ra lỗi trong quá trình xử lý thanh toán với mã lỗi: 97" );
                userResponseDTO.setOrderCode(orderOptional.get().getOrderCode());
                userResponseDTO.setPaymentStatus("Fail");
                userResponseDTO.setProccessTime(proccessTime);
            }
            return userResponseDTO;
        } catch (Exception ex)
        {
            log.error("Return process error", ex);
            return UserResponseDTO.builder().message("Đã xảy ra lỗi trong quá trình thanh toán ")
                .detail(ex.getMessage())
                .paymentStatus("Fail").proccessTime(new Timestamp(System.currentTimeMillis()+7*60*60*1000L)).build();
        }
    }

    // process refund request
    public RefundResponseDTO doRefund(RefundRequestDTO refundRequestDTO, HttpServletRequest request)
    {
        try{
            System.out.println(request.getHeaderNames());
            StringBuilder query = new StringBuilder(VNPAYConsts.vnpayTransactionURL);
            query.append("?");
            String vnp_RequestId = VNPAYConfigs.getRandomNum(15);
            String vnp_Version ="2.1.0";
            String vnp_Command ="refund";
            String vnp_TmnCode = VNPAYConsts.vnp_tnmCode;
            String vnp_TransactionType = refundRequestDTO.getTransactionType();
            Optional<Transaction> transactionOp = transactionRepository.getByTransactionCode(refundRequestDTO.getTransactionCode());
            if(!transactionOp.isPresent() || transactionOp.get().getTransactionStatus() !=1 || transactionOp.get().getOrder().getOrderStatus()!=1)
                throw new RuntimeException("Transaction not found or not successfull transaction");
            String vnp_TxnRef = transactionOp.get().getOrder().getTransactionRef();
            if(refundRequestDTO.getAmount() > transactionOp.get().getAmount())
                throw new RuntimeException("Requested Refund Amount exceeded the transaction's amount");
            Long vnp_Amount = refundRequestDTO.getAmount()*100;
            String vnp_OrderInfo =refundRequestDTO.getRefundInfo();
            Long vnp_TransactionNo =transactionOp.get().getTransactionNo(); // can empty

            Calendar calendar =Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_TransactionDate = formatter.format(calendar.getTime());
            String vnp_CreateBy =refundRequestDTO.getRequestBy();
            String vnp_createDate = formatter.format(calendar.getTime());
            String vnp_IpAddr = VNPAYConfigs.getIPAddress(request);
            System.out.println(vnp_IpAddr);
            if(vnp_IpAddr ==null) throw new RuntimeException("Can't get request IP");

            // put to a map
            Map vnp_Params = new LinkedHashMap();
            vnp_Params.put("vnp_RequestId",vnp_RequestId);
            vnp_Params.put("vnp_Version",vnp_Version);
            vnp_Params.put("vnp_Command",vnp_Command);
            vnp_Params.put("vnp_TmnCode",vnp_TmnCode);
            vnp_Params.put("vnp_TransactionType",vnp_TransactionType);
            vnp_Params.put("vnp_TxnRef",vnp_TxnRef);
            vnp_Params.put("vnp_Amount",vnp_Amount);
            vnp_Params.put("vnp_TransactionNo",vnp_TransactionNo);
            vnp_Params.put("vnp_TransactionDate",vnp_TransactionDate);
            vnp_Params.put("vnp_CreateBy",vnp_CreateBy);
            vnp_Params.put("vnp_CreateDate",vnp_createDate);
            vnp_Params.put("vnp_IpAddr",vnp_IpAddr);
            vnp_Params.put("vnp_OrderInfo",vnp_OrderInfo);

            List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
            StringBuilder hashData = new StringBuilder();
            Iterator itr = fieldNames.iterator();
            while (itr.hasNext()){
                String fieldKey = (String) itr.next();
                String fieldVal = String.valueOf(vnp_Params.get(fieldKey));
                if(fieldKey!=null && StringUtils.hasText(fieldVal))
                {
                    query.append(fieldKey).append("=").append(URLEncoder.encode(fieldVal,StandardCharsets.US_ASCII.toString()));
                    hashData.append(fieldKey).append("=").append(URLEncoder.encode(fieldVal,StandardCharsets.US_ASCII.toString()));
                }
                if(itr.hasNext()){
                    query.append("&");
                    hashData.append("|");
                }
            }
            String vnp_SecureHash = VNPAYConfigs.hmacSHA512(VNPAYConsts.vnp_HashSecret,hashData.toString());
            query.append("&").append("vnp_SecureHash").append("=").append(vnp_SecureHash);
            return RefundResponseDTO.builder().requestURL(query.toString())
                    .createdTime(calendar.getTime())
                    .message("Yêu cầu hoàn tiền đã được tạo!").build();
        } catch (Exception ex)
        {
            log.error("Request refund error",ex);
            return RefundResponseDTO.builder()
                                    .message("Error in creating refund request")
                                    .requestURL(null)
                                    .createdTime(new Timestamp(System.currentTimeMillis()+7*60*60*1000L)).build();
        }
    }
}
