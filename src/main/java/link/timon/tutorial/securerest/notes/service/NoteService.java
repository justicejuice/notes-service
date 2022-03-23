package link.timon.tutorial.securerest.notes.service;

import java.util.Collection;
import java.util.Optional;
import link.timon.tutorial.securerest.notes.common.EntityNotFoundException;
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
     * Creates a new Note for given user.
     *
     * @param userId The Id off the user who created the note.
     * @param noteView The NoteView to create.
     *
     * @return The created NoteView.
     */
    public Optional<NoteView> create(String userId, NoteView noteView) {
        User user = userService.getCurrentUserAuthorized(userId);
        Note note = ViewMapper.INSTANCE.noteViewToModel(noteView);

        note.setAuthor(user);

        updateUserNotes(user, note);

        return Optional.ofNullable(ViewMapper.INSTANCE.noteToView(noteRepository.save(note)));
    }

    /**
     * Updates the Note of the given user.
     *
     * @param userId The user who owns the Note.
     * @param noteView The NoteView to update.
     *
     * @return The updated NoteView
     */
    public Optional<NoteView> update(String userId, NoteView noteView) {
        userService.checkCurrentuserAuthorized(userId);

        Note note = ViewMapper.INSTANCE.noteViewToModel(noteView);
        Note saved = noteRepository.save(note);

        return Optional.ofNullable(ViewMapper.INSTANCE.noteToView(saved));
    }

    /**
     * Finds all NoteViews of a given user.
     *
     * @param userId The user to search for.
     *
     * @return All found NoteViews, can be empty.
     */
    public Collection<NoteView> findAllForUser(String userId) {
        User user = userService.getCurrentUserAuthorized(userId);

        return ViewMapper.INSTANCE.notesToViews(noteRepository.findByAuthor(user));
    }

    /**
     * Searches for a specific note of given user.
     *
     * @param userId The Id of the User to search for.
     * @param noteId The Id of the Note to search.
     *
     * @return The found NoteView.
     */
    public Optional<NoteView> findById(String userId, String noteId) {
        Optional<Note> note = noteRepository.findById(noteId);

        if (note.isEmpty()) {
            throw new EntityNotFoundException(String.format("Could not find note with id=%s", noteId));

        }

        return Optional.ofNullable(ViewMapper.INSTANCE.noteToView(note.get()));
    }

    /**
     * Deletes a Note of given User.
     *
     * @param userId The Id of the User to delete the note from.
     * @param noteId The Id of the Note to delete.
     */
    public void delete(String userId, String noteId) {
        User user = userService.getCurrentUserAuthorized(userId);
        user.getNotes().removeIf((n) -> n.getId().equalsIgnoreCase(noteId));
        userService.save(user);

        noteRepository.deleteById(noteId);
    }

    private void updateUserNotes(User user, Note note) {
        user.getNotes().add(note);
        userService.save(user);
    }

}
