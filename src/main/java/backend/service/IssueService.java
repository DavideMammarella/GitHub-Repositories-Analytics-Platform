package backend.service;

import backend.model.GitRepository;
import backend.model.Issue;
import org.kohsuke.github.GHRepository;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface IssueService {

    Issue findById(Long id);

    Issue findByRepositoryIdAndNumber(Long repositoryId, int issueNumber);

    List<Issue> findByRepositoryIdInTimeRange(Long repositoryId, Date start, Date end);

    void deleteAllByRepositoryId(Long repositoryId);

    void mineIssues(GHRepository gitHubRepository, GitRepository repository) throws IOException;

    void mineNewIssues(GHRepository gitHubRepository, GitRepository repository) throws IOException;
}
