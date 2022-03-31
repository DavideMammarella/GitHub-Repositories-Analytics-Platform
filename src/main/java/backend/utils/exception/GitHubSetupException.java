package backend.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
public class GitHubSetupException extends RuntimeException {

    public GitHubSetupException() {
        super("Error with GitHub API setup");
    }
}
