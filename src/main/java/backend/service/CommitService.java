package backend.service;

import backend.model.Commit;
import backend.model.GitRepository;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface CommitService {

    Commit findById(Long id);

    Set<Commit> findAllByRepositoryId(Long id);

    List<Commit> findByRepositoryIdInTimeRange(Long repositoryId, Date start, Date end);

    void deleteAllByRepositoryId(Long repositoryId);

    void mineCommits(GitRepository gitRepository);

    void linkCommitsClosingIssues(GitRepository gitRepository);

    void computeMetrics(GitRepository gitRepository);
}
