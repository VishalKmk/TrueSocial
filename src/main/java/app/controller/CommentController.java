package app.controller;

import app.apidto.ApiErrorResponse;
import app.apidto.ApiResponse;
import app.config.GlobalUserDetails;
import app.dto.CommentCreateRequest;
import app.dto.CommentResponse;
import app.exception.CommentNotFoundException;
import app.exception.PostNotFoundException;
import app.exception.UnauthorizedException;
import app.model.GlobalComment;
import app.model.GlobalUsers;
import app.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @PathVariable UUID postId,
            @AuthenticationPrincipal GlobalUserDetails globalUserDetails,
            @Valid @RequestBody CommentCreateRequest request) {
        GlobalUsers currentUser = globalUserDetails.getUser();
        GlobalComment createdComment = commentService.createComment(postId, currentUser, request);
        CommentResponse commentResponse = commentService.mapCommentToResponse(createdComment);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(commentResponse));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getCommentsForPost(
            @PathVariable UUID postId,
            Pageable pageable) {
        Page<GlobalComment> comments = commentService.getPostComments(postId, pageable);
        List<CommentResponse> commentResponses = comments.stream()
                .map(commentService::mapCommentToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(commentResponses));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Object>> deleteComment(
            @PathVariable UUID postId,
            @PathVariable UUID commentId,
            @AuthenticationPrincipal GlobalUserDetails globalUserDetails) {
        GlobalUsers currentUser = globalUserDetails.getUser();
        commentService.deleteComment(commentId, currentUser);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable UUID postId,
            @PathVariable UUID commentId,
            @AuthenticationPrincipal GlobalUserDetails globalUserDetails,
            @Valid @RequestBody CommentCreateRequest request) {
        GlobalUsers currentUser = globalUserDetails.getUser();
        GlobalComment updatedComment = commentService.updateComment(commentId, currentUser, request);
        CommentResponse commentResponse = commentService.mapCommentToResponse(updatedComment);
        return ResponseEntity.ok(ApiResponse.success(commentResponse));
    }

    @ExceptionHandler(CommentNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleCommentNotFoundException(CommentNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiErrorResponse.failed(ex.getMessage()));
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
}
