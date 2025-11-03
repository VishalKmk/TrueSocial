package app.controller;

import app.apidto.ApiErrorResponse;
import app.apidto.ApiResponse;
import app.config.GlobalUserDetails;
import app.dto.LikeResponse;
import app.exception.LikeAlreadyExistsException;
import app.exception.LikeNotFoundException;
import app.exception.PostNotFoundException;
import app.model.GlobalUsers;
import app.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/like")
public class LikeController {

    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<ApiResponse<LikeResponse>> likePost(
            @PathVariable UUID postId,
            @AuthenticationPrincipal GlobalUserDetails globalUserDetails) {
        GlobalUsers currentUser = globalUserDetails.getUser();
        LikeResponse likeResponse = likeService.likePost(postId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(likeResponse));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse<LikeResponse>> unlikePost(
            @PathVariable UUID postId,
            @AuthenticationPrincipal GlobalUserDetails globalUserDetails) {
        GlobalUsers currentUser = globalUserDetails.getUser();
        LikeResponse likeResponse = likeService.unlikePost(postId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(likeResponse));
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handlePostNotFoundException(PostNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiErrorResponse.failed(ex.getMessage()));
    }

    @ExceptionHandler(LikeAlreadyExistsException.class)
    public ResponseEntity<ApiErrorResponse> handleLikeAlreadyExistsException(LikeAlreadyExistsException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiErrorResponse.failed(ex.getMessage()));
    }

    @ExceptionHandler(LikeNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleLikeNotFoundException(LikeNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiErrorResponse.failed(ex.getMessage()));
    }
}
