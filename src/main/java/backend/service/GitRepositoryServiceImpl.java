package backend.service;

import backend.model.*;
import backend.model.dto.CommitSmallDTO;
import backend.model.dto.GitRepositoryDataDTO;
import backend.model.dto.IssueSmallDTO;
import backend.model.status.RequestStatus;
import backend.repository.GitRepositoryRepository;
import backend.utils.RequestProcessor;
import backend.utils.exception.GitRepositoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GitRepositoryServiceImpl implements GitRepositoryService {

    private final GitRepositoryRepository repository;

    private final CommitService commitService;
    private final IssueService issueService;
    private final LabelService labelService;
    private final GitHubApiService gitHubApiService;

    private GitHub gitHub;

    private final java.io.File gitRepositoryDirectory = new File("src/main/resources/repositories");

    private static final Logger LOGGER = LoggerFactory.getLogger(GitRepositoryServiceImpl.class);

    @PostConstruct
    private void setupGitHubApi() {
        this.gitHub = gitHubApiService.getGitHub();
    }

    @PreDestroy
    public void destroy() {
        deleteDirectory(gitRepositoryDirectory);
        RequestProcessor.stop();
    }

    @Override
    public Page<GitRepository> findAllPage(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public GitRepository findById(Long id) {
        Optional<GitRepository> gitRepositoryOptional = repository.findById(id);
        if (gitRepositoryOptional.isPresent()) {
            return gitRepositoryOptional.get();
        }

        throw new GitRepositoryNotFoundException(id);
    }

    @Override
    public Optional<GitRepository> findByUrl(String url) {
        return repository.findByUrl(url);
    }

    @Override
    public Set<GitRepository> findAllByStatusIn(Set<RequestStatus> statuses) {
        return repository.findAllByStatusIn(statuses);
    }

    @Override
    @Transactional
    public void saveAndFlush(GitRepository gitRepository) {
        repository.saveAndFlush(gitRepository);
    }

    @Override
    @Transactional
    public void reset(GitRepository gitRepository) {
        gitRepository.reset();
        labelService.deleteAllByRepositoryId(gitRepository.getId());
        commitService.deleteAllByRepositoryId(gitRepository.getId());
        issueService.deleteAllByRepositoryId(gitRepository.getId());

        save(gitRepository);
    }

    @Override
    @Transactional
    public void save(GitRepository gitRepository) {
        repository.save(gitRepository);
    }

    @Override
    public Optional<GitRepository> getNextRepositoryInQueue() {
        return repository.findFirstByStatusEqualsOrderByLastUpdatedAsc(RequestStatus.IN_QUEUE);
    }

    @Override
    public GitRepositoryDataDTO getRepositoryDataInTimeRange(Long id, Date start, Date end) {
        return GitRepositoryDataDTO
                .builder()
                .commits(commitService.findByRepositoryIdInTimeRange(id, start, end)
                        .stream()
                        .map(CommitSmallDTO::new)
                        .collect(Collectors.toList()))
                .issues(issueService.findByRepositoryIdInTimeRange(id, start, end)
                        .stream()
                        .map(IssueSmallDTO::new)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional
    public void setRepositoryStatus(GitRepository gitRepository, RequestStatus status) {
        gitRepository.setStatus(status);
        saveAndFlush(gitRepository);
    }

    @Override
    @Transactional
    public GitRepository createRepository(String url) {
        final GitRepository gitRepository = GitRepository
                .builder()
                .url(url)
                .lastUpdated(new Date())
                .status(RequestStatus.IN_QUEUE)
                .build();

        save(gitRepository);
        return gitRepository;
    }

    @Override
    @Transactional
    public void addRepositoryDetails(GitRepository gitRepository, GHRepository ghRepository) {
        gitRepository.setDescription(ghRepository.getDescription());
        gitRepository.setForksCount(ghRepository.getForksCount());
        gitRepository.setTitle(ghRepository.getName());

        save(gitRepository);
    }

    @Override
    public void mineRepository(GitRepository gitRepository) {
        LOGGER.info("Started request for repository " + gitRepository.getUrl());
        GHRepository ghRepository = getGhRepository(gitRepository);
        mineRepository(gitRepository, ghRepository);
    }

    public void mineRepository(GitRepository gitRepository, GHRepository ghRepository) {
        setRepositoryStatus(gitRepository, RequestStatus.DOWNLOADING);
        LOGGER.info("Cloning repository");
        cloneRepository(gitRepository.getUrl());

        setRepositoryStatus(gitRepository, RequestStatus.MINING);
        addRepositoryDetails(gitRepository, ghRepository);

        try {
            labelService.mineLabels(ghRepository, gitRepository);
            commitService.mineCommits(gitRepository);
            issueService.mineIssues(ghRepository, gitRepository);

            setRepositoryStatus(gitRepository, RequestStatus.ANALYZING);
            commitService.linkCommitsClosingIssues(gitRepository);
            commitService.computeMetrics(gitRepository);

            deleteDirectory(gitRepositoryDirectory);
            setRepositoryStatus(gitRepository, RequestStatus.DONE);
            LOGGER.info("Completed request for repository " + gitRepository.getUrl());
        } catch (IOException e) {
            deleteDirectory(gitRepositoryDirectory);
            setRepositoryStatus(gitRepository, RequestStatus.FAILED);
            throw new GitRepositoryNotFoundException(gitRepository.getUrl());
        }
    }

    @Override
    @Transactional
    public void requestUpdate(GitRepository gitRepository) {
        gitRepository.setUpdating(true);
        setRepositoryStatus(gitRepository, RequestStatus.IN_QUEUE);
    }

    @Override
    public void updateRepository(GitRepository gitRepository) {
        LOGGER.info("Updating repository " + gitRepository.getUrl());

        GHRepository ghRepository = getGhRepository(gitRepository);
        updateRepository(gitRepository, ghRepository);
    }

    public void updateRepository(GitRepository gitRepository, GHRepository ghRepository) {
        if (ghRepository.getPushedAt().before(gitRepository.getLastUpdated())) {
            gitRepository.setLastUpdated(new Date());
            gitRepository.setUpdating(false);
            setRepositoryStatus(gitRepository, RequestStatus.DONE);
            LOGGER.info("Repository " + gitRepository.getUrl() + " is up-to-date");
            return;
        }

        setRepositoryStatus(gitRepository, RequestStatus.DOWNLOADING);
        cloneRepository(gitRepository.getUrl());

        setRepositoryStatus(gitRepository, RequestStatus.MINING);

        try {
            labelService.mineNewLabels(ghRepository, gitRepository);
            commitService.deleteAllByRepositoryId(gitRepository.getId());
            commitService.mineCommits(gitRepository);
            issueService.mineNewIssues(ghRepository, gitRepository);

            setRepositoryStatus(gitRepository, RequestStatus.ANALYZING);
            commitService.linkCommitsClosingIssues(gitRepository);
            commitService.computeMetrics(gitRepository);

            gitRepository.setUpdating(false);
            gitRepository.setLastUpdated(new Date());
            deleteDirectory(gitRepositoryDirectory);
            setRepositoryStatus(gitRepository, RequestStatus.DONE);
            LOGGER.info("Completed update request for repository " + gitRepository.getUrl());
        } catch (IOException e) {
            gitRepository.setUpdating(false);
            deleteDirectory(gitRepositoryDirectory);
            setRepositoryStatus(gitRepository, RequestStatus.FAILED);
            LOGGER.error(e.getMessage());
        }

    }

    private GHRepository getGhRepository(GitRepository repository) {
        GHRepository ghRepository;
        try {
            ghRepository = gitHub.getRepository(repository.getUrl());
        } catch (IOException e) {
            setRepositoryStatus(repository, RequestStatus.FAILED);
            throw new GitRepositoryNotFoundException(repository.getUrl());
        }
        return ghRepository;
    }

    private void cloneRepository(String repositoryUrl) {
        try {
            Git.cloneRepository()
                    .setURI("https://github.com/" + repositoryUrl + ".git")
                    .setDirectory(gitRepositoryDirectory)
                    .setBranch("refs/heads/master")
                    .call();
        } catch (GitAPIException e) {
            LOGGER.error(e.getMessage());
        }
    }

    private void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }

        try {
            Files.delete(directoryToBeDeleted.toPath());
        } catch (IOException e) {
            LOGGER.info("Unable to delete directory: " + e.getMessage());
        }
    }
}
