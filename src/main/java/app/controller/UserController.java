package app.controller;

import app.service.ImageUploadService;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import app.config.GlobalUserDetails;
import app.apidto.ApiErrorResponse;
import app.apidto.ApiResponse;
import app.dto.UserInfoRequest;
import app.dto.UserResponse;
import app.exception.UserAlreadyExistsException;
import app.exception.UserNotFoundException;
import app.model.GlobalUsers;
import app.service.UserService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * REST Controller for managing user accounts (profile retrieval and updates).
 * Base path: /api/user
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final ImageUploadService imageUploadService;

    /**
     * Endpoint for the authenticated user to retrieve their own profile information.
     * GET /api/user/me
     *
     * @param globalUserDetails The authenticated user's details, injected by Spring Security.
     * @return Standardized API Response wrapping the UserResponse DTO.
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getUserInfo(
            @AuthenticationPrincipal GlobalUserDetails globalUserDetails) {

        GlobalUsers user = globalUserDetails.getUser();

        UserResponse userResponse = userService.mapUserToResponse(user);

        // Wrap the response in the standardized API structure
        return ResponseEntity.ok(ApiResponse.success(userResponse));
    }

    /**
     * Endpoint for the authenticated user to update their own profile information.
     * PUT /api/user/me
     *
     * @param globalUserDetails The authenticated user's details.
     * @param request           DTO containing the fields to update.
     * @return Standardized API Response wrapping the updated GlobalUsers entity.
     */
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> updateUserInfo(
            @AuthenticationPrincipal GlobalUserDetails globalUserDetails,
            @Valid @RequestBody UserInfoRequest request) {

        GlobalUsers userToUpdate = globalUserDetails.getUser();

        GlobalUsers updatedUser = userService.updateUserInfo(userToUpdate, request);

        UserResponse userResponse = userService.mapUserToResponse(updatedUser);

        return ResponseEntity.ok(ApiResponse.success(userResponse));
    }

    /**
     * Endpoint for the authenticated user to soft-delete their own account.
     * DELETE /api/user/me
     *
     * @param globalUserDetails The authenticated user's details.
     * @return 204 No Content, wrapped in a 200 OK with an empty data structure.
     */
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Object>> deleteAccount(
            @AuthenticationPrincipal GlobalUserDetails globalUserDetails) {

        UUID userId = globalUserDetails.getUser().getId();

        userService.deleteUser(userId);

        // Success response for DELETE:
        // Returning 200 OK with success status and null data for structural consistency.
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PostMapping("/me/profile-picture")
    public ResponseEntity<ApiResponse<String>> uploadProfilePicture(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal GlobalUserDetails globalUserDetails) throws IOException {

        GlobalUsers user = globalUserDetails.getUser();

        // Upload to Cloudinary
        String imageUrl = imageUploadService.uploadImage(file, "profile-pictures");

        // Update user's profile picture
        user.setProfilePicture(imageUrl);
        userService.updateUserInfo(user, new UserInfoRequest());

        return ResponseEntity.ok(ApiResponse.success(imageUrl));
    }

    // --- EXCEPTION HANDLERS ---

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleUserNotFoundException(UserNotFoundException ex) {
        // Returns 404 NOT FOUND with a standardized error body
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiErrorResponse.failed(ex.getMessage()));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        // Returns 409 CONFLICT with a standardized error body
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiErrorResponse.failed(ex.getMessage()));
    }
}