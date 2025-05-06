package com.backend.project.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "report_types")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private boolean state = true;

    public ReportType(String name, String description, boolean state) {
        this.name = name;
        this.description = description;
        this.state = state;
    }
}