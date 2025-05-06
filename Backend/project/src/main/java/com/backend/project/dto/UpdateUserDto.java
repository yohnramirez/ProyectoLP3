package com.backend.project.dto;

import lombok.Data;

@Data
public class UpdateUserDto {

    private String address;

    private String birthday;

    private String country;

    private String typeDocument;

    private String numberDocument;
}
