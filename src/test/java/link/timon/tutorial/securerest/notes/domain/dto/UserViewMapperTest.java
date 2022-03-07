package link.timon.tutorial.securerest.notes.domain.dto;

import java.time.LocalDateTime;
import link.timon.tutorial.securerest.notes.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class UserViewMapperTest {

    private static final String ID = "kajshdfkals";
    private static final String NAME = "Max Mustermann";
    private static final String EMAIL = "max.muster@mail.de";
    private static final String PASSWORD = "2323";
    private static final LocalDateTime CREATED = LocalDateTime.now();

    @Autowired
    private UserViewMapper testee;

    @Test
    public void shouldMapToUserView() {
        User user = User.builder()
                .id(ID)
                .name(NAME)
                .email(EMAIL)
                .password(PASSWORD)
                .createdAt(CREATED)
                .build();

        UserView result = testee.map(user);

        Assertions.assertThat(result.getId()).isEqualTo(ID);
        Assertions.assertThat(result.getCreatedAt()).isEqualTo(CREATED);
        Assertions.assertThat(result.getName()).isEqualTo(NAME);
        Assertions.assertThat(result.getEmail()).isEqualTo(EMAIL);
    }

    @Test
    public void shouldMapUserViewToUser() {
        UserView userView = UserView.builder()
                .id(ID)
                .email(EMAIL)
                .name(NAME)
                .createdAt(CREATED)
                .build();

        User result = testee.map(userView);

        Assertions.assertThat(result.getId()).isEqualTo(ID);
        Assertions.assertThat(result.getCreatedAt()).isEqualTo(CREATED);
        Assertions.assertThat(result.getName()).isEqualTo(NAME);
        Assertions.assertThat(result.getEmail()).isEqualTo(EMAIL);
        Assertions.assertThat(result.getPassword()).isBlank();

    }
}
