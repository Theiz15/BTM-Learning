package com.learning.btmlearning.repository;

import com.learning.btmlearning.entity.AiChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiChatSessionRepository extends JpaRepository<AiChatSession, Long> {
    Optional<AiChatSession> findBySessionToken(String sessionToken);
}
