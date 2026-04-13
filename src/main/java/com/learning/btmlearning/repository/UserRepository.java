package com.learning.btmlearning.repository;

import com.learning.btmlearning.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    // Dùng sau cho OAuth2 Google
//    Optional<User> findByProviderAndProviderId(String provider, String providerId);
}
