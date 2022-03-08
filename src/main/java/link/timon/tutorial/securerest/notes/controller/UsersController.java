package link.timon.tutorial.securerest.notes.controller;

import java.util.Optional;
import link.timon.tutorial.securerest.notes.common.RestConstants;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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

    @PostMapping("/register")
    public ResponseEntity<UserView> create(@RequestBody UserRegisterRequestDto user) {
        return ResponseEntity.of(service.register(user));
    }

    @PostMapping("/login")
    public ResponseEntity<UserView> login(@RequestBody UserLoginRequestDto login) {
        Optional<UserView> user = service.login(login);

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = jwtUtil.generateFor(UserViewMapper.INSTANCE.viewToModel(user.get()));

        return ResponseEntity.ok().header(HttpHeaders.AUTHORIZATION, token).body(user.get());
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable String userId) {
        service.deleteById(userId);
        return ResponseEntity.noContent().build();
    }

    private UsernamePasswordAuthenticationToken createPasswordToken(UserLoginRequestDto request) {
        return new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());
    }

}
