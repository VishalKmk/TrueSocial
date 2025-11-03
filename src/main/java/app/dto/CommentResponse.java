package app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CommentResponse {
    private UUID id;
    private String comment;
    private String userUsername;
    private String userFullName;
    private String userProfilePicture;
    private Instant createdAt;
    private Instant editedAt;
}