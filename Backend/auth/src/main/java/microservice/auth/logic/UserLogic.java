package microservice.auth.logic;

import microservice.auth.abstraction.IUserRepository;
import microservice.auth.dto.UserDto;
import microservice.auth.mapper.IUserMapper;
import microservice.auth.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserLogic {

    /**
     * Instance of the repository layer
     */
    private final IUserRepository userRepository;

    /**
     * Implementation of the user mapper
     */
    @Autowired
    private final IUserMapper userMapper;

    /**
     * Constructor
     * @param userRepository instance of the logic layer
     * @param userMapper instance of the repository layer
     */
    @Autowired
    public UserLogic(IUserRepository userRepository, IUserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    /**
     * Create a user
     * @param data data info
     * @return UserDto
     */
    public UserDto registerUser(UserDto data) {
        User savedUser = this.userMapper.toEntity(data);
        savedUser = this.userRepository.save(savedUser);

        return this.userMapper.toDto(savedUser);
    }

    /**
     * Get all users
     * @return List of users
     */
    public List<UserDto> getAllUsers() {
        List<User> listUsers = this.userRepository.findAll();
        return this.userMapper.toUserDtoList(listUsers);
    }

    /**
     * Update a user
     * @return UserDto
     */
    public UserDto updateUser(Long id, UserDto updateData) {
        Optional<User> userToUpdate = this.userRepository.findUserById(id);

        if (userToUpdate.isPresent()) {
            User user = this.setUpdateUserData(userToUpdate.get(), updateData);

            // Validate users with same email
            if (!user.getEmail().equals(updateData.getEmail())) {
                // Search if exists a user with the same email
                Optional<User> existSameEmail = this.userRepository.findUserByEmail(updateData.getEmail());

                // Validates if it is the same user
                if (existSameEmail.isPresent() && existSameEmail.get().getId() != id) {
                    throw new RuntimeException("Email is already in use.");
                }

                // Sett the email
                user.setEmail(updateData.getEmail());
            }

            // Validate users with same number document
            if (!user.getNumberDocument().equals(updateData.getNumberDocument())) {
                // Search if exists a user with the same number document
                Optional<User> existSameDocument = this.userRepository.findUserByNumberDocument(updateData.getNumberDocument());

                // Validates if it is the same user
                if (existSameDocument.isPresent() && existSameDocument.get().getId() != id) {
                    throw new RuntimeException("Number document is already in use.");
                }

                // Sett the email
                user.setNumberDocument(updateData.getNumberDocument());
            }

            // Convert to dto and return the response
            return this.userMapper.toDto(this.userRepository.save(user));
        } else {
            throw new RuntimeException("User not found with id " + id);
        }
    }

    /**
     * Delete a user
     */
    public void deleteUser(Long id) {
        Optional<User> userToDelete = this.userRepository.findUserById(id);

        if (userToDelete.isPresent()) {
            this.userRepository.deleteById(id);
        } else {
            throw new RuntimeException("User not found with id " + id);
        }
    }

    /**
     * Set the data user to update
     * @return User entity
     */
    public User setUpdateUserData(User userToUpdate, UserDto updateData) {
        userToUpdate.setIdGender(updateData.getIdGender());
        userToUpdate.setIdTypeDocument(updateData.getIdTypeDocument());
        userToUpdate.setIdRole(updateData.getIdRole());
        userToUpdate.setFirstName(updateData.getFirstName());
        userToUpdate.setLastName(updateData.getLastName());
        userToUpdate.setPassword(updateData.getPassword());
        userToUpdate.setPhoneNumber(updateData.getPhoneNumber());
        userToUpdate.setEmergencyContactPhone(updateData.getEmergencyContactPhone());
        userToUpdate.setAddress(updateData.getAddress());
        userToUpdate.setBirthDay(updateData.getBirthDay());

        return userToUpdate;
    }
}