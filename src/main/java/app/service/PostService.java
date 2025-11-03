package app.service;

import app.dto.PostResponse;
import app.exception.PostNotFoundException;
import app.exception.UnauthorizedException;
import app.model.GlobalPost;
import app.model.GlobalUsers;
import app.repository.GlobalCommentRepository;
import app.repository.GlobalLikeRepository;
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
public class PostService {

    private static final Logger logger = LoggerFactory.getLogger(PostService.class);
    private final GlobalPostRepository postRepository;
    private final GlobalLikeRepository likeRepository;  // Direct repo instead of service
    private final GlobalCommentRepository commentRepository;  // Direct repo instead of service

    /**
     * Create a new post
     */
    public GlobalPost createPost(GlobalUsers owner, String contentLink) {
        if (postRepository.existsByContentLink(contentLink)) {
            throw new IllegalArgumentException("Post with this content link already exists");
        }

        GlobalPost post = new GlobalPost(contentLink, owner);
        GlobalPost savedPost = postRepository.save(post);
        logger.info("Post created with ID: {} by user: {}", savedPost.getId(), owner.getId());
        return savedPost;
    }

    /**
     * Get post by ID
     */
    public GlobalPost getPostById(UUID postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + postId));
    }

    /**
     * Get all posts by a user
     */
    public Page<GlobalPost> getUserPosts(UUID userId, Pageable pageable) {
        return postRepository.findByPostOwnerId(userId, pageable);
    }

    /**
     * Get feed (all posts paginated)
     */
    public Page<GlobalPost> getFeed(Pageable pageable) {
        return postRepository.findAllForFeed(pageable);
    }

    /**
     * Update post (only owner can update)
     */
    public GlobalPost updatePost(UUID postId, GlobalUsers currentUser, String newContentLink) {
        GlobalPost post = getPostById(postId);

        // Check if current user is the owner
        if (!post.getPostOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only update your own posts");
        }

        // Check if new content link already exists
        if (!newContentLink.equals(post.getContentLink()) &&
                postRepository.existsByContentLink(newContentLink)) {
            throw new IllegalArgumentException("Post with this content link already exists");
        }

        post.setContentLink(newContentLink);
        GlobalPost updatedPost = postRepository.save(post);
        logger.info("Post {} updated by user: {}", postId, currentUser.getId());
        return updatedPost;
    }

    /**
     * Delete post (soft delete - only owner can delete)
     */
    public void deletePost(UUID postId, GlobalUsers currentUser) {
        GlobalPost post = getPostById(postId);

        // Check if current user is the owner
        if (!post.getPostOwner().getId().equals(currentUser.getId())) {
            throw new UnauthorizedException("You can only delete your own posts");
        }

        post.setDeleted(true);
        postRepository.save(post);
        logger.info("Post {} soft deleted by user: {}", postId, currentUser.getId());
    }

    /**
     * Get like count for a post (using repository directly)
     */
    public long getPostLikeCount(UUID postId) {
        return likeRepository.countByPostId(postId);
    }

    /**
     * Get comment count for a post (using repository directly)
     */
    public long getPostCommentCount(UUID postId) {
        return commentRepository.countByPostId(postId);
    }

    /**
     * Map GlobalPost to PostResponse DTO
     */
    public PostResponse mapPostToResponse(GlobalPost post) {
        return new PostResponse(
                post.getId(),
                post.getContentLink(),
                post.getPostOwner().getUsername(),
                post.getPostOwner().getFullName(),
                post.getPostOwner().getProfilePicture(),
                getPostLikeCount(post.getId()),
                getPostCommentCount(post.getId()),
                post.getCreatedAt(),
                post.getEditedAt()
        );
    }

    /**
     * Save post (for internal use)
     */
    public GlobalPost savePost(GlobalPost post) {
        return postRepository.save(post);
    }
}