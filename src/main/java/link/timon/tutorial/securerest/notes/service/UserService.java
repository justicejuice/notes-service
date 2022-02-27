package link.timon.tutorial.securerest.notes.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import link.timon.tutorial.securerest.notes.common.EntityAlreadyExistsException;
import link.timon.tutorial.securerest.notes.domain.dto.RegisterRequest;
import link.timon.tutorial.securerest.notes.domain.Role;
import link.timon.tutorial.securerest.notes.domain.User;
import link.timon.tutorial.securerest.notes.repository.UserRepository;
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
     * @param user The User to register.
     *
     * @return The created User.
     *
     * @throws EntityAlreadyExistsException if the user already exists.
     */
    public Optional<User> register(RegisterRequest register) {
        repository.findByEmail(register.getEmail()).ifPresent(u -> {
            throw new EntityAlreadyExistsException(String.format("E-Mail %s already exists.", u.getEmail()));
        });

        Role basicUser = new Role(Role.BASIC_USER);

        User user = User.builder()
                .authorities(Set.of(basicUser))
                .email(register.getEmail())
                .name(register.getName())
                .password(passwordEncoder.encode(register.getPassword()))
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        return Optional.ofNullable(repository.save(user));
    }

    /**
     * Logs in an existing user.
     *
     * @param email The email of the user to login.
     * @param password The password of the user to authorize.
     *
     * @return The User or <code>empty</code>.
     */
    public Optional<User> login(String email, String password) {
        // TODO implement!
        return Optional.of(User.builder().email(email).password(password).name("IMPLEMENT!!!").build());
    }

}
