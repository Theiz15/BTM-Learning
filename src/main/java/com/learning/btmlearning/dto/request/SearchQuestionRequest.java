package com.learning.btmlearning.dto.request;

import com.learning.btmlearning.constant.Difficulty;
import lombok.Data;

@Data
public class SearchQuestionRequest {
    private String keyword;
    private Difficulty difficulty;
    private Boolean unassigned; // true = chỉ lấy câu hỏi chưa gán vào quiz nào
    private int pageNo = 0;
    private int pageSize = 10;
}
