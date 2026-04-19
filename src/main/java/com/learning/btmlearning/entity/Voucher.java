package com.learning.btmlearning.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "vouchers")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Voucher {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(unique = true, nullable = false, length = 50)
    String code;

    @Column(nullable = false)
    Integer discountPercent;

    @Column(nullable = false)
    Integer quantity;

    @Column(nullable = false)
    @Builder.Default
    Integer usedCount = 0;

    LocalDateTime startDate;
    LocalDateTime expirationDate;

    @Column(nullable = false)
    @Builder.Default
    Boolean isActive = true;
}