package app.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class DeleteUserRequest {
    private UUID id;
    private String username;
    private String password;
}
