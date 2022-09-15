package com.example.vnpaytest.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class VNPAYUtils {

    public static String generateTranAndOrderCode(Long id ,int length){
        try {
            StringBuilder sb = new StringBuilder();
            String tranIdString = String.valueOf(id);
            // let transaction code maximum length is 15
            for (int i = 0; i<length-tranIdString.length(); i++)
            {
                sb.append("0");
            }
            sb.append(tranIdString);
            return sb.toString();
        } catch (Exception ex)
        {
            log.error("generate Transaction Code error",ex);
            return null;
        }
    }


    // return message of transaction status code
    public String processResponseCodeStatusCode(String tranResponseCode)
    {
        String returnMessage = "";
            switch (tranResponseCode) {
                case "00" : {
                    returnMessage =  "Giao dịch thành công";
                    break;
                }
                case "07" : {
                    returnMessage = "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).";
                    break;
                }
                case "09" : {
                    returnMessage = "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng.";
                    break;
                }
                case "10" : {
                    returnMessage = "Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần";
                    break;
                }
                case "11" : {
                    returnMessage = "Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.";
                    break;
                }
                case "12" : {
                    returnMessage = "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa.";
                    break;
                }
                case "13" : {
                    returnMessage = "Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP). Xin quý khách vui lòng thực hiện lại giao dịch.";
                    break;
                }
                case "24" : {
                    returnMessage = "Giao dịch không thành công do: Khách hàng hủy giao dịch";
                    break;
                }
                case "51" : {
                    returnMessage = "Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch.";
                    break;
                }
                case "65" : {
                    returnMessage = "Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.";
                    break;
                }
                case "75" : {
                    returnMessage = "Ngân hàng thanh toán đang bảo trì.";
                    break;
                }
                case "79" : {
                    returnMessage = "Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định. Xin quý khách vui lòng thực hiện lại giao dịch.";
                    break;
                }
                case "97" : {
                    returnMessage = "Chữ ký không hợp lệ";
                    break;
                }
                case "99" : {
                    returnMessage = "Các lỗi khác (lỗi còn lại, không có trong danh sách mã lỗi đã liệt kê)";
                    break;
                }
                default: break;

            }
            return returnMessage;
    }
}
