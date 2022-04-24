package link.timon.tutorial.securerest.notes.service;

import link.timon.tutorial.securerest.notes.common.EntityAlreadyExistsException;
import link.timon.tutorial.securerest.notes.common.UnauthorizedException;
import link.timon.tutorial.securerest.notes.domain.Role;
import link.timon.tutorial.securerest.notes.domain.User;
import link.timon.tutorial.securerest.notes.domain.dto.UserLoginRequestDto;
import link.timon.tutorial.securerest.notes.domain.dto.UserRegisterRequestDto;
import link.timon.tutorial.securerest.notes.domain.dto.UserView;
import link.timon.tutorial.securerest.notes.domain.dto.ViewMapper;
import link.timon.tutorial.securerest.notes.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

/**
 * Service for user entity.
 *
 * @author Timon
 */
@Service
@RequiredArgsConstructor
@Slf4j
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
    public Optional<UserView> register(UserRegisterRequestDto userToRegister) {
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
                .build();

        return Optional.ofNullable(ViewMapper.INSTANCE.userToView(repository.save(user)));
    }

    /**
     * Performs a login and returns the authenticated user.
     *
     * @param login The login request.
     * @return The UserView of authenticated user.
     */
    public Optional<UserView> login(UserLoginRequestDto login) {
        User user = (User) securityService.authenticate(login);
        log.info("Logged in User: {}", user);
        log.info("Users notes: {}", user.getNotes());
        return Optional.ofNullable(ViewMapper.INSTANCE.userToView(user));
    }

    /**
     * Returns the currently logged in user. Identified by jwt stored in
     * security context.
     *
     * @return The currently logged in user or empty, if it can't be received.
     */
    public User getAuthenticatedUser() {
        Optional<UsernamePasswordAuthenticationToken> token = securityService.getAuthenticationToken();

        if (token.isPresent() && token.get().getPrincipal() instanceof User) {
            return (User) token.get().getPrincipal();
        }

        log.error("Something went wrong with authentication token! {}", token);
        throw new UnauthorizedException("No authenticated User found!");
    }

    /**
     * Checks if given userId is authorized to perform the desired action.
     *
     * @param userId The user to check.
     *
     * @throws UnauthorizedException when given userId is not the current authorized user.
     */
    void checkCurrentuserAuthorized(String userId) {
        User currentUser = getAuthenticatedUser();

        if (!StringUtils.equals(userId, currentUser.getId())) {
            throw new UnauthorizedException(String.format("User witth Id=%s is not authorized for this request", userId));
        }
    }

    /**
     * Deletes a user by given Id.
     *
     * @param userId The id of the user to delete.
     */
    public void deleteById(String userId) {
        checkCurrentuserAuthorized(userId);
        repository.deleteById(userId);
    }

    /**
     * Saves The given user in repository.
     *
     * @param user The User to save.
     *
     * @return The saved User. Can be empty.
     */
    Optional<User> save(User user) {
        return Optional.of(repository.save(user));
    }

}
