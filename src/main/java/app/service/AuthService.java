package app.service;

import app.dto.LoginRequest;
import app.dto.LoginResponse;
import app.dto.RegisterRequest;
import app.dto.RegisterResponse;
import app.exception.InvalidCredentialsException;
import app.exception.UserAlreadyExistsException;
import app.model.GlobalUsers;
import app.repository.GlobalUsersRepository;
import app.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final GlobalUsersRepository globalUsersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Register a new user
     */
    public RegisterResponse registerUser(RegisterRequest request) {
        if (globalUsersRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("Username already taken.");
        }
        if (globalUsersRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("Email already registered.");
        }

        String hashedPassword = passwordEncoder.encode(request.getPassword());

        GlobalUsers newUser = new GlobalUsers(
                request.getUsername(),
                request.getEmail(),
                hashedPassword,
                request.getFirstName(),
                request.getMiddleName(),
                request.getLastName()
        );

        globalUsersRepository.save(newUser);
        logger.info("User registered: {}", request.getUsername());
        return new RegisterResponse("User registered successfully.");
    }

    /**
     * Login user and return JWT token
     */
    public LoginResponse loginUser(LoginRequest request) {
        GlobalUsers user = globalUsersRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password."));

        if (user.isDeleted()) {
            throw new InvalidCredentialsException("Account has been deleted.");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password.");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());
        logger.info("User logged in: {}", request.getUsername());
        return new LoginResponse(token, user.getUsername());
    }
}