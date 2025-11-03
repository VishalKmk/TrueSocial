package app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "global_posts", indexes = {
        @Index(name = "idx_post_owner", columnList = "post_owner"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
@Where(clause = "is_deleted = false")
public class GlobalPost {

    private static final Logger logger = LoggerFactory.getLogger(GlobalPost.class);

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, columnDefinition = "TEXT", length = 500)
    private String contentLink; // S3 or Cloudinary URL

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_owner", nullable = false)
    private GlobalUsers postOwner; // Who posted this

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = true)
    private Instant editedAt;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Version
    private Long version; // Optimistic locking

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        logger.debug("Post entity initialized: {}", id);
    }

    @PreUpdate
    protected void onUpdate() {
        editedAt = Instant.now();
    }

    @PostPersist
    protected void logCreation() {
        logger.info("Post created with ID: {} by user: {}", id, postOwner.getId());
    }

    @PostUpdate
    protected void logUpdate() {
        logger.info("Post ID {} successfully updated", id);
    }

    // Constructors
    public GlobalPost(String contentLink, GlobalUsers postOwner) {
        this.contentLink = contentLink;
        this.postOwner = postOwner;
    }

    public GlobalPost(UUID id, String contentLink, GlobalUsers postOwner) {
        this.id = id;
        this.contentLink = contentLink;
        this.postOwner = postOwner;
    }

    // Business logic methods
    public boolean isRecent() {
        return Instant.now().minus(Duration.ofDays(7)).isBefore(createdAt);
    }

    public boolean isEdited() {
        return editedAt != null;
    }
}