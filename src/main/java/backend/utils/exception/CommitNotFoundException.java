package backend.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CommitNotFoundException extends RuntimeException {

    public CommitNotFoundException(Long id) {
        super("Commit with id " + id + " not found");
    }
}
