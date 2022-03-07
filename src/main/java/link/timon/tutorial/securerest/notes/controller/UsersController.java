package link.timon.tutorial.securerest.notes.controller;

import java.util.Optional;
import link.timon.tutorial.securerest.notes.common.RestConstants;
import link.timon.tutorial.securerest.notes.common.UnauthorizedException;
import link.timon.tutorial.securerest.notes.domain.User;
import link.timon.tutorial.securerest.notes.domain.dto.UserLoginRequestDto;
import link.timon.tutorial.securerest.notes.domain.dto.UserRegisterRequestDto;
import link.timon.tutorial.securerest.notes.domain.dto.UserView;
import link.timon.tutorial.securerest.notes.domain.dto.UserViewMapper;
import link.timon.tutorial.securerest.notes.security.JwtUtil;
import link.timon.tutorial.securerest.notes.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final UserViewMapper mapper;

    @PostMapping("/register")
    public ResponseEntity<UserView> create(@RequestBody UserRegisterRequestDto user) {
        Optional<User> registeredUser = service.register(user);

        if (registeredUser.isPresent()) {
            return ResponseEntity.ok(mapper.modelToView(registeredUser.get()));
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("/login")
    public ResponseEntity<UserView> login(@RequestBody UserLoginRequestDto login) {
        UsernamePasswordAuthenticationToken pwToken = createPasswordToken(login);
        Authentication authenticate = authenticationManager.authenticate(pwToken);

        User user = (User) authenticate.getPrincipal();

        String token = jwtUtil.generateFor(user);

        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, token).body(mapper.modelToView(user));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable String userId) {
        Optional<User> currentUser = service.getCurrentUser();

        if (currentUser.isEmpty() || !currentUser.get().getId().equals(userId)) {
            throw new UnauthorizedException();
        }

        service.deleteById(userId);

        return ResponseEntity.noContent().build();
    }

    private UsernamePasswordAuthenticationToken createPasswordToken(UserLoginRequestDto request) {
        return new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
    }

}
