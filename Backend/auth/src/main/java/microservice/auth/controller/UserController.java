package microservice.auth.controller;

import microservice.auth.dto.UserDto;
import microservice.auth.logic.UserLogic;
import microservice.auth.model.User;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/api/user")
public class UserController {

    /**
     * Instance of the logic layer
     */
    private final UserLogic userLogic;

    /**
     * Constructor
     * @param userLogic instance of the logic layer
     */
    public UserController(UserLogic userLogic) {
        this.userLogic = userLogic;
    }

    /**
     * Create user
     * @param data data info
     * @return UserDto
     */
    @PostMapping("/create")
    public ResponseEntity<UserDto> register(@RequestBody UserDto data) {
     UserDto createdUser = this.userLogic.registerUser(data);
     return ResponseEntity.ok(createdUser);
    }

    /**
     * Get all users
     * @return List of users
     */
    @GetMapping("/all")
    public ResponseEntity<List<UserDto>> getAll() {
        List<UserDto> users = this.userLogic.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Update a user
     * @return UserDto
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> update(@PathVariable Long id, @RequestBody UserDto updateData) {
        UserDto updateUser = this.userLogic.updateUser(id, updateData);
        return ResponseEntity.ok(updateUser);
    }

    /**
     * Delete a user
     * @return void 200 status code
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        this.userLogic.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
