package microservice.auth.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@Table(name = "users")
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private long idGender;

    @Column(nullable = false)
    private long idTypeDocument;

    @Column(nullable = false)
    private Long idRole;

    @Column(nullable = false, unique = true)
    private String numberDocument;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String emergencyContactPhone;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String birthDay;

    @Column(nullable = false)
    private boolean state = true;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Token> tokens;

    @CreationTimestamp
    @Column(nullable = false ,updatable = false)
    private LocalDateTime dateCreated;

    public User() {
    }
}
