package app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "global_like", indexes = {
        @Index(name = "idx_post_id", columnList = "post_id"),
        @Index(name = "idx_user_id", columnList = "user_id")
}, uniqueConstraints = {
        @UniqueConstraint(columnNames = {"post_id", "user_id"}, name = "uk_post_user_like")
})
public class GlobalLike {

    private static final Logger logger = LoggerFactory.getLogger(GlobalLike.class);

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private GlobalPost post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private GlobalUsers user;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant likedAt;

    // Constructor
    public GlobalLike(GlobalPost post, GlobalUsers user) {
        this.post = post;
        this.user = user;
    }

    @PostPersist
    protected void logCreation() {
        logger.info("Like created: User {} liked Post {}", user.getId(), post.getId());
    }
}