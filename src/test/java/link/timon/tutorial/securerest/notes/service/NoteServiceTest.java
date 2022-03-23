package link.timon.tutorial.securerest.notes.service;

import java.util.ArrayList;
import java.util.Optional;
import link.timon.tutorial.securerest.notes.common.UnauthorizedException;
import link.timon.tutorial.securerest.notes.domain.Note;
import link.timon.tutorial.securerest.notes.domain.User;
import link.timon.tutorial.securerest.notes.domain.dto.NoteView;
import link.timon.tutorial.securerest.notes.domain.dto.ViewMapper;
import link.timon.tutorial.securerest.notes.repository.NoteRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class NoteServiceTest {

    @Mock(lenient = true)
    private UserService userService;

    @Mock
    private NoteRepository noteRepository;

    @InjectMocks
    private NoteService testee;

    private static final User AUTHENTICATED_USER = User.builder()
            .id("12345")
            .email("Testmail")
            .enabled(true)
            .name("Hans Maulwurf")
            .notes(new ArrayList<>())
            .build();

    @BeforeEach
    public void setupMocks() {
        Mockito.when(this.userService.getCurrentUserAuthorized(AUTHENTICATED_USER.getId()))
                .thenReturn(AUTHENTICATED_USER);
        AUTHENTICATED_USER.getNotes().clear();
    }

    @Test
    @DisplayName("Should create Note for authenticated user")
    public void shouldCreate() {
        NoteView toCreate = NoteView.builder()
                .title("To Do")
                .text("Testcontent")
                .build();

        Note model = ViewMapper.INSTANCE.noteViewToModel(toCreate);

        Mockito.when(noteRepository.save(Mockito.any())).thenReturn(model);
        Optional<NoteView> result = testee.create(AUTHENTICATED_USER.getId(), toCreate);

        Assertions.assertThat(result).isPresent();
        Assertions.assertThat(result.get().getTitle()).isEqualTo(toCreate.getTitle());
        Assertions.assertThat(AUTHENTICATED_USER.getNotes()).hasSize(1);
    }

    @Test
    @DisplayName("Should not create when user not authenticated")
    public void shouldNotCreate() {
        Mockito.when(userService.getCurrentUserAuthorized(Mockito.any())).thenThrow(UnauthorizedException.class);
        Assertions.assertThatExceptionOfType(UnauthorizedException.class).isThrownBy(() -> {
            testee.create("1234", NoteView.builder().build());
        });
    }

}
