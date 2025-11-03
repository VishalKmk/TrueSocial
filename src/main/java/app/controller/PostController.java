package app.controller;

import app.apidto.ApiErrorResponse;
import app.apidto.ApiResponse;
import app.config.GlobalUserDetails;
import app.dto.PostResponse;
import app.exception.PostNotFoundException;
import app.exception.UnauthorizedException;
import app.model.GlobalPost;
import app.model.GlobalUsers;
import app.service.ImageUploadService;
import app.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final ImageUploadService imageUploadService;

    /**
     * Create post with image upload
     */
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPostWithImage(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal GlobalUserDetails globalUserDetails) throws IOException {

        GlobalUsers currentUser = globalUserDetails.getUser();

        // Upload image to Cloudinary
        String imageUrl = imageUploadService.uploadImage(file, "posts");

        // Create post with the image URL
        GlobalPost createdPost = postService.createPost(currentUser, imageUrl);
        PostResponse postResponse = postService.mapPostToResponse(createdPost);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(postResponse));
    }

    /**
     * Get post by ID
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> getPost(@PathVariable UUID postId) {
        GlobalPost post = postService.getPostById(postId);
        PostResponse postResponse = postService.mapPostToResponse(post);
        return ResponseEntity.ok(ApiResponse.success(postResponse));
    }

    /**
     * Update post with new image
     */
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
            @PathVariable UUID postId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal GlobalUserDetails globalUserDetails) throws IOException {

        GlobalUsers currentUser = globalUserDetails.getUser();

        // Upload new image
        String imageUrl = imageUploadService.uploadImage(file, "posts");

        // Update post
        GlobalPost updatedPost = postService.updatePost(postId, currentUser, imageUrl);
        PostResponse postResponse = postService.mapPostToResponse(updatedPost);

        return ResponseEntity.ok(ApiResponse.success(postResponse));
    }

    /**
     * Delete post
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<Object>> deletePost(
            @PathVariable UUID postId,
            @AuthenticationPrincipal GlobalUserDetails globalUserDetails) {

        GlobalUsers currentUser = globalUserDetails.getUser();
        postService.deletePost(postId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handlePostNotFoundException(PostNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiErrorResponse.failed(ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiErrorResponse> handleUnauthorizedException(UnauthorizedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiErrorResponse.failed(ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.failed(ex.getMessage()));
    }
}
