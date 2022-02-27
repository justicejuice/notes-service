package link.timon.tutorial.securerest.notes.controller;

import java.util.Collection;
import java.util.Optional;
import link.timon.tutorial.securerest.notes.common.RestConstants;
import link.timon.tutorial.securerest.notes.common.UnauthorizedException;
import link.timon.tutorial.securerest.notes.domain.User;
import link.timon.tutorial.securerest.notes.domain.dto.NoteView;
import link.timon.tutorial.securerest.notes.service.NoteService;
import link.timon.tutorial.securerest.notes.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(RestConstants.API_V1 + "/users/{userId}/notes")
public class NotesController {

    private final UserService userService;
    private final NoteService noteService;

    @GetMapping
    public ResponseEntity<Collection<NoteView>> findAll(@PathVariable String userId) {
        User user = ensureCurrentUser(userId);
        return ResponseEntity.ok(noteService.findAllForUser(user));
    }

    @PostMapping
    public ResponseEntity<NoteView> create(@PathVariable String userId, @RequestBody NoteView note) {
        User user = ensureCurrentUser(userId);
        return ResponseEntity.of(noteService.save(user, note));
    }

    private User ensureCurrentUser(String requestUserId) {
        Optional<User> currentUser = userService.getCurrentUser();

        if (currentUser.isEmpty() || !currentUser.get().getId().equals(requestUserId)) {
            throw new UnauthorizedException("You are not allowed to access this users notes!");
        }

        return currentUser.get();
    }

}
