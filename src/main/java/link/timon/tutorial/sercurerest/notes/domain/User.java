package link.timon.tutorial.sercurerest.notes.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * This model represents a user and functions as mongo db document.
 *
 * @author Timon
 */
@Document
@Data
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String id;

    private String name;

    private String email;

    private String password;

}
