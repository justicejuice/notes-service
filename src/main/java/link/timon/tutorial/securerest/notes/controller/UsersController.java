package link.timon.tutorial.securerest.notes.controller;

import java.util.Optional;
import link.timon.tutorial.securerest.notes.common.RestConstants;
import link.timon.tutorial.securerest.notes.domain.User;
import link.timon.tutorial.securerest.notes.domain.dto.LoginRequest;
import link.timon.tutorial.securerest.notes.domain.dto.RegisterRequest;
import link.timon.tutorial.securerest.notes.domain.dto.UserView;
import link.timon.tutorial.securerest.notes.security.JwtUtil;
import link.timon.tutorial.securerest.notes.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController for user resource.
 *
 * @author Timon
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(RestConstants.API_V1 + "/users")
public class UsersController {

    private final UserService service;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<UserView> create(@RequestBody RegisterRequest user) {
        Optional<User> registeredUser = service.register(user);

        if (registeredUser.isPresent()) {
            return ResponseEntity.ok(UserView.map(registeredUser.get()));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/login")
    public ResponseEntity<UserView> login(@RequestBody LoginRequest login) {
        UsernamePasswordAuthenticationToken pwToken = createPasswordToken(login);
        Authentication authenticate = authenticationManager.authenticate(pwToken);

        User user = (User) authenticate.getPrincipal();

        String token = jwtUtil.generateFor(user);

        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, token).body(UserView.map(user));
    }

    private UsernamePasswordAuthenticationToken createPasswordToken(LoginRequest request) {
        return new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
    }

}
