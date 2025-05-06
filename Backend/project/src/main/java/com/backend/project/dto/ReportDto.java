package com.backend.project.dto;

import com.backend.project.util.enums.ReportStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReportDto {

    private long id;

    private long userId;

    private double latitude;

    private double longitude;

    private ReportTypeDto type;

    private String description;

    private String imageUrl;

    private String status;

    private boolean state;

    private LocalDateTime dateCreated;

    public String getStatus() {
        return status != null ? status : ReportStatus.PENDIENTE.getValue();
    }
}
