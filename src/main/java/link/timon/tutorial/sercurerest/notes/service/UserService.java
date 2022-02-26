package link.timon.tutorial.sercurerest.notes.service;

import java.util.Optional;
import link.timon.tutorial.sercurerest.notes.common.EntityAlreadyExistsException;
import link.timon.tutorial.sercurerest.notes.domain.User;
import link.timon.tutorial.sercurerest.notes.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * Service for user entity.
 *
 * @author Timon
 */
@Service
public class UserService {

    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
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
    public Optional<User> register(User user) {
        repository.findByEmail(user.getEmail()).ifPresent(u -> {
            throw new EntityAlreadyExistsException(String.format("E-Mail %s already exists.", u.getEmail()));
        });
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
