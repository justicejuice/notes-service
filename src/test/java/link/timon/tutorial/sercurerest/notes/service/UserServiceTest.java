package link.timon.tutorial.sercurerest.notes.service;

import java.util.Optional;
import link.timon.tutorial.sercurerest.notes.common.EntityAlreadyExistsException;
import link.timon.tutorial.sercurerest.notes.domain.User;
import link.timon.tutorial.sercurerest.notes.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    @Test
    public void shouldRegister() {
        User user = testUser();

        Mockito.when(repository.findByEmail(Mockito.any())).thenReturn(Optional.empty());
        Mockito.when(repository.save(user)).thenReturn(user);

        Optional<User> result = service.register(user);

        Assertions.assertThat(result).isPresent();
    }

    @Test
    public void shouldNotRegisterBecauseEmailExists() {
        User user = testUser();
        Mockito.when(repository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        Assertions.assertThatExceptionOfType(EntityAlreadyExistsException.class)
                .isThrownBy(() -> service.register(user))
                .withMessage(String.format("409 CONFLICT \"E-Mail %s already exists.\"", user.getEmail()));

    }

    private User testUser() {
        return User.builder().email("test@test.de").name("Max").password("secret").build();
    }

}
