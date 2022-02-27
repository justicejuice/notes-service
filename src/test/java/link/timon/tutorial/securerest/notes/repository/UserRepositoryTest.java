package link.timon.tutorial.securerest.notes.repository;

import link.timon.tutorial.securerest.notes.repository.UserRepository;
import java.util.Optional;
import link.timon.tutorial.securerest.notes.domain.User;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
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
    public void shouldSaveNew() {
        User result = repository.save(User.builder().email("test@test.de").build());

        Assertions.assertThat(result.getId()).isNotBlank();
        Assertions.assertThat(result.getEmail()).isEqualTo("test@test.de");
    }

    @Test
    public void shouldFindByEmail() {
        Optional<User> result = repository.findByEmail(USER_MAIL);

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getId()).isNotBlank();
        Assertions.assertThat(result.get().getEmail()).isEqualTo(USER_MAIL);
        Assertions.assertThat(result.get().getName()).isEqualTo(USER_NAME);
        Assertions.assertThat(result.get().getPassword()).isEqualTo(USER_PASSWORD);
    }

    @Test
    public void shouldNotFindByMailWhenNotExists() {
        Optional<User> result = repository.findByEmail("test@test.de");
        Assertions.assertThat(result).isEmpty();
    }

    @Test
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
