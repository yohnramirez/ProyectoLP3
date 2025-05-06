package com.backend.project.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReportStatusHistoryDto {

    private Long id;

    private Long reportId;

    private String status;

    private LocalDateTime timestamp;
}
