package com.backend.project.dto;

import lombok.Data;

@Data
public class ReportTypeDto {

    private Long id;

    private String name;

    private String description;

    private boolean state;
}
