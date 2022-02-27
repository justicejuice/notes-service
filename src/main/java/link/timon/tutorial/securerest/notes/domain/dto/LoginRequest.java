package link.timon.tutorial.securerest.notes.domain.dto;

import lombok.Data;

/**
 * Dto for a login.
 *
 * @author Timon
 */
@Data
public class LoginRequest {

    private String email;
    private String password;

}
