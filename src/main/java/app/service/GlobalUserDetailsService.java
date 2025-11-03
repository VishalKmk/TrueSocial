package app.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import app.config.GlobalUserDetails;
import app.model.GlobalUsers;
import app.repository.GlobalUsersRepository;

/**
 * Implementation of Spring Security's UserDetailsService.
 * This service is responsible for loading user details from the database
 * and wrapping them in a UserDetails object for authentication.
 */
@Service
public class GlobalUserDetailsService implements UserDetailsService {

    private final GlobalUsersRepository globalUsersRepository;

    // Inject the UserRepository to access user data
    public GlobalUserDetailsService(GlobalUsersRepository globalUsersRepository) {
        this.globalUsersRepository = globalUsersRepository;
    }

    /**
     * Locates the user based on the username.
     * This method is called by the Spring Security framework during the login process.
     *
     * @param username The username submitted by the user.
     * @return A UserDetails object (GlobalUserDetails) containing the user's security info.
     * @throws UsernameNotFoundException if the user could not be found.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Fetch the GlobalUsers entity from the database
        GlobalUsers user = globalUsersRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // 2. Wrap the entity in your custom UserDetails implementation
        return new GlobalUserDetails(user);
    }
}