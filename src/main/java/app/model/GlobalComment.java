package app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "global_comments", indexes = {
        @Index(name = "idx_post_id", columnList = "post_id"),
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_created_at", columnList = "created_at")
})
public class GlobalComment {

    private static final Logger logger = LoggerFactory.getLogger(GlobalComment.class);

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private GlobalPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private GlobalUsers user;

    @Column(nullable = true, columnDefinition = "TEXT", length = 1000)
    private String comment;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = true)
    private Instant editedAt;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Version
    private Long version; // Optimistic locking for concurrent updates

    // Constructors
    public GlobalComment(GlobalPost post, GlobalUsers user, String comment) {
        this.post = post;
        this.user = user;
        this.comment = comment;
    }

    @PostPersist
    protected void logCreation() {
        logger.info("Comment created with ID: {} by user: {} on post: {}",
                id, user.getId(), post.getId());
    }

    @PostUpdate
    protected void logUpdate() {
        logger.info("Comment ID {} successfully updated", id);
    }

    // Business logic
    public boolean isEdited() {
        return editedAt != null;
    }
}