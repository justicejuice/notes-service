package link.timon.tutorial.sercurerest.notes.controller;

import link.timon.tutorial.sercurerest.notes.common.RestConstants;
import link.timon.tutorial.sercurerest.notes.domain.User;
import link.timon.tutorial.sercurerest.notes.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController for user resource.
 *
 * @author Timon
 */
@RestController
@RequestMapping(RestConstants.API_V1 + "/users")
public class UsersController {

    private final UserService service;

    public UsersController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        return ResponseEntity.of(service.register(user));
    }

    @GetMapping
    public ResponseEntity<User> login(String email, String password) {
        return ResponseEntity.ok(User.builder().email(email).password(password).build());
    }

}
