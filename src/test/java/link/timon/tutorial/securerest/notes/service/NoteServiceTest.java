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

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NoteServiceTest {

    @Mock
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

        when(noteRepository.save(any())).thenReturn(model);
        when(userService.getAuthenticatedUser()).thenReturn(Optional.of(AUTHENTICATED_USER));
        Optional<NoteView> result = testee.create(toCreate);

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo(toCreate.getTitle());
        assertThat(AUTHENTICATED_USER.getNotes()).hasSize(1);
    }

    @Test
    @DisplayName("Should not create when user not authenticated")
    public void shouldNotCreate() {
        when(userService.getAuthenticatedUser()).thenReturn(Optional.empty());
        assertThatExceptionOfType(UnauthorizedException.class).isThrownBy(() ->
                testee.create(NoteView.builder().build())
        );
    }

    @Test
    @DisplayName("Should Update Note.")
    public void shouldUpdate() {
        User author = User.builder().id("1").build();

        NoteView toUpdate = NoteView.builder()
                .id("1")
                .text("test")
                .title("test")
                .build();

        Note existingNote = ViewMapper.INSTANCE.noteViewToModel(toUpdate);
        existingNote.setAuthor(author);
        existingNote.setTitle("psst.");

        when(userService.getAuthenticatedUser()).thenReturn(Optional.of(author));
        when(noteRepository.findById(toUpdate.getId())).thenReturn(Optional.of(existingNote));
        when(noteRepository.save(existingNote)).thenReturn(existingNote);

        Optional<NoteView> updated = testee.update(toUpdate);

        assertThat(updated).isPresent();
        assertThat(updated.get().getTitle()).isEqualTo("test");
    }

    @Test
    @DisplayName("Should not update when user not authenticated.")
    public void shouldNotUpdate() {
        when(userService.getAuthenticatedUser()).thenReturn(Optional.empty());
        when(noteRepository.findById("1")).thenReturn(Optional.of(Note.builder().id("1").build()));

        assertThatExceptionOfType(UnauthorizedException.class)
                .isThrownBy(() -> testee.update(NoteView.builder().id("1").build()))
                .withMessage("401 UNAUTHORIZED \"You are not allowed to update this note!\"");
    }

}
