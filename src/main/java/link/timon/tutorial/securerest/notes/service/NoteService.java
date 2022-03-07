package link.timon.tutorial.securerest.notes.service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import link.timon.tutorial.securerest.notes.domain.Note;
import link.timon.tutorial.securerest.notes.domain.User;
import link.timon.tutorial.securerest.notes.domain.dto.NoteView;
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

    public Optional<NoteView> save(String userId, NoteView noteView) {
        Optional<User> userOptional = userService.getCurrentUserAuthorized(userId);

        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();

        Note note = Note.builder()
                .author(user)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .text(noteView.getText())
                .title(noteView.getTitle())
                .build();

        user.getNotes().add(note);
        userService.save(user);

        Note savedNote = noteRepository.save(note);

        if (savedNote == null) {
            return Optional.empty();
        }

        return Optional.of(NoteView.builder()
                .createdAt(savedNote.getCreatedAt())
                .modifiedAt(savedNote.getModifiedAt())
                .id(savedNote.getId())
                .title(savedNote.getTitle())
                .text(savedNote.getText())
                .build());
    }

    public Optional<Note> update(Note note) {
        return Optional.ofNullable(noteRepository.save(note));
    }

    public Collection<NoteView> findAllForUser(String userId) {
        Optional<User> user = userService.getCurrentUserAuthorized(userId);

        if (user.isEmpty()) {
            return List.of();
        }

        return noteRepository.findByAuthor(user.get())
                .stream()
                .map(fetchedNote -> NoteView.builder()
                .createdAt(fetchedNote.getCreatedAt())
                .modifiedAt(fetchedNote.getModifiedAt())
                .id(fetchedNote.getId())
                .text(fetchedNote.getText())
                .title(fetchedNote.getTitle())
                .build())
                .collect(Collectors.toList());
    }

    public Optional<Note> findById(String noteId) {
        return noteRepository.findById(noteId);
    }

    public void delete(User user, String noteId) {
        user.getNotes().removeIf((n) -> n.getId().equalsIgnoreCase(noteId));
        userService.save(user);

        noteRepository.deleteById(noteId);
    }

}
