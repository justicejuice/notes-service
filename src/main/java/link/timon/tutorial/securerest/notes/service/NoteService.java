package link.timon.tutorial.securerest.notes.service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import link.timon.tutorial.securerest.notes.domain.Note;
import link.timon.tutorial.securerest.notes.domain.User;
import link.timon.tutorial.securerest.notes.domain.dto.NoteView;
import link.timon.tutorial.securerest.notes.repository.NoteRepository;
import link.timon.tutorial.securerest.notes.repository.UserRepository;
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
    private final UserRepository userRepository;

    public Optional<NoteView> save(User user, NoteView noteView) {
        Note note = Note.builder()
                .author(user)
                .createdAt(LocalDateTime.now())
                .modifiedAt(LocalDateTime.now())
                .text(noteView.getText())
                .title(noteView.getTitle())
                .build();

        user.getNotes().add(note);
        userRepository.save(user);

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

    public Collection<NoteView> findAllForUser(User user) {
        return noteRepository.findByAuthor(user)
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
        userRepository.save(user);

        noteRepository.deleteById(noteId);
    }

}
