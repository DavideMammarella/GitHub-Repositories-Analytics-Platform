package backend.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class GitRepositoryNotFoundException extends RuntimeException {

    public GitRepositoryNotFoundException(Long id) {
        super("Repository with id " + id + " not found");
    }

    public GitRepositoryNotFoundException(String repositoryUrl) {
        super("Repository " + repositoryUrl + " does not exist or has private/restricted access");
    }
}