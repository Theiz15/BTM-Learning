package com.learning.btmlearning.repository;

import com.learning.btmlearning.constant.UserRole;
import com.learning.btmlearning.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends org.springframework.data.jpa.repository.JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // Dùng sau cho OAuth2 Google
//    Optional<User> findByProviderAndProviderId(String provider, String providerId);
    @Query("SELECT u FROM User u WHERE "+
            "(:role IS NULL OR u.role = :role) AND " +
            "(:isActive IS NULL OR u.isActive=:isActive) AND " +
            "(:keyword IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "                  OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')))"

    )
    Page<User> searchUsers(@Param("role")UserRole role ,
                           @Param("isActive") Boolean isActive ,
                           @Param("keyword") String keyword,
                           Pageable pageable
                           );

    List<User> findByIsActiveTrue();

    List<User> findByRoleAndIsActiveTrue(UserRole role);
}
