package app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
    private String username;
    private String password;
    private String email;

    // Explicitly map the JSON key "firstname" to the Java field 'firstName'
    @JsonProperty("firstname")
    private String firstName;

    // Explicitly map the JSON key "lastname" to the Java field 'lastName'
    @JsonProperty("lastname")
    private String lastName;

    // Explicitly map the JSON key "middlename" to the Java field 'middleName'
    @JsonProperty("middlename")
    private String middleName;
}