package app.repository;

import app.model.GlobalPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GlobalPostRepository extends JpaRepository<GlobalPost, UUID> {

    // Find all posts by a specific user
    List<GlobalPost> findByPostOwnerId(UUID userId);

    // Find posts by user with pagination
    Page<GlobalPost> findByPostOwnerId(UUID userId, Pageable pageable);

    // Find post by ID (excluding soft deleted)
    Optional<GlobalPost> findById(UUID id);

    // Check if content link exists
    boolean existsByContentLink(String contentLink);

    // Get all non-deleted posts for feed
    @Query("SELECT p FROM GlobalPost p WHERE p.isDeleted = false ORDER BY p.createdAt DESC")
    Page<GlobalPost> findAllForFeed(Pageable pageable);
}