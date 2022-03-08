package link.timon.tutorial.securerest.notes.service;

import java.util.Optional;
import java.util.Set;
import link.timon.tutorial.securerest.notes.common.EntityAlreadyExistsException;
import link.timon.tutorial.securerest.notes.domain.Role;
import link.timon.tutorial.securerest.notes.domain.User;
import link.timon.tutorial.securerest.notes.domain.dto.UserRegisterRequestDto;
import link.timon.tutorial.securerest.notes.domain.dto.UserView;
import link.timon.tutorial.securerest.notes.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    private static final String PASSWORD = "secret";
    private static final String NAME = "Max";
    private static final String EMAIL = "test@test.de";

    @Mock
    private UserRepository repository;

    @Mock
    private SecurityService secuityService;

    @InjectMocks
    private UserService service;

    @Test
    @DisplayName("Should Register a new user.")
    public void shouldRegister() {
        UserRegisterRequestDto user = testRegisterRequest();

        Mockito.when(secuityService.createUserPassword(PASSWORD)).thenReturn("ULTRA SECURE 98237492");
        Mockito.when(repository.findByEmail(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(repository.save(Mockito.any())).thenReturn(createdTestUser());

        Optional<UserView> result = service.register(user);

        Assertions.assertThat(result).isPresent();
    }

    @Test
    @DisplayName("Should not register a user if the e-mail allready exists.")
    public void shouldNotRegisterBecauseEmailExists() {
        UserRegisterRequestDto user = testRegisterRequest();

        Mockito.when(repository.findByEmail(user.getEmail())).thenReturn(Optional.of(createdTestUser()));

        Assertions.assertThatExceptionOfType(EntityAlreadyExistsException.class)
                .isThrownBy(() -> service.register(user))
                .withMessage(String.format("409 CONFLICT \"E-Mail %s already exists.\"", user.getEmail()));

    }

    private UserRegisterRequestDto testRegisterRequest() {
        return UserRegisterRequestDto.builder().email(EMAIL).name(NAME).password(PASSWORD).build();
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
