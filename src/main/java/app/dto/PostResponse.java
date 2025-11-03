package app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PostResponse {
    private UUID id;
    private String contentLink;
    private String ownerUsername;
    private String ownerFullName;
    private String ownerProfilePicture;
    private long likeCount;
    private long commentCount;
    private Instant createdAt;
    private Instant editedAt;
}