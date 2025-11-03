package app.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Where;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "global_users", indexes = {
        @Index(name = "idx_username", columnList = "username"),
        @Index(name = "idx_email", columnList = "email")
})
@Where(clause = "is_deleted = false")
public class GlobalUsers {

    private static final Logger logger = LoggerFactory.getLogger(GlobalUsers.class);

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, name = "username")
    private String username;

    @Column(nullable = false, unique = true, name = "email")
    private String email;

    @Column(nullable = false)
    private String password; // Store hashed password, never plain text

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = true)
    private String middleName;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = true, columnDefinition = "TEXT")
    private String profilePicture;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant lastUpdatedAt;

    @Column(nullable = false)
    private boolean isDeleted = false;

    @Version
    private Long version; // Optimistic locking for concurrent updates

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        lastUpdatedAt = Instant.now();
        logger.debug("User entity initialized: {}", id);
    }

    @PreUpdate
    protected void onUpdate() {
        lastUpdatedAt = Instant.now();
    }

    @PostPersist
    protected void logCreation() {
        logger.info("User created with ID: {}", id);
    }

    @PostUpdate
    protected void logUpdate() {
        logger.info("User ID {} successfully updated", id);
    }

    // Constructors
    public GlobalUsers(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public GlobalUsers(String firstName, String middleName, String lastName) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
    }

    public GlobalUsers(String username, String email, String password,
                       String firstName, String middleName, String lastName) {
        this.username = username;
        this.email = email;
        this.password = password; // Should be hashed by service layer
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
    }

    // Business logic methods
    public String getFullName() {
        String displayFirstName = capitalizeNamePart(firstName);
        String displayLastName = capitalizeNamePart(lastName);

        if (middleName != null && !middleName.trim().isEmpty()) {
            String displayMiddleName = capitalizeNamePart(middleName);
            return displayFirstName + " " + displayMiddleName + " " + displayLastName;
        }

        return displayFirstName + " " + displayLastName;
    }

    private String capitalizeNamePart(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "";
        }
        String trimmedLower = name.trim().toLowerCase();
        return trimmedLower.substring(0, 1).toUpperCase() + trimmedLower.substring(1);
    }
}