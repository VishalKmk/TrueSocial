package app.dto;

import lombok.Data;
import app.model.GlobalUsers;

import java.time.Instant;

/**
 * DTO used for sending non-sensitive user profile information back to the client.
 */
@Data
public class UserResponse {
    private String profile_picture;
    private String username;
    private String email;
    private String fullName; // Calculated field
    private Instant createdAt;
    private Instant lastUpdatedAt;

    /**
     * Constructor to map essential, safe data from the entity to the DTO.
     * @param user The GlobalUsers entity.
     */
    public UserResponse(GlobalUsers user) {
        this.profile_picture = user.getProfilePicture();
        this.username = user.getUsername();
        this.email = user.getEmail();
        // Using existing logic in the entity to calculate the full name
        this.fullName = user.getFullName();
        this.createdAt = user.getCreatedAt();
        this.lastUpdatedAt = user.getLastUpdatedAt();
    }
}
