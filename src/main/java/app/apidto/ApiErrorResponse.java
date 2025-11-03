package app.apidto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {

    private String status = "failed";
    private String message;
    private Object data = null;

    public static ApiErrorResponse failed(String message) {
        return new ApiErrorResponse("failed", message, null);
    }
}