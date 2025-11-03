package app.repository;

import app.model.GlobalLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GlobalLikeRepository extends JpaRepository<GlobalLike, UUID> {

    // Check if user already liked a post
    boolean existsByPostIdAndUserId(UUID postId, UUID userId);

    // Find like by post and user
    Optional<GlobalLike> findByPostIdAndUserId(UUID postId, UUID userId);

    // Count likes on a post
    long countByPostId(UUID postId);

    // Delete like by post and user
    void deleteByPostIdAndUserId(UUID postId, UUID userId);
}