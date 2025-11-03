package app.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import app.model.GlobalUsers;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom implementation of Spring Security's UserDetails interface.
 * This class wraps the GlobalUsers entity to provide security-specific information.
 *
 * @param user Store a reference to the actual database entity
 */
public record GlobalUserDetails(GlobalUsers user) implements UserDetails {

    /**
     * Retrieves the authorities (roles/permissions) granted to the user.
     * For simplicity, we are granting a default "ROLE_USER" to every user.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // In a real application, you would load roles from the database.
        // For now, we grant a basic ROLE_USER
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /**
     * Returns the user's HASHED password (must be the hashed value from the database).
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * Returns the username used to authenticate the user.
     */
    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // --- Standard Spring Security Account Status Checks ---
    // For a basic implementation, we assume all are true unless we add logic for account locking, etc.

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // You might use a boolean field in GlobalUsers (e.g., is_enabled) here.
        return true;
    }

    public GlobalUsers getUser() {
        return user;
    }
}