package link.timon.tutorial.securerest.notes.service;

import java.util.Collection;
import java.util.Optional;
import link.timon.tutorial.securerest.notes.common.EntityNotFoundException;
import link.timon.tutorial.securerest.notes.common.UnauthorizedException;
import link.timon.tutorial.securerest.notes.domain.Note;
import link.timon.tutorial.securerest.notes.domain.User;
import link.timon.tutorial.securerest.notes.domain.dto.NoteView;
import link.timon.tutorial.securerest.notes.domain.dto.ViewMapper;
import link.timon.tutorial.securerest.notes.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service for notes.
 *
 * @author Timon
 */
@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final UserService userService;

    /**
     * Creates a new Note for authenticated user.
     *
     * @param noteView The NoteView to create.
     *
     * @return The created NoteView.
     */
    public Optional<NoteView> create(NoteView noteView) {
        Note note = ViewMapper.INSTANCE.noteViewToModel(noteView);

        Optional<User> authenticatedUser = userService.getAuthenticatedUser();

        if (authenticatedUser.isEmpty()) {
            throw new UnauthorizedException("You must be logged in to create a new note.");
        }


        note.setAuthor(authenticatedUser.get());

        updateUserNotes(authenticatedUser.get(), note);

        return Optional.ofNullable(ViewMapper.INSTANCE.noteToView(noteRepository.save(note)));
    }

    /**
     * Updates the Note of the given user.
     *
     * @param noteView The NoteView to update.
     *
     * @return The updated NoteView
     */
    public Optional<NoteView> update(NoteView noteView) {
        Note note = ViewMapper.INSTANCE.noteViewToModel(noteView);
        Note saved = noteRepository.save(note);

        return Optional.ofNullable(ViewMapper.INSTANCE.noteToView(saved));
    }

    /**
     * Finds all NoteViews of a given user.
     *
     * @return All found NoteViews, can be empty.
     */
    public Collection<NoteView> findAllForAuthorizedUser() {
        Optional<User> authenticatedUser = userService.getAuthenticatedUser();

        if (authenticatedUser.isEmpty()) {
            throw new UnauthorizedException("Please login to view your notes.");
        }

        return ViewMapper.INSTANCE.notesToViews(noteRepository.findByAuthor(authenticatedUser.get()));
    }

    /**
     * Searches for a specific note of authenticated user.
     *
     * @param noteId The Id of the Note to search.
     *
     * @return The found NoteView.
     */
    public Optional<NoteView> findById(String noteId) {
        Optional<Note> note = noteRepository.findById(noteId);
        Optional<User> authenticatedUser = userService.getAuthenticatedUser();

        if (note.isEmpty()) {
            throw new EntityNotFoundException(String.format("Could not find note with id=%s", noteId));
        }

        if (authenticatedUser.isEmpty() || !authenticatedUser.get().getId().equals(note.get().getAuthor().getId())) {
            throw new UnauthorizedException("You are not allowed to view this note!");
        }

        return Optional.ofNullable(ViewMapper.INSTANCE.noteToView(note.get()));
    }

    /**
     * Deletes a Note of authenticated User.
     *
     * @param noteId The Id of the Note to delete.
     */
    public void delete(String noteId) {
        Optional<User> authenticatedUser = userService.getAuthenticatedUser();

        if (authenticatedUser.isEmpty()) {
            throw new UnauthorizedException("You must be authenticated to delete a note.");
        }

        Optional<Note> noteOptional = authenticatedUser.get().getNotes()
                .stream()
                .filter(note -> note.getId().equals(noteId))
                .findFirst();

        if (noteOptional.isEmpty()) {
            throw new EntityNotFoundException(String.format("Note with id=%s not found", noteId));
        }

        if (!noteOptional.get().getAuthor().getId().equals(authenticatedUser.get().getId())) {
            throw new UnauthorizedException("You are not allowed to remove this note!");
        }

        authenticatedUser.get().getNotes().removeIf((n) -> n.getId().equalsIgnoreCase(noteId));
        userService.save(authenticatedUser.get());

        noteRepository.deleteById(noteId);
    }

    private void updateUserNotes(User user, Note note) {
        user.getNotes().add(note);
        userService.save(user);
    }

}
