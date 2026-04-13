package com.learning.btmlearning.controller;

import com.learning.btmlearning.configuration.VNPayConfig;
import com.learning.btmlearning.service.impl.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {

    PaymentService paymentService;

//    @Value("${frontend.url:http://localhost:3000}")
//    private String frontendUrl;

    @PostMapping("/create-url")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> createPaymentUrl(@RequestParam Long courseId, HttpServletRequest request) {
        String paymentUrl = paymentService.createPaymentUrl(courseId, request);
        Map<String, String> response = new HashMap<>();
        response.put("url", paymentUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vnpay-return")
    public ResponseEntity<?> vnpayReturn(HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            fields.put(entry.getKey(), entry.getValue()[0]);
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        String signValue = VNPayConfig.hashAllFields(fields, paymentService.getSecretKey());
        String txnRef = request.getParameter("vnp_TxnRef");

        if (signValue.equals(vnp_SecureHash)) {
            boolean isSuccess = paymentService.processPaymentReturn(fields);

            if (isSuccess) {
                // TRẢ VỀ TEXT TRỰC TIẾP LÊN TRÌNH DUYỆT
                return ResponseEntity.ok("🎉 CHÚC MỪNG! Thanh toán thành công cho mã đơn hàng: " + txnRef +
                        ". Bạn có thể kiểm tra bảng payments và enrollments trong Database.");
            } else {
                return ResponseEntity.badRequest().body("❌ THANH TOÁN THẤT BẠI hoặc ĐÃ BỊ HỦY cho mã đơn hàng: " + txnRef);
            }
        } else {
            return ResponseEntity.badRequest().body("⚠️ LỖI BẢO MẬT: Sai chữ ký VNPay!");
        }
    }
}
