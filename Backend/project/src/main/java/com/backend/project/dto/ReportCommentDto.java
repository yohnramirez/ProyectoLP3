package com.backend.project.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class ReportCommentDto {

    private Long id;

    private Long reportId;

    private String userId;

    private String comment;

    private LocalDateTime createdAt;
}
