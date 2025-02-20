package microservice.auth.dto;

import lombok.Builder;
import lombok.Data;
import microservice.auth.model.Role;
import microservice.auth.model.Token;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class UserDto {

    private long id;

    private long idGender;

    private long idTypeDocument;

    private Long idRole;

    private String numberDocument;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String phoneNumber;

    private String emergencyContactPhone;

    private String address;

    private String birthDay;

    private List<Token> tokens;

    private boolean state = true;

    private LocalDateTime dateCreated;
}
