package link.timon.tutorial.securerest.notes.service;

import java.util.Optional;
import java.util.Set;
import link.timon.tutorial.securerest.notes.common.EntityAlreadyExistsException;
import link.timon.tutorial.securerest.notes.domain.dto.RegisterRequest;
import link.timon.tutorial.securerest.notes.domain.Role;
import link.timon.tutorial.securerest.notes.domain.User;
import link.timon.tutorial.securerest.notes.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static final String PASSWORD = "secret";
    private static final String NAME = "Max";
    private static final String EMAIL = "test@test.de";

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService service;

    @Test
    public void shouldRegister() {
        RegisterRequest user = testRegisterRequest();

        Mockito.when(passwordEncoder.encode(PASSWORD)).thenReturn("ULTRA SECURE 98237492");
        Mockito.when(repository.findByEmail(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(repository.save(Mockito.any())).thenReturn(createdTestUser());

        Optional<User> result = service.register(user);

        Assertions.assertThat(result).isPresent();
    }

    @Test
    public void shouldNotRegisterBecauseEmailExists() {
        RegisterRequest user = testRegisterRequest();

        Mockito.when(repository.findByEmail(user.getEmail())).thenReturn(Optional.of(createdTestUser()));

        Assertions.assertThatExceptionOfType(EntityAlreadyExistsException.class)
                .isThrownBy(() -> service.register(user))
                .withMessage(String.format("409 CONFLICT \"E-Mail %s already exists.\"", user.getEmail()));

    }

    private RegisterRequest testRegisterRequest() {
        return RegisterRequest.builder().email(EMAIL).name(NAME).password(PASSWORD).build();
    }

    private User createdTestUser() {
        return User.builder()
                .email(EMAIL)
                .password(PASSWORD)
                .name(NAME)
                .authorities(Set.of(new Role(Role.BASIC_USER)))
                .build();
    }

}
