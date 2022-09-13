package com.example.vnpaytest.configurations;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import com.example.vnpaytest.constants.VNPAYConsts;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VNPAYConfigs {

    private static final String[] IP_HEADERS = {
        "X-FORWARDED-FOR",
        "Proxy-Client-IP",
        "WL-Proxy-Client-IP",
        "HTTP_X_FORWARDED_FOR",
        "HTTP_X_FORWARDED",
        "HTTP_X_CLUSTER_CLIENT_IP",
        "HTTP_CLIENT_IP",
        "HTTP_FORWARDED_FOR",
        "HTTP_FORWARDED",
        "HTTP_VIA",
        "REMOTE_ADDR"
    };


    // generate a md5 hash code
    public static  String md5(String message)
    {
        String digest =null;
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte [] hash = md.digest(message.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(2*hash.length);
            for(byte b : hash)
            {
                sb.append(String.format("%02x",b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex)
        {
            log.error("Generate MD5 error",ex);
            digest="";
            return digest;
        }
    }


    // generate a sha256 hash code
    public static String sha256(String message)
    {
        String digest = null;
        try{
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte [] hash = md.digest(message.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(hash.length*2);
            for (byte b : hash)
            {
                sb.append(String.format("%02x",b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex)
        {
            log.error("Generate SHA256",ex);
            digest = "";
            return digest;
        }
    }

    // generate a hase using hmac sha512
    public static String hmacSHA512(String key, String data) // key truyền vào vnp_hashSecret,
    {
        try{
            if(key==null || data ==null)
                throw new NullPointerException();
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] keyBytes = key.getBytes();
            SecretKeySpec keySpec = new SecretKeySpec(keyBytes,"HmacSHA512");
            hmac512.init(keySpec);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2*result.length);
            for(byte b : result)
            {
                sb.append(String.format("%02x",b & 0xff));
            }
            return sb.toString();
        } catch (Exception ex)
        {
            log.error("Generate hmac HmacSHA512 error",ex);
            return "";
        }
    }

    // Unitly for hashing in VNPAY
    public static String hashAllFields (Map fields)
    {
        try{
            // need complete
            List<String> fieldNames = new ArrayList<>(fields.keySet());
            Collections.sort(fieldNames);
            // create a string builder
            StringBuilder sb = new StringBuilder();
            Iterator itr = fieldNames.iterator();
            while (itr.hasNext())
            {
                String fieldNameKey = (String) itr.next();
                String fieldValue = (String)fields.get(fieldNameKey);
                if(fieldNameKey !=null && StringUtils.hasText(fieldValue))
                    sb.append(fieldNameKey).append("=").append(fieldValue);
                if(itr.hasNext()) sb.append("&");
            }
            return hmacSHA512(VNPAYConsts.vnp_HashSecret,sb.toString());
        } catch (Exception ex)
        {
            log.error("Hash all fields error",ex);
            return "";
        }
    }

    //get ip address send request
    public static String getIPAddress(HttpServletRequest request)
    {
        try{
            String IP = null;
            for(String header : IP_HEADERS)
            {
                String ipTemp = request.getHeader(header);
                if(ipTemp==null || ipTemp.isEmpty())
                    continue;
                else IP = ipTemp;
            }
            if (IP == null) IP = request.getRemoteAddr();
            return IP;
        } catch (Exception ex)
        {
            log.error("IP is invalid: " ,ex);
            return null;
        }
    }

    public static  String getRandomNum(int length)
    {
        Random rad = new Random();
        StringBuilder sb = new StringBuilder(length);
        for(int i= 0; i<length;i++)
        {
            sb.append(Math.abs(rad.nextInt(9)));
        }
        return sb.toString();
    }


}
