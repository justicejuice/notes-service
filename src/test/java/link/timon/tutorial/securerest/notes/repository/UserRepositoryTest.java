package link.timon.tutorial.securerest.notes.repository;

import java.util.Optional;
import link.timon.tutorial.securerest.notes.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@EnableMongoAuditing
@DataMongoTest(excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
public class UserRepositoryTest {

    private static final String USER_NAME = "Hans Maulwurf";
    private static final String USER_MAIL = "hans.maulwurf@support.com";
    private static final String USER_PASSWORD = "secret";

    @Container
    private static final MongoDBContainer container = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", container::getReplicaSetUrl);
    }

    @Autowired
    private UserRepository repository;

    @AfterEach
    void cleanUp() {
        repository.deleteAll();
    }

    @BeforeEach
    void setUp() {
        repository.save(User.builder()
                .email(USER_MAIL)
                .name(USER_NAME)
                .password(USER_PASSWORD)
                .build());
    }

    @Test
    @DisplayName("Should save a new user.")
    public void shouldSaveNew() {
        User result = repository.save(User.builder().email("test@test.de").build());

        Assertions.assertThat(result.getId()).isNotBlank();
        Assertions.assertThat(result.getEmail()).isEqualTo("test@test.de");
        Assertions.assertThat(result.getCreatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should find users by email.")
    public void shouldFindByEmail() {
        Optional<User> result = repository.findByEmail(USER_MAIL);

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getId()).isNotBlank();
        Assertions.assertThat(result.get().getEmail()).isEqualTo(USER_MAIL);
        Assertions.assertThat(result.get().getName()).isEqualTo(USER_NAME);
        Assertions.assertThat(result.get().getPassword()).isEqualTo(USER_PASSWORD);
    }

    @Test
    @DisplayName("Should not find any user when email is unknown.")
    public void shouldNotFindByMailWhenNotExists() {
        Optional<User> result = repository.findByEmail("test@test.de");
        Assertions.assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should update values of users after save.")
    public void shouldUpdate() {
        User user = repository.findAll().get(0);

        String id = user.getId();
        String oldName = user.getName();

        user.setName("Donald Duck");

        User result = repository.save(user);

        Assertions.assertThat(oldName).isEqualTo(USER_NAME);
        Assertions.assertThat(result.getName()).isEqualTo("Donald Duck");
        Assertions.assertThat(id).isEqualTo(result.getId());
    }
}
