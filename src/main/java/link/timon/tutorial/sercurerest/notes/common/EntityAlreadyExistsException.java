package link.timon.tutorial.sercurerest.notes.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Base class for handling Exceptions in notes service.
 *
 * @author Timon
 */
public class EntityAlreadyExistsException extends ResponseStatusException {

    public EntityAlreadyExistsException(String message) {
        super(HttpStatus.CONFLICT, message);
    }

}
