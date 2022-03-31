package backend.service;

import backend.model.GitRepository;
import backend.model.Label;
import org.kohsuke.github.GHRepository;

import java.io.IOException;
import java.util.Optional;

public interface LabelService {

    Optional<Label> findByRepositoryIdAndName(Long id, String name);

    void deleteAllByRepositoryId(Long repositoryId);

    void mineLabels(GHRepository gitHubRepository, GitRepository repository) throws IOException;

    void mineNewLabels(GHRepository gitHubRepository, GitRepository repository) throws IOException;
}
