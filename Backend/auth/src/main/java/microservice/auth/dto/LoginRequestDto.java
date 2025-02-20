package microservice.auth.dto;

import lombok.Data;

@Data
public class LoginRequestDto {

    public String email;

    public String password;
}
