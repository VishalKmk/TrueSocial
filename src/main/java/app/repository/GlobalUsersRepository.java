package app.repository;

import lombok.NonNull;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import app.model.GlobalUsers;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface GlobalUsersRepository extends JpaRepository<GlobalUsers, UUID> {

    Optional<GlobalUsers> findByUsername(String username);

    @Query("SELECT u FROM GlobalUsers u WHERE u.email = ?1")
    Optional<GlobalUsers> findByEmail(String email);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM GlobalUsers u WHERE u.username = ?1 AND u.id != ?2")
    boolean existsByUsernameExcludingId(String username, UUID userId);

    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM GlobalUsers u WHERE u.email = ?1 AND u.id != ?2")
    boolean existsByEmailExcludingId(String email, UUID userId);

    @NonNull
    Optional<GlobalUsers> findById(@NonNull UUID id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
