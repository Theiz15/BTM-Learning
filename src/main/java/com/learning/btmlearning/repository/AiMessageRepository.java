package com.learning.btmlearning.repository;

import com.learning.btmlearning.entity.AiMessage;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AiMessageRepository extends CrudRepository<AiMessage, Long> {
    // Lấy 20 tin nhắn gần nhất của 1 session, sắp xếp mới nhất lên đầu
    List<AiMessage> findTop20BySessionIdOrderByCreatedAtDesc(Long sessionId);
}
