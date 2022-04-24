package link.timon.tutorial.securerest.notes.service;

import link.timon.tutorial.securerest.notes.common.EntityNotFoundException;
import link.timon.tutorial.securerest.notes.common.UnauthorizedException;
import link.timon.tutorial.securerest.notes.domain.Note;
import link.timon.tutorial.securerest.notes.domain.User;
import link.timon.tutorial.securerest.notes.domain.dto.NoteView;
import link.timon.tutorial.securerest.notes.domain.dto.ViewMapper;
import link.timon.tutorial.securerest.notes.repository.NoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

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
        when(userService.getAuthenticatedUser()).thenReturn(AUTHENTICATED_USER);
        Optional<NoteView> result = testee.create(toCreate);

        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo(toCreate.getTitle());
        assertThat(AUTHENTICATED_USER.getNotes()).hasSize(1);
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

        when(userService.getAuthenticatedUser()).thenReturn(author);
        when(noteRepository.findById(toUpdate.getId())).thenReturn(Optional.of(existingNote));
        when(noteRepository.save(existingNote)).thenReturn(existingNote);

        Optional<NoteView> updated = testee.update(toUpdate.getId(), toUpdate);

        assertThat(updated).isPresent();
        assertThat(updated.get().getTitle()).isEqualTo("test");
    }

    @Test
    @DisplayName("Should not update when user not authenticated.")
    public void shouldNotUpdate() {
        when(userService.getAuthenticatedUser()).thenReturn(AUTHENTICATED_USER);
        when(noteRepository.findById("1")).thenReturn(Optional.of(Note.builder().id("1").build()));

        assertThatExceptionOfType(UnauthorizedException.class)
                .isThrownBy(() -> testee.update("1", NoteView.builder().id("1").build()))
                .withMessage("401 UNAUTHORIZED \"You are not allowed to update this note!\"");
    }

    @Test
    @DisplayName("Should not update when note not exists.")
    public void shouldNotUpdateWhenNoteNotExists() {
        when(userService.getAuthenticatedUser()).thenReturn(AUTHENTICATED_USER);
        when(noteRepository.findById("1")).thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> testee.update("1", NoteView.builder().id("1").build()))
                .withMessage("404 NOT_FOUND \"Note with id=1 does not exist!\"");
    }

    @Test
    @DisplayName("Should not update note when ids don't match")
    public void shouldNotUpdateWhenIdsDontMatch() {
        when(userService.getAuthenticatedUser()).thenReturn(AUTHENTICATED_USER);
        when(noteRepository.findById("2")).thenReturn(Optional.of(Note.builder().id("2").build()));

        assertThatExceptionOfType(UnauthorizedException.class)
                .isThrownBy(() -> testee.update("2", NoteView.builder().id("1").build()))
                .withMessage("401 UNAUTHORIZED \"You are not allowed to update this note!\"");
    }

    @Test
    @DisplayName("Should find all Notes for authenticated user")
    public void shouldFindAllNotes() {
        when(userService.getAuthenticatedUser()).thenReturn(AUTHENTICATED_USER);
        when(noteRepository.findByAuthor(AUTHENTICATED_USER)).thenReturn(List.of(
                Note.builder().id("1").build(),
                Note.builder().id("2").build()
        ));

        Collection<NoteView> allNotes = testee.findAllForAuthorizedUser();

        assertThat(allNotes).hasSize(2);
    }

    @Test
    @DisplayName("Should find a note by id of authenticated user.")
    public void shouldFindById() {
        when(userService.getAuthenticatedUser()).thenReturn(AUTHENTICATED_USER);
        when(noteRepository.findById("1"))
                .thenReturn(Optional.of(Note.builder().author(AUTHENTICATED_USER).id("1").build()));

        Optional<NoteView> result = testee.findById("1");

        assertThat(result).isPresent();
    }

    @Test
    @DisplayName("Should not find by id when note not exists.")
    public void shouldNotFindWhenNotFound() {
        when(userService.getAuthenticatedUser()).thenReturn(AUTHENTICATED_USER);
        when(noteRepository.findById(any())).thenReturn(Optional.empty());

        assertThatExceptionOfType(EntityNotFoundException.class)
                .isThrownBy(() -> testee.findById("1"))
                .withMessage("404 NOT_FOUND \"Could not find note with id=1\"");
    }

    @Test
    @DisplayName("Should be unauthorized when authenticated user tries to find another users note.")
    public void shouldBeUnauthorizedWhenWrongNote() {
        when(userService.getAuthenticatedUser()).thenReturn(AUTHENTICATED_USER);
        when(noteRepository.findById("12"))
                .thenReturn(Optional.of(Note.builder().id("12").author(User.builder().build()).build()));

        assertThatExceptionOfType(UnauthorizedException.class)
                .isThrownBy(() -> testee.findById("12"))
                .withMessage("401 UNAUTHORIZED \"You are not allowed to view this note!\"");
    }

}
