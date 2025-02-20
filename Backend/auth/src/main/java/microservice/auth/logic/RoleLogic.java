package microservice.auth.logic;

import microservice.auth.abstraction.IRoleRepository;
import microservice.auth.abstraction.IUserRepository;
import microservice.auth.model.Role;
import microservice.auth.model.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleLogic {

    /**
     * Instance of the user repository layer
     */
    private final IUserRepository userRepository;

    /**
     * Instance of the role repository layer
     */
    private final IRoleRepository roleRepository;

    public RoleLogic(IUserRepository userRepository, IRoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Assign the rol to specific user
     */
    public void assignRoleUser(Long idUser, String roleName) {
        Optional<User> user = this.userRepository.findUserById(idUser);
        Optional<Role> roleToAssign = this.roleRepository.findByName(roleName);

        if (user.isPresent() && roleToAssign.isPresent()) {
            User userToAssign = user.get();
            userToAssign.setIdRole(roleToAssign.get().getId());

            this.userRepository.save(userToAssign);
        } else {
            throw new RuntimeException("Error assigning the role to the user with id " + idUser);
        }
    }

    /**
     * Remove the rol to specific user
     */
    public void removeRoleUser(Long idUser, String roleName) {
        Optional<User> user = this.userRepository.findUserById(idUser);
        Optional<Role> roleToAssign = this.roleRepository.findByName(roleName);

        if (user.isPresent() && roleToAssign.isPresent()) {
            User userToAssign = user.get();
            userToAssign.setIdRole(roleToAssign.get().getId());

            this.userRepository.save(userToAssign);
        } else {
            throw new RuntimeException("Error removing the role to the user with id " + idUser);
        }
    }
}
