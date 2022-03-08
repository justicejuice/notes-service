package link.timon.tutorial.securerest.notes.service;

import java.util.Optional;
import link.timon.tutorial.securerest.notes.domain.dto.UserLoginRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * A Service to provide Security related functionality.
 *
 * @author Timon Link.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class SecurityService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Returns the current SecurityContext.
     *
     * @return The current SecurityContext.
     */
    public SecurityContext securityContext() {
        return SecurityContextHolder.getContext();
    }

    /**
     * Creates a secure password of given string.
     *
     * @param password The password in clear-text.
     *
     * @return The created secure password.
     */
    public String createUserPassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Returns the UnsernamePasswordAuthenticationToken from SecurityContext.
     *
     * @return The UsernamePasswordAuthenticationToken, can be empty.
     */
    public Optional<UsernamePasswordAuthenticationToken> getAuthenticationToken() {
        SecurityContext securityContext = securityContext();
        UsernamePasswordAuthenticationToken token;

        if (securityContext == null) {
            log.warn("Security Context is null!");
            return Optional.empty();
        }

        if (securityContext.getAuthentication() instanceof UsernamePasswordAuthenticationToken) {
            token = (UsernamePasswordAuthenticationToken) securityContext().getAuthentication();
            return Optional.ofNullable(token);
        }

        log.warn("The current security context does not hold an UsernamePasswordAuthenticationToken.");

        return Optional.empty();
    }

    /**
     * Authenticates a user by given login request.
     *
     * @param login The login request.
     *
     * @return The authenticated user.
     */
    public Object authenticate(UserLoginRequestDto login) {
        UsernamePasswordAuthenticationToken token = createToken(login);
        Authentication authenticate = authenticationManager.authenticate(token);
        return authenticate.getPrincipal();
    }

    private UsernamePasswordAuthenticationToken createToken(UserLoginRequestDto login) {
        return new UsernamePasswordAuthenticationToken(login.getEmail(), login.getPassword());
    }

}
