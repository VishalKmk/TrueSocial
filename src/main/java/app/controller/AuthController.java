package app.controller;

import app.dto.LoginResponse;
import app.dto.RegisterResponse;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import app.apidto.ApiErrorResponse;
import app.apidto.ApiResponse;
import app.dto.RegisterRequest;
import app.dto.LoginRequest;
import app.exception.InvalidCredentialsException;
import app.exception.UserAlreadyExistsException;
import app.service.AuthService;

/**
 * Controller for handling authentication-related operations such as user registration and login.
 */
@RestController
@RequestMapping("api/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * Constructor for AuthController.
     *
     * @param authService the authentication service to handle business logic
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Registers a new user.
     *
     * @param request the registration request containing user details
     * @return a ResponseEntity containing the created user and HTTP status 201 (Created)
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<RegisterResponse>> registerUser(@RequestBody @Valid RegisterRequest request) {
        authService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(new RegisterResponse("User registered successfully.")));
    }

    /**
     * Logs in an existing user.
     *
     * @param request the login request containing user credentials
     * @return a ResponseEntity containing a success message and HTTP status 200 (OK)
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> loginUser(@RequestBody @Valid LoginRequest request) {
        LoginResponse response = authService.loginUser(request);
        return ResponseEntity.ok(ApiResponse.success(response));
    }



    // --- EXCEPTION HANDLERS ---

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT) // 409
                .body(ApiErrorResponse.failed(ex.getMessage()));
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED) // 401
                .body(ApiErrorResponse.failed(ex.getMessage()));
    }
}
