package com.backend.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportBroadcastDto {

    private Long id;

    private double latitude;

    private double longitude;

    private String type;

    private String description;

    private LocalDateTime dateCreated;
}
