package link.timon.tutorial.securerest.notes.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request data for request.
 *
 * @author Timon
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegisterRequestDto {

    private String email;
    private String name;
    private String password;

}
