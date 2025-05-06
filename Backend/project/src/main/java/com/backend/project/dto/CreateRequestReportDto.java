package com.backend.project.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateRequestReportDto {

    private long userId;

    private double latitude;

    private double longitude;

    private long typeId;

    private String description;

    private String imageUrl;
}
