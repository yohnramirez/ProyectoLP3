package microservice.auth.dto;

import lombok.Data;
import microservice.auth.model.Role;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserDto {

    private long id;

    private long idGender;

    private long idTypeDocument;

    private Set<Role> roles;

    private String numberDocument;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String phoneNumber;

    private String emergencyContactPhone;

    private String address;

    private String birthDay;

    private boolean state = true;

    private LocalDateTime dateCreated;
}
