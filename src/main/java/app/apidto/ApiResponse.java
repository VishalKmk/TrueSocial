package app.apidto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Generic DTO used to standardize all successful API responses.
 * Implements the structure: { status: "success", data: T }
 * * @param <T> The type of the payload data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    // The status is explicitly set to 'success' for successful responses
    private String status = "success";

    // The payload data, which can be any DTO or object (e.g., UserResponse, GlobalUsers)
    private T data;

    /**
     * Static factory method for creating a standardized successful response.
     * @param data The payload to be wrapped.
     * @param <T> The type of the payload.
     * @return A new ApiResponse instance.
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", data);
    }
}
