package link.timon.tutorial.securerest.notes.controller;

import java.util.Collection;
import link.timon.tutorial.securerest.notes.common.RestConstants;
import link.timon.tutorial.securerest.notes.domain.dto.NoteView;
import link.timon.tutorial.securerest.notes.service.NoteService;
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

    private final NoteService noteService;

    @GetMapping
    public ResponseEntity<Collection<NoteView>> findAll(@PathVariable String userId) {
        return ResponseEntity.ok(noteService.findAllForUser(userId));
    }

    @PostMapping
    public ResponseEntity<NoteView> create(@PathVariable String userId, @RequestBody NoteView note) {
        return ResponseEntity.of(noteService.create(userId, note));
    }
}
