package com.learning.btmlearning.service.impl;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.learning.btmlearning.configuration.VNPayConfig;
import com.learning.btmlearning.constant.EnrollmentStatus;
import com.learning.btmlearning.constant.PaymentStatus;
import com.learning.btmlearning.entity.Course;
import com.learning.btmlearning.entity.Enrollment;
import com.learning.btmlearning.entity.Payment;
import com.learning.btmlearning.entity.User;
import com.learning.btmlearning.exception.AppException;
import com.learning.btmlearning.exception.ErrorCode;
import com.learning.btmlearning.repository.CourseRepository;
import com.learning.btmlearning.repository.EnrollmentRepository;
import com.learning.btmlearning.repository.PaymentRepository;
import com.learning.btmlearning.repository.UserRepository;
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
    UserRepository userRepository;
    PaymentRepository paymentRepository;
    EnrollmentRepository enrollmentRepository;
    SecurityUtil securityUtil;
    ObjectMapper objectMapper; // Dùng để chuyển Map thành JSON String

    @Value("${vnpay.tmn-code}") @NonFinal String tmnCode;
    @Getter
    @Value("${vnpay.hash-secret}") @NonFinal String secretKey;
    @Value("${vnpay.pay-url}") @NonFinal String vnpPayUrl;
    @Value("${vnpay.return-url}") @NonFinal String vnpReturnUrl;

    @Transactional
    public String createPaymentUrl(Long courseId, HttpServletRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new AppException(ErrorCode.COURSE_NOT_FOUND));

        User user = securityUtil.getCurrentUser();

        // 1. Kiểm tra xem đã mua chưa
        if (enrollmentRepository.existsByUserIdAndCourseId(user.getId(), courseId)) {
            throw new AppException(ErrorCode.YOU_ARE_OWN);
        }

        // 2. Tạo bản ghi Payment (PENDING) lưu vào DB trước
        Payment payment = Payment.builder()
                .user(user)
                .course(course)
                .amount(BigDecimal.valueOf(course.getPrice()))
                .currency("VND")
                .status(PaymentStatus.PENDING)
                .gateway("VNPAY")
                .build();
        payment = paymentRepository.save(payment);

        // 3. Build URL VNPay, dùng ID của Payment làm TxnRef
        long vnpAmount = (long) (course.getPrice() * 100);
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

        // Tạo chữ ký từ hàm đã sửa ở Bước 1
        String vnp_SecureHash = VNPayConfig.hashAllFields(vnp_Params, secretKey);

        // Ghép chữ ký vào URL cuối cùng
        String queryUrl = query.toString() + "&vnp_SecureHash=" + vnp_SecureHash;

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

        // Nếu giao dịch đã được xử lý (tránh trường hợp spam F5 Return URL)
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
            // Cập nhật trạng thái Payment thành công
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setTransactionId(transactionNo);
            payment.setPaidAt(LocalDateTime.now());
            paymentRepository.save(payment);

            // Sinh ra bản ghi Enrollment cho phép user học
            Enrollment enrollment = Enrollment.builder()
                    .user(payment.getUser())
                    .course(payment.getCourse())
                    .payment(payment)
                    .progressPercent(0.0f)
                    .status(EnrollmentStatus.ACTIVE)
                    .build();
            enrollmentRepository.save(enrollment);

            log.info(">>> Giao dịch {} THÀNH CÔNG. Đã cấp quyền học khóa {}", paymentId, payment.getCourse().getId());
            return true;
        } else {
            // Thanh toán thất bại hoặc bị hủy
            payment.setStatus(PaymentStatus.FAILED);
            payment.setTransactionId(transactionNo);
            paymentRepository.save(payment);

            log.info(">>> Giao dịch {} THẤT BẠI. Mã lỗi VNPay: {}", paymentId, responseCode);
            return false;
        }
    }
}