package link.timon.tutorial.securerest.notes.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Thrown when an internal exception is thrown.
 *
 * @author Timon
 */
public class InternalServerException extends ResponseStatusException {

    public InternalServerException(String message, Throwable cause) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, message, cause);
    }

}
