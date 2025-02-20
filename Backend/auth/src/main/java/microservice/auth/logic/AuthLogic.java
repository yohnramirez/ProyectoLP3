package microservice.auth.logic;

import microservice.auth.abstraction.ITokenRepository;
import microservice.auth.abstraction.IUserRepository;
import microservice.auth.dto.LoginRequestDto;
import microservice.auth.dto.TokenResponseDto;
import microservice.auth.dto.UserDto;
import microservice.auth.mapper.IUserMapper;
import microservice.auth.model.Token;
import microservice.auth.model.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthLogic {

    private final ITokenRepository tokenRepository;

    private final IUserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final IUserMapper userMapper;

    private final JwtLogic jwtLogic;

    private final AuthenticationManager authenticationManager;

    public AuthLogic(ITokenRepository tokenRepository, IUserRepository userRepository, PasswordEncoder passwordEncoder, IUserMapper userMapper, JwtLogic jwtLogic, AuthenticationManager authenticationManager) {
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.jwtLogic = jwtLogic;
        this.authenticationManager = authenticationManager;
    }

    public TokenResponseDto registerUser(UserDto user) {
        var userToRegister = UserDto.builder()
                .email(user.getEmail())
                .address(user.getAddress())
                .birthDay(user.getBirthDay())
                .idGender(user.getIdGender())
                .idTypeDocument(user.getIdTypeDocument())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .idRole(user.getIdRole())
                .password(this.passwordEncoder.encode(user.getPassword()))
                .phoneNumber(user.getPhoneNumber())
                .emergencyContactPhone(user.getEmergencyContactPhone())
                .numberDocument(user.getNumberDocument())
                .state(true)
                .build();

        var convertToEntity = this.userMapper.toEntity(userToRegister);
        var savedUser = this.userRepository.save(convertToEntity);

        var token = this.jwtLogic.generateToken(savedUser);
        var refreshToken = this.jwtLogic.generateRefreshToken(savedUser);

        saveUserToken(savedUser, token);

        return new TokenResponseDto(token, refreshToken);
    }

    public TokenResponseDto loginUser(LoginRequestDto request) {
        this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = this.userRepository.findUserByEmail(request.email).orElseThrow();
        var jwtToken = this.jwtLogic.generateToken(user);
        var refreshToken = this.jwtLogic.generateRefreshToken(user);

        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        return new TokenResponseDto(jwtToken, refreshToken);
    }

    public void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(Token.TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();

        this.tokenRepository.save(token);
    }

    public TokenResponseDto refreshToken(@NotNull final String authHeader) {
        if (!authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid bearer token");
        }

        final String refreshToken = authHeader.substring(7);
        final String userEmail = this.jwtLogic.extractUsername(refreshToken);

        if (userEmail == null) {
            return null;
        }

        final User user = this.userRepository.findUserByEmail(userEmail).orElseThrow();
        final boolean isTokenValid = this.jwtLogic.isTokenValid(refreshToken, user);

        if (!isTokenValid) {
            return null;
        }

        final String accessToken = this.jwtLogic.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, accessToken);

        return new TokenResponseDto(accessToken, refreshToken);
    }

    private void revokeAllUserTokens(final User user) {
        final List<Token> validUserTokens = this.tokenRepository
                .findAllValidTokenByUser(user.getId());

        if (!validUserTokens.isEmpty()) {
            validUserTokens.forEach(token -> {
                token.setExpired(true);
                token.setRevoked(true);
            });

            this.tokenRepository.saveAll(validUserTokens);
        }
    }
}
