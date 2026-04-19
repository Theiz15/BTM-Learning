package com.learning.btmlearning.entity;

import com.learning.btmlearning.constant.Provider;
import com.learning.btmlearning.constant.UserRole;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User extends BaseEntity {
    @Column(unique = true, nullable = false)
    String email;

    String passwordHash;
    String fullName;
    String avatarUrl;

    String bio ;

    @Enumerated(EnumType.STRING)
    UserRole role;

    @Enumerated(EnumType.STRING)
    Provider provider;

    Boolean isActive = true;
}