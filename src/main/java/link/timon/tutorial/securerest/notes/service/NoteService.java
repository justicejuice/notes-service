package link.timon.tutorial.securerest.notes.service;

import link.timon.tutorial.securerest.notes.common.EntityNotFoundException;
import link.timon.tutorial.securerest.notes.common.UnauthorizedException;
import link.timon.tutorial.securerest.notes.domain.Note;
import link.timon.tutorial.securerest.notes.domain.User;
import link.timon.tutorial.securerest.notes.domain.dto.NoteView;
import link.timon.tutorial.securerest.notes.domain.dto.ViewMapper;
import link.timon.tutorial.securerest.notes.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

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

        User authenticatedUser = userService.getAuthenticatedUser();

        note.setAuthor(authenticatedUser);

        updateUserNotes(authenticatedUser, note);

        return Optional.ofNullable(ViewMapper.INSTANCE.noteToView(noteRepository.save(note)));
    }

    /**
     * Updates given Note of the authenticated user.
     *
     * @param noteId The id of the note to update.
     * @param noteView The NoteView to update.
     *
     * @return The updated NoteView.
     */
    public Optional<NoteView> update(String noteId, NoteView noteView) {
        User authenticatedUser = userService.getAuthenticatedUser();
        Optional<Note> noteToUpdate = noteRepository.findById(noteId);

        if (noteToUpdate.isEmpty()) {
            throw new EntityNotFoundException(String.format("Note with id=%s does not exist!", noteView.getId()));
        }

        if (!StringUtils.equals(authenticatedUser.getId(), noteToUpdate.get().getId())
                || !StringUtils.equals(noteId, noteView.getId())) {

            throw new UnauthorizedException("You are not allowed to update this note!");

        }

        noteToUpdate.get().setTitle(noteView.getTitle());
        noteToUpdate.get().setText(noteView.getText());

        Note saved = noteRepository.save(noteToUpdate.get());

        return Optional.ofNullable(ViewMapper.INSTANCE.noteToView(saved));
    }

    /**
     * Finds all NoteViews of a given user.
     *
     * @return All found NoteViews, can be empty.
     */
    public Collection<NoteView> findAllForAuthorizedUser() {
        User authenticatedUser = userService.getAuthenticatedUser();

        return ViewMapper.INSTANCE.notesToViews(noteRepository.findByAuthor(authenticatedUser));
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
        User authenticatedUser = userService.getAuthenticatedUser();

        if (note.isEmpty()) {
            throw new EntityNotFoundException(String.format("Could not find note with id=%s", noteId));
        }

        if (!StringUtils.equals(authenticatedUser.getId(), note.get().getAuthor().getId())) {
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
        User authenticatedUser = userService.getAuthenticatedUser();

        Optional<Note> noteOptional = authenticatedUser.getNotes()
                .stream()
                .filter(note -> note.getId().equals(noteId))
                .findFirst();

        if (noteOptional.isEmpty()) {
            throw new EntityNotFoundException(String.format("Note with id=%s not found", noteId));
        }

        if (!StringUtils.equals(noteOptional.get().getAuthor().getId(), authenticatedUser.getId())) {
            throw new UnauthorizedException("You are not allowed to remove this note!");
        }

        authenticatedUser.getNotes().removeIf((n) -> n.getId().equalsIgnoreCase(noteId));
        userService.save(authenticatedUser);

        noteRepository.deleteById(noteId);
    }

    private void updateUserNotes(User user, Note note) {
        user.getNotes().add(note);
        userService.save(user);
    }

}
