package link.timon.tutorial.securerest.notes.domain.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User data-transfer-object for UI clients.
 *
 * @author Timon
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserView {

    private String id;
    private String email;
    private String name;
    private LocalDateTime createdAt;

}
