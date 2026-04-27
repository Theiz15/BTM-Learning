package com.learning.btmlearning.entity;

import com.learning.btmlearning.constant.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voucher_id")
    Voucher voucher;

    @Column(nullable = false, precision = 12, scale = 2)
    BigDecimal amount;

    BigDecimal discountAmount;
    @Column(nullable = false, length = 10)
    String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    PaymentStatus status;

    @Column(nullable = false, length = 20)
    String gateway;

    @Column(name = "transaction_id")
    String transactionId; // Mã giao dịch do VNPay trả về (vnp_TransactionNo)

    @Column(name = "gateway_response", columnDefinition = "TEXT")
    String gatewayResponse; // Lưu toàn bộ JSON/Tham số VNPay trả về để đối soát

    @Column(name = "paid_at")
    LocalDateTime paidAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    LocalDateTime createdAt;
}