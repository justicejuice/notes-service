package link.timon.tutorial.securerest.notes.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * This Exception will be thrown, when an entity could not be found.
 *
 * @author Timon
 */
public class EntityNotFoundException extends ResponseStatusException {

    public EntityNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }

}
