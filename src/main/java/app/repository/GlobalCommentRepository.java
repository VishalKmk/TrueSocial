package app.repository;

import app.model.GlobalComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GlobalCommentRepository extends JpaRepository<GlobalComment, UUID> {

    // Find all comments on a post (paginated)
    Page<GlobalComment> findByPostId(UUID postId, Pageable pageable);

    // Count comments on a post
    long countByPostId(UUID postId);

    // Find all comments by a user
    Page<GlobalComment> findByUserId(UUID userId, Pageable pageable);
}
