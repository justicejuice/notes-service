package link.timon.tutorial.securerest.notes.repository;

import java.util.ArrayList;
import java.util.List;
import link.timon.tutorial.securerest.notes.domain.Note;
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
public class NoteRepositoryTest {

    private static final String USER_NAME = "Hans Maulwurf";
    private static final String USER_MAIL = "hans.maulwurf@support.com";
    private static final String USER_PASSWORD = "secret";

    @Container
    private static final MongoDBContainer container = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", container::getReplicaSetUrl);
    }

    private User user;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        user = userRepository.save(User.builder()
                .email(USER_MAIL)
                .name(USER_NAME)
                .password(USER_PASSWORD)
                .notes(List.of())
                .build());
    }

    @AfterEach
    public void clearNotes() {
        noteRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void shouldNotFindAnyNote() {
        Assertions.assertThat(noteRepository.findByAuthor(user)).isEmpty();
    }

    @Test
    public void shouldFindTwoNotesForUser() {
        final User anotherUser = User.builder().email("te@mail.de").name("Dex").password("pw").build();

        noteRepository.save(Note.builder().author(user).title("title 1").text("text 1").build());
        noteRepository.save(Note.builder().author(user).title("title 2").text("text 2").build());
        noteRepository.save(Note.builder().author(anotherUser).title("title 3").text("text 3").build());

        List<Note> result = new ArrayList<>(noteRepository.findByAuthor(user));

        Assertions.assertThat(result).hasSize(2);
        Assertions.assertThat(result.get(0).getAuthor()).isEqualTo(user);
        Assertions.assertThat(result.get(0).getTitle()).isEqualTo("title 1");
        Assertions.assertThat(result.get(1).getAuthor()).isEqualTo(user);
        Assertions.assertThat(result.get(1).getTitle()).isEqualTo("title 2");
    }

}
