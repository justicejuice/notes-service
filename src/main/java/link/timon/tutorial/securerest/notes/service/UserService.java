package link.timon.tutorial.securerest.notes.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import link.timon.tutorial.securerest.notes.common.EntityAlreadyExistsException;
import link.timon.tutorial.securerest.notes.common.InternalServerException;
import link.timon.tutorial.securerest.notes.common.UnauthorizedException;
import link.timon.tutorial.securerest.notes.domain.Role;
import link.timon.tutorial.securerest.notes.domain.User;
import link.timon.tutorial.securerest.notes.domain.dto.UserRegisterRequestDto;
import link.timon.tutorial.securerest.notes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

/**
 * Service for user entity.
 *
 * @author Timon
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final SecurityService securityService;

    /**
     * Registers a new User.
     *
     * @param userToRegister The User to register.
     *
     * @return The created User.
     *
     * @throws EntityAlreadyExistsException if the user already exists.
     */
    public Optional<User> register(UserRegisterRequestDto userToRegister) {
        repository.findByEmail(userToRegister.getEmail()).ifPresent(u -> {
            throw new EntityAlreadyExistsException(String.format("E-Mail %s already exists.", u.getEmail()));
        });

        Role basicUser = new Role(Role.BASIC_USER);

        User user = User.builder()
                .authorities(Set.of(basicUser))
                .email(userToRegister.getEmail())
                .name(userToRegister.getName())
                .password(securityService.createUserPassword(userToRegister.getPassword()))
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .build();

        return Optional.ofNullable(repository.save(user));
    }

    /**
     * Returns the currently logged in user. Identified by jwt stored in
     * security context.
     *
     * @return The currently logged in user or empty, if it can't be received.
     */
    public Optional<User> getCurrentUser() {
        Optional<UsernamePasswordAuthenticationToken> token = securityService.getAuthenticationToken();

        if (token.isPresent()) {
            return Optional.ofNullable((User) token.get().getPrincipal());
        }

        return Optional.empty();
    }

    /**
     * Gets the currently logged in User and checks if he is authorized for
     * operating of given user ids entities. If he is authorized, the User
     * entity will be returned. If he is not authorized an Unauthorized
     * exception will be thrown.
     *
     * @param userId The User Id to check.
     *
     * @return The user if he is authorized.
     */
    public Optional<User> getCurrentUserAuthorized(String userId) {
        Optional<User> currentUser = getCurrentUser();

        if (currentUser.isEmpty() || StringUtils.equals(userId, currentUser.get().getId())) {
            throw new UnauthorizedException(String.format("User witth Id=%s is not authorized for this request", userId));
        }

        return currentUser;
    }

    /**
     * Deletes a user by given Id.
     *
     * @param userId The id of the user to delete.
     */
    public void deleteById(String userId) {
        try {
            repository.deleteById(userId);
        }
        catch (Exception e) {
            throw new InternalServerException(String.format("Could not delete User %s", userId), e);
        }
    }

    /**
     * Saves The given user in repository.
     *
     * @param user The User to save.
     *
     * @return The saved User. Can be empty.
     */
    Optional<User> save(User user) {
        return Optional.ofNullable(repository.save(user));
    }

}
