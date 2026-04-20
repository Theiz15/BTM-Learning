package com.learning.btmlearning.controller;

import com.learning.btmlearning.configuration.VNPayConfig;
import com.learning.btmlearning.service.impl.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("${api.prefix}/payments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentController {

    PaymentService paymentService;

    @Value("${vnpay.frontend-return-url}")
    @NonFinal
    String frontendUrl;

    @PostMapping("/create-url")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> createPaymentUrl(
            @RequestParam Long courseId,
            @RequestParam(required = false) String code,
            HttpServletRequest request
    ) {
        String paymentUrl = paymentService.createPaymentUrl(courseId,code ,request);
        Map<String, String> response = new HashMap<>();
        response.put("url", paymentUrl);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/vnpay-return")
    public void vnpayReturn(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> fields = new HashMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            fields.put(entry.getKey(), entry.getValue()[0]);
        }

        String vnp_SecureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");

        String signValue = VNPayConfig.hashAllFields(fields, paymentService.getSecretKey());
        String txnRef = request.getParameter("vnp_TxnRef");
        Long courseId = paymentService.getCourseIdByPaymentId(txnRef);

        if (signValue.equals(vnp_SecureHash)) {
            boolean isSuccess = paymentService.processPaymentReturn(fields);

            if (isSuccess) {
                response.sendRedirect(buildFrontendRedirect("success", txnRef, courseId, null));
            } else {
                response.sendRedirect(buildFrontendRedirect("failed", txnRef, courseId, null));
            }
        } else {
            response.sendRedirect(buildFrontendRedirect("invalid_signature", txnRef, courseId, "invalid_signature"));
        }
    }

    private String buildFrontendRedirect(String status, String txnRef, Long courseId, String reason) {
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(frontendUrl)
                .queryParam("status", status);

        if (txnRef != null && !txnRef.isBlank()) {
            builder.queryParam("txnRef", txnRef);
        }

        if (courseId != null) {
            builder.queryParam("courseId", courseId);
        }

        if (reason != null && !reason.isBlank()) {
            builder.queryParam("reason", reason);
        }

        return builder.build().toUriString();
    }
}
