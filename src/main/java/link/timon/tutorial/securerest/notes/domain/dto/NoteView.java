package link.timon.tutorial.securerest.notes.domain.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto for Note.
 *
 * @author Timon
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoteView {

    private String id;
    private String title;
    private String text;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

}
