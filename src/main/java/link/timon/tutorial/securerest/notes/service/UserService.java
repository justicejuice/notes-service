package link.timon.tutorial.securerest.notes.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import link.timon.tutorial.securerest.notes.common.EntityAlreadyExistsException;
import link.timon.tutorial.securerest.notes.common.InternalServerException;
import link.timon.tutorial.securerest.notes.domain.Role;
import link.timon.tutorial.securerest.notes.domain.User;
import link.timon.tutorial.securerest.notes.domain.dto.RegisterRequest;
import link.timon.tutorial.securerest.notes.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service for user entity.
 *
 * @author Timon
 */
@Service
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new User.
     *
     * @param userToRegister The User to register.
     *
     * @return The created User.
     *
     * @throws EntityAlreadyExistsException if the user already exists.
     */
    public Optional<User> register(RegisterRequest userToRegister) {
        repository.findByEmail(userToRegister.getEmail()).ifPresent(u -> {
            throw new EntityAlreadyExistsException(String.format("E-Mail %s already exists.", u.getEmail()));
        });

        Role basicUser = new Role(Role.BASIC_USER);

        User user = User.builder()
                .authorities(Set.of(basicUser))
                .email(userToRegister.getEmail())
                .name(userToRegister.getName())
                .password(passwordEncoder.encode(userToRegister.getPassword()))
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        return Optional.ofNullable(repository.save(user));
    }

    /**
     * Returns the currently logged in user. Identified by jwt stored in security context.
     *
     * @return The currently logged in user or empty, if it can't be received.
     */
    public Optional<User> getCurrentUser() {
        SecurityContext context = SecurityContextHolder.getContext();

        if (context == null) {
            return Optional.empty();
        }

        UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) context.getAuthentication();

        if (authentication == null) {
            return Optional.empty();
        }

        return Optional.ofNullable((User) authentication.getPrincipal());
    }

    /**
     * Deletes a user by given Id.
     *
     * @param userId The id of the user to delete.
     */
    public void deleteById(String userId) {
        try {
            repository.deleteById(userId);
        } catch (Exception e) {
            throw new InternalServerException(String.format("Could not delete User %s", userId), e);
        }

    }

}
