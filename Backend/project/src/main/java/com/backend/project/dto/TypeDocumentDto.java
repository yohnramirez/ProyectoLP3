package com.backend.project.dto;

import lombok.Data;

@Data
public class TypeDocumentDto {

    private Long id;

    private String name;

    private String mask;

    private boolean state;
}