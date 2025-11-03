package app.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import app.dto.UserInfoRequest;
import app.dto.UserResponse;
import app.exception.UserAlreadyExistsException;
import app.exception.UserNotFoundException;
import app.model.GlobalUsers;
import app.repository.GlobalUsersRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final GlobalUsersRepository globalUsersRepository;

    /**
     * Finds a user by their unique ID.
     * NOTE: This method is now primarily a utility for non-authenticated lookups
     * or internal logic (like deleteUser), NOT for the /api/user/me endpoint.
     *
     * @param id The UUID of the user.
     * @return The found GlobalUsers entity.
     * @throws UserNotFoundException if no user exists with the given ID.
     */
    public GlobalUsers findUserById(UUID id) {
        return globalUsersRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with ID: " + id));
    }

    public GlobalUsers findByUsername(String username) {
        return globalUsersRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Takes the full GlobalUsers entity (already loaded by Spring Security)
     * and maps it directly to the response DTO.
     * This method DOES NOT make a database call and should be used for the /api/user/me endpoint.
     *
     * @param user The preloaded GlobalUsers entity.
     * @return UserResponse DTO.
     */
    public UserResponse mapUserToResponse(GlobalUsers user) {
        // Uses the constructor from your UserResponse DTO to perform the mapping.
        return new UserResponse(user);
    }

    /**
     * Takes the existing user entity (already loaded from the security context) and applies changes directly.
     * This avoids the redundant 'select by id' query, but retains all necessary validation logic.
     *
     * @param userToUpdate The preloaded, managed GlobalUsers entity to update.
     * @param request The DTO containing the fields to update.
     * @return The updated GlobalUsers entity.
     * @throws UserAlreadyExistsException if the new username or email already belongs to another user.
     */
    @Transactional // Transactional to ensure data consistency
    public GlobalUsers updateUserInfo(GlobalUsers userToUpdate, UserInfoRequest request) {
        GlobalUsers user = userToUpdate;

        // 1. Validate and Update Unique Fields (Username)
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            // Check if another user already has this username
            if (globalUsersRepository.existsByUsername(request.getUsername())) {
                throw new UserAlreadyExistsException("Username '" + request.getUsername() + "' is already taken.");
            }
            user.setUsername(request.getUsername());
        }

        if (globalUsersRepository.existsByUsernameExcludingId(request.getUsername(), user.getId())) {
            throw new UserAlreadyExistsException("Username already taken.");
        }

        // 2. Validate and Update Unique Fields (Email)
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            // Check if another user already has this email
            if (globalUsersRepository.existsByEmail(request.getEmail())) {
                throw new UserAlreadyExistsException("Email '" + request.getEmail() + "' is already registered.");
            }
            user.setEmail(request.getEmail());
        }

        // 3. Update Name Fields (only if provided)
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getMiddleName() != null) {
            user.setMiddleName(request.getMiddleName());
        }

        return globalUsersRepository.save(user);
    }

    /**
     * Deletes a user by their unique ID.
     *
     * @param id The UUID of the user to delete.
     * @throws UserNotFoundException if the user to delete does not exist.
     */
    public void deleteUser(UUID id) {
        if (!globalUsersRepository.existsById(id)) {
            throw new UserNotFoundException("Cannot delete: User not found with ID: " + id);
        }
        globalUsersRepository.deleteById(id);
        System.out.println("LOG: User ID " + id + " successfully deleted.");
    }
}
