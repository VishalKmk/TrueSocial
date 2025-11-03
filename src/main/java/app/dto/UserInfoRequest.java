package app.dto;

import lombok.Data;

@Data
public class UserInfoRequest {
    private String username;
    private String email;
    private String firstName;
    private String middleName;
    private String lastName;
}
