package com.backend.project.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginRequestDto {

    private String email;

    private String password;
}
