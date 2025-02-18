package microservice.auth.controller;

import microservice.auth.logic.RoleLogic;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/api/role")
public class RoleController {

    /**
     * Instance of the role logic layer
     */
    private final RoleLogic roleLogic;

    /**
     * Constructor
     * @param roleLogic instance of the role logic layer
     */
    public RoleController(RoleLogic roleLogic) {
        this.roleLogic = roleLogic;
    }

    /**
     * Assign Role
     * @param idUser Id User
     * @param roleName role name
     * @return UserDto
     */
    @PostMapping("/assign")
    public ResponseEntity<String> assignRole(@RequestParam Long idUser, @RequestParam String roleName) {
        this.roleLogic.assignRoleUser(idUser, roleName);
        return ResponseEntity.ok("Role assigned successfully to user " + idUser);
    }

    /**
     * Remove Role
     * @param idUser Id User
     * @param roleName role name
     * @return UserDto
     */
    @PostMapping("/remove")
    public ResponseEntity<String> removeRole(@RequestParam Long idUser, @RequestParam String roleName) {
        this.roleLogic.removeRoleUser(idUser, roleName);
        return ResponseEntity.ok("Role removed successfully to user " + idUser);
    }
}
