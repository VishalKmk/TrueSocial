package app.service;

import app.dto.CommentCreateRequest;
import app.dto.CommentResponse;
import app.exception.CommentNotFoundException;
import app.exception.UnauthorizedException;
import app.model.GlobalComment;
import app.model.GlobalPost;
import app.model.GlobalUsers;
import app.repository.GlobalCommentRepository;
import app.repository.GlobalPostRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    private final GlobalCommentRepository commentRepository;
    private final GlobalPostRepository postRepository;
    private final PostService postService;

    /**
     * Create a comment on a post
     */
    public GlobalComment createComment(UUID postId, GlobalUsers user, CommentCreateRequest request) {
        GlobalPost post = postService.getPostById(postId);
        GlobalComment comment = new GlobalComment(post, user, request.getComment());
        GlobalComment savedComment = commentRepository.save(comment);

        logger.info("User {} commented on post {}", user.getId(), postId);
        return savedComment;
    }

    /**
     * Get comment by ID
     */
    public GlobalComment getCommentById(UUID commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment not found with ID: " + commentId));
    }

    /**
     * Get all comments on a post (paginated)
     */
    public Page<GlobalComment> getPostComments(UUID postId, Pageable pageable) {
        postService.getPostById(postId); // Verify post exists
        return commentRepository.findByPostId(postId, pageable);
    }

    /**
     * Get all comments by a user
     */
    public Page<GlobalComment> getUserComments(UUID userId, Pageable pageable) {
        return commentRepository.findByUserId(userId, pageable);
    }

    /**
     * Get comment count on a post
     */
    public long getCommentCount(UUID postId) {
        return commentRepository.countByPostId(postId);
    }

    /**
     * Update a comment (only owner can update)
     */
    public GlobalComment updateComment(UUID commentId, GlobalUsers currentUser, CommentCreateRequest request) {
        GlobalComment comment = getCommentById(commentId);

        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only update your own comments");
        }

        comment.setComment(request.getComment());
        GlobalComment updatedComment = commentRepository.save(comment);
        logger.info("Comment {} updated by user {}", commentId, currentUser.getId());
        return updatedComment;
    }

    /**
     * Delete a comment (soft delete - only owner can delete)
     */
    public void deleteComment(UUID commentId, GlobalUsers currentUser) {
        GlobalComment comment = getCommentById(commentId);

        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only delete your own comments");
        }

        comment.setDeleted(true);
        commentRepository.save(comment);
        logger.info("Comment {} soft deleted by user {}", commentId, currentUser.getId());
    }

    /**
     * Map GlobalComment to CommentResponse DTO
     */
    public CommentResponse mapCommentToResponse(GlobalComment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getComment(),
                comment.getUser().getUsername(),
                comment.getUser().getFullName(),
                comment.getUser().getProfilePicture(),
                comment.getCreatedAt(),
                comment.getEditedAt()
        );
    }
}