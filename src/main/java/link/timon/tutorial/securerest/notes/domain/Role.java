package link.timon.tutorial.securerest.notes.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

/**
 * Represents a role for authorization.
 *
 * @author Timon
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role implements GrantedAuthority {

    public static final String ADMIN = "ADMIN";
    public static final String BASIC_USER = "BASIC_USER";

    private String authority;

}
