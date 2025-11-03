package app.service;

import app.dto.LikeResponse;
import app.exception.LikeAlreadyExistsException;
import app.exception.LikeNotFoundException;
import app.exception.PostNotFoundException;
import app.model.GlobalLike;
import app.model.GlobalPost;
import app.model.GlobalUsers;
import app.repository.GlobalLikeRepository;
import app.repository.GlobalPostRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private static final Logger logger = LoggerFactory.getLogger(LikeService.class);
    private final GlobalLikeRepository likeRepository;
    private final GlobalPostRepository postRepository;  // Direct repo, not service

    /**
     * Like a post
     */
    public LikeResponse likePost(UUID postId, GlobalUsers user) {
        // Verify post exists without using PostService
        GlobalPost post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("Post not found with ID: " + postId));

        if (likeRepository.existsByPostIdAndUserId(postId, user.getId())) {
            throw new LikeAlreadyExistsException("User has already liked this post");
        }

        GlobalLike like = new GlobalLike(post, user);
        likeRepository.save(like);
        long likeCount = likeRepository.countByPostId(postId);

        logger.info("User {} liked post {}", user.getId(), postId);
        return new LikeResponse(true, likeCount);
    }

    /**
     * Unlike a post
     */
    public LikeResponse unlikePost(UUID postId, GlobalUsers user) {
        GlobalLike like = likeRepository.findByPostIdAndUserId(postId, user.getId())
                .orElseThrow(() -> new LikeNotFoundException("Like not found"));

        likeRepository.delete(like);
        long likeCount = likeRepository.countByPostId(postId);

        logger.info("User {} unliked post {}", user.getId(), postId);
        return new LikeResponse(false, likeCount);
    }

    /**
     * Get like count for a post
     */
    public long getLikeCountForPost(UUID postId) {
        return likeRepository.countByPostId(postId);
    }

    /**
     * Check if user liked a post
     */
    public boolean hasUserLikedPost(UUID postId, UUID userId) {
        return likeRepository.existsByPostIdAndUserId(postId, userId);
    }

    /**
     * Toggle like (like if not liked, unlike if liked)
     */
    public boolean toggleLike(UUID postId, GlobalUsers user) {
        if (likeRepository.existsByPostIdAndUserId(postId, user.getId())) {
            unlikePost(postId, user);
            return false;
        } else {
            likePost(postId, user);
            return true;
        }
    }
}