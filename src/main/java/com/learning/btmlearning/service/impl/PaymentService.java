package com.learning.btmlearning.service.impl;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.btmlearning.configuration.VNPayConfig;
import com.learning.btmlearning.constant.CourseStatus;
import com.learning.btmlearning.constant.EnrollmentStatus;
import com.learning.btmlearning.constant.PaymentStatus;
import com.learning.btmlearning.entity.*;
import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import com.learning.btmlearning.repository.CourseRepository;
import com.learning.btmlearning.repository.EnrollmentRepository;
import com.learning.btmlearning.repository.PaymentRepository;
import com.learning.btmlearning.repository.VoucherRepository;
import com.learning.btmlearning.utils.SecurityUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PaymentService {
    CourseRepository courseRepository;
    PaymentRepository paymentRepository;
    EnrollmentRepository enrollmentRepository;
    SecurityUtil securityUtil;
    ObjectMapper objectMapper;
    VoucherRepository voucherRepository;


    @Value("${vnpay.tmn-code}") @NonFinal String tmnCode;
    @Getter
    @Value("${vnpay.hash-secret}") @NonFinal String secretKey;
    @Value("${vnpay.pay-url}") @NonFinal String vnpPayUrl;
    @Value("${vnpay.return-url}") @NonFinal String vnpReturnUrl;
    @Value("${vnpay.frontend-return-url}") @NonFinal String frontendReturnUrl;

    public String getSecretKey() {
        return secretKey;
    }


    @Transactional
    public String createPaymentUrl(Long courseId, String voucherCode, HttpServletRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        if (course.getStatus() != CourseStatus.PUBLISHED && course.getStatus() != CourseStatus.ACTIVE) {
            throw new RuntimeException("Course is not published");
        }

        User user = securityUtil.getCurrentUser();

        if (enrollmentRepository.existsByUserIdAndCourseId(user.getId(), courseId)) {
            throw new AppException(ErrorCode.YOU_ARE_OWN);
        }

        BigDecimal finalAmount = course.getActualPrice();
        BigDecimal discountAmount = BigDecimal.ZERO;
        Voucher appliedVoucher = null ;

        if (voucherCode != null && !voucherCode.isEmpty()) {
            appliedVoucher = voucherRepository.findByCodeAndIsActiveTrue(voucherCode).orElseThrow(() -> new AppException(ErrorCode.VOUCHER_NOT_FOUND));
            discountAmount = finalAmount.multiply(BigDecimal.valueOf(appliedVoucher.getDiscountPercent())).divide(BigDecimal.valueOf(100));

            finalAmount = finalAmount.subtract(discountAmount);
        }

        Payment payment = Payment.builder()
                .user(user)
                .course(course)
                .amount(finalAmount)
                .voucher(appliedVoucher)
                .discountAmount(discountAmount)
                .currency("VND")
                .gateway(finalAmount.compareTo(BigDecimal.ZERO) == 0 ? "FREE" : "VNPAY")
                .status(finalAmount.compareTo(BigDecimal.ZERO) == 0 ? PaymentStatus.SUCCESS : PaymentStatus.PENDING)
                .paidAt(finalAmount.compareTo(BigDecimal.ZERO) == 0 ? LocalDateTime.now() : null)
                .build();

        payment = paymentRepository.save(payment);

        if (finalAmount.compareTo(BigDecimal.ZERO) == 0) {

            Enrollment enrollment = Enrollment.builder()
                    .user(user)
                    .course(course)
                    .payment(payment)
                    .progressPercent(0.0f)
                    .status(EnrollmentStatus.ACTIVE)
                    .build();
            enrollmentRepository.save(enrollment);

            if (appliedVoucher != null) {
                appliedVoucher.setUsedCount(appliedVoucher.getUsedCount() + 1);
                // voucherRepository.save(appliedVoucher);
            }

            log.info(">>> Khách hàng {} đã nhận khóa học {} MIỄN PHÍ thành công!", user.getEmail(), course.getTitle());
//            response.sendRedirect(frontendUrl + "?status=success&txnRef=" + txnRef);
            return frontendReturnUrl + "?status=success&txnRef=" + payment.getId() + "&isFree=true";
        }

        long vnpAmount = finalAmount
                .multiply(BigDecimal.valueOf(100))
                .longValue();

        String vnp_TxnRef = String.valueOf(payment.getId());

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", tmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(vnpAmount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan khoa hoc ID: " + course.getId());
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnpReturnUrl);
        vnp_Params.put("vnp_IpAddr", request.getRemoteAddr());

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));

        cld.add(Calendar.MINUTE, 15);
        vnp_Params.put("vnp_ExpireDate", formatter.format(cld.getTime()));


        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder query = new StringBuilder();
        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (!fieldValue.isEmpty())) {
                try {
                    // Encode cả Key và Value để ghép vào URL
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                } catch (Exception e) {
                    log.error("Lỗi mã hóa URL", e);
                }
                if (itr.hasNext()) {
                    query.append('&');
                }
            }
        }

        String vnp_SecureHash = VNPayConfig.hashAllFields(vnp_Params, secretKey);

        String queryUrl = query + "&vnp_SecureHash=" + vnp_SecureHash;

        return vnpPayUrl + "?" + queryUrl;
    }

    @Transactional
    public boolean processPaymentReturn(Map<String, String> vnpayParams) {
        String txnRef = vnpayParams.get("vnp_TxnRef");
        String responseCode = vnpayParams.get("vnp_ResponseCode");
        String transactionNo = vnpayParams.get("vnp_TransactionNo");

        Long paymentId = Long.parseLong(txnRef);
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy giao dịch hợp lệ"));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            return payment.getStatus() == PaymentStatus.SUCCESS;
        }

        try {
            // Lưu lại toàn bộ tham số VNPay trả về dưới dạng JSON
            payment.setGatewayResponse(objectMapper.writeValueAsString(vnpayParams));
        } catch (Exception e) {
            log.error("Lỗi khi parse JSON gateway response", e);
        }

        if ("00".equals(responseCode)) {
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId(transactionNo);
            payment.setPaidAt(LocalDateTime.now());
            paymentRepository.save(payment);

            Voucher voucher = payment.getVoucher();

            if (voucher != null) {
                voucher.setUsedCount(voucher.getUsedCount() + 1);
            }

            Enrollment enrollment = Enrollment.builder()
                    .user(payment.getUser())
                    .course(payment.getCourse())
                    .payment(payment)
                    .paymentStatus(PaymentStatus.SUCCESS)
                    .progressPercent(0.0f)
                    .status(EnrollmentStatus.ACTIVE)
                    .build();
            enrollmentRepository.save(enrollment);

            log.info(">>> Giao dịch {} THÀNH CÔNG. Đã cấp quyền học khóa {}", paymentId, payment.getCourse().getId());
            return true;
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setTransactionId(transactionNo);
            paymentRepository.save(payment);

            log.info(">>> Giao dịch {} THẤT BẠI. Mã lỗi VNPay: {}", paymentId, responseCode);
            return false;
        }
    }
}