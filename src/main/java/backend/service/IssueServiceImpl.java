package backend.service;

import backend.model.GitRepository;
import backend.model.Issue;
import backend.model.status.IssueStatus;
import backend.model.Label;
import backend.repository.IssueRepository;
import backend.utils.exception.IssueNotFoundException;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IssueServiceImpl implements IssueService {

    private final IssueRepository issueRepository;

    private final LabelService labelService;

    private static final Logger LOGGER = LoggerFactory.getLogger(IssueServiceImpl.class);

    @Override
    public Issue findById(Long id) {
        Optional<Issue> issueOptional = issueRepository.findById(id);
        if (issueOptional.isPresent()) {
            return issueOptional.get();
        }

        throw new IssueNotFoundException(id);
    }

    @Override
    public Issue findByRepositoryIdAndNumber(Long repositoryId, int issueNumber) {
        return issueRepository.findByRepositoryIdAndNumber(repositoryId, issueNumber);
    }

    @Override
    public List<Issue> findByRepositoryIdInTimeRange(Long repositoryId, Date start, Date end) {
        return issueRepository.findByRepositoryIdInTimeRange(repositoryId, start, end);
    }

    @Override
    @Transactional
    public void deleteAllByRepositoryId(Long repositoryId) {
        issueRepository.deleteAllByRepositoryId(repositoryId);
    }

    @Override
    @Transactional
    public void mineIssues(GHRepository gitHubRepository, GitRepository repository) throws IOException {
        LOGGER.info("Mining issues");
        List<GHIssue> ghIssuesAndPRs = gitHubRepository.getIssues(GHIssueState.ALL);
        List<GHIssue> ghIssues = ghIssuesAndPRs
                .stream()
                .filter(Predicate.not(GHIssue::isPullRequest))
                .collect(Collectors.toList());

        mapAndSaveIssues(ghIssues, repository);
        repository.setIssuesCount(ghIssues.size());
    }

    @Override
    @Transactional
    public void mineNewIssues(GHRepository gitHubRepository, GitRepository repository) throws IOException {
        LOGGER.info("Mining new issues");
        List<GHIssue> newIssues = gitHubRepository.getIssues(GHIssueState.ALL)
                .stream()
                .filter(ghIssue -> {
                    try {
                        return !ghIssue.isPullRequest() && ghIssue.getCreatedAt().after(repository.getLastUpdated());
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage());
                    }
                    return false;
                })
                .collect(Collectors.toList());

        mapAndSaveIssues(newIssues, repository);
        repository.setIssuesCount(repository.getIssuesCount() + newIssues.size());
    }

    private void mapAndSaveIssues(List<GHIssue> issues, GitRepository repository) {
        issues.forEach(ghIssue -> {
            try {
                Issue issue = Issue
                        .builder()
                        .repository(repository)
                        .body(ghIssue.getBody())
                        .closedAt(ghIssue.getClosedAt())
                        .createdAt(ghIssue.getCreatedAt())
                        .number(ghIssue.getNumber())
                        .status(IssueStatus.valueOf(ghIssue.getState().toString()))
                        .title(ghIssue.getTitle())
                        .build();

                issueRepository.save(issue);

                Set<Label> labels = ghIssue
                        .getLabels()
                        .stream()
                        .map(ghLabel -> labelService
                                .findByRepositoryIdAndName(repository.getId(), ghLabel.getName())
                                .orElse(null))
                        .collect(Collectors.toSet());

                issue.setLabels(labels);
                issueRepository.save(issue);
            } catch (IOException e) {
                LOGGER.error(e.getMessage());
            }
        });
    }
}
