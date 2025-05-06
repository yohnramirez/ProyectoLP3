package com.backend.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RegisterRequestDto {

    private String fullName;

    private String email;

    private String password;

    private String address;

    private String city;

    private String country;

    private String birthday;

    private String numberDocument;

    private String typeDocument;

    private String gender;
}
