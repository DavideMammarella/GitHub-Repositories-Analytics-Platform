package backend.service;

import backend.metrics.MetricsComputer;
import backend.model.Commit;
import backend.model.GitRepository;
import backend.parser.CommitsJSONtoPOJO;
import backend.parser.LinkCommitToClosedIssue;
import backend.repository.CommitRepository;
import backend.utils.exception.CommitNotFoundException;
import lombok.RequiredArgsConstructor;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CommitServiceImpl implements CommitService {

    private final CommitRepository commitRepository;

    private final IssueService issueService;

    private static final Logger LOGGER = LoggerFactory.getLogger(CommitServiceImpl.class);

    @Override
    public Commit findById(Long id) {
        Optional<Commit> commitOptional = commitRepository.findById(id);
        if (commitOptional.isPresent()) {
            return commitOptional.get();
        }

        throw new CommitNotFoundException(id);
    }

    @Override
    public Set<Commit> findAllByRepositoryId(Long id) {
        return commitRepository.findAllByRepositoryId(id);
    }

    @Override
    public List<Commit> findByRepositoryIdInTimeRange(Long repositoryId, Date start, Date end) {
        return commitRepository.findByRepositoryIdAndCreatedAtBetweenOrderByCreatedAt(repositoryId, start, end);
    }

    @Override
    @Transactional
    public void deleteAllByRepositoryId(Long repositoryId) {
        commitRepository.deleteAllByRepositoryId(repositoryId);
    }

    @Override
    @Transactional
    public void mineCommits(GitRepository gitRepository) {
        LOGGER.info("Mining commits");
        try {
            String pathToFile = toFile(String.valueOf(getCommits()));

            List<Commit> commits = CommitsJSONtoPOJO.run(pathToFile);

            commits.forEach(commit -> commit.setRepository(gitRepository));
            commitRepository.saveAll(commits);
            gitRepository.setCommitsCount(commits.size());
        } catch (IOException | GitAPIException e) {
            LOGGER.error(e.getMessage());
        }
    }

    @Override
    @Transactional
    public void linkCommitsClosingIssues(GitRepository gitRepository) {
        LOGGER.info("Linking commits closing issues");

        Set<Commit> commits = findAllByRepositoryId(gitRepository.getId());

        commits.forEach(commit -> {
            Set<Integer> closingIssuesNumbers = LinkCommitToClosedIssue.fixCloseResolveFinder(commit.getMessage(), gitRepository.getUrl());

            commit.setClosingIssues(closingIssuesNumbers
                    .stream()
                    .map(issueNumber -> issueService.findByRepositoryIdAndNumber(gitRepository.getId(), issueNumber))
                    .collect(Collectors.toSet()));

            commitRepository.save(commit);
        });
    }

    @Override
    @Transactional
    public void computeMetrics(GitRepository gitRepository) {
        LOGGER.info("Computing commit metrics");

        Set<Commit> commits = findAllByRepositoryId(gitRepository.getId());

        commits.forEach(commit -> {
            MetricsComputer.computeMetricsDifference(commit);
            commitRepository.save(commit);
        });
    }

    private JSONArray getCommits() throws IOException, GitAPIException {
        Repository repository;

        File tempGitRepositoryDirectory = new File("src/main/resources/repositories/.git");

        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        repository = builder.setGitDir(tempGitRepositoryDirectory)
                .setMustExist(true).build();

        Git git = new Git(repository);
        RevWalk revWalk = new RevWalk(repository);

        return getCommitsOf(git, revWalk, repository);
    }

    public JSONArray getCommitsOf(Git git, RevWalk revWalk, Repository repository) throws IOException, GitAPIException {

        JSONArray jsonCommitArray = new JSONArray();

        Iterable<RevCommit> commits = git.log().call();

        for (RevCommit commit : commits) {

            JSONObject jsonCommit = new JSONObject();

            // Prepare the pieces
            final String author = commit.getAuthorIdent().getName();

            final Instant commitInstant = Instant.ofEpochSecond(commit.getCommitTime());
            final ZoneId zone = commit.getAuthorIdent().getTimeZone().toZoneId();
            final ZonedDateTime authorDateTime = ZonedDateTime.ofInstant(commitInstant, zone);
            final String dateFormat = "EEE MMM dd HH:mm:ss yyyy Z";
            final String formattedDate = authorDateTime.format(DateTimeFormatter.ofPattern(dateFormat));

            final String message = Arrays
                    .stream(commit.getShortMessage().split("\\r?\\n"))
                    .map(s -> " " + s)
                    .collect(Collectors.joining()); // put it back together

            final String body = Arrays
                    .stream(commit.getFullMessage().split("\\r?\\n"))
                    .map(s -> " " + s)
                    .collect(Collectors.joining());

            RevCommit currentCommit = revWalk.parseCommit(commit.toObjectId());
            RevCommit parent = revWalk.parseCommit(commit.toObjectId());

            if (revWalk.parseCommit(commit.toObjectId()).getParentCount() > 0)
                parent = revWalk.parseCommit(commit.getParent(0).getId());

            DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);

            jsonCommit.put("commit", commit.getName());
            jsonCommit.put("author", author);
            jsonCommit.put("date", formattedDate);
            jsonCommit.put("message", message);
            jsonCommit.put("body", body);
            filesDiff(repository, jsonCommit, currentCommit, parent, diffFormatter);
            jsonCommitArray.put(jsonCommit);
        }

        return jsonCommitArray;
    }

    public void filesDiff(Repository repository, JSONObject jsonCommit, RevCommit currentCommit, RevCommit parent, DiffFormatter diffFormatter) throws IOException {
        JSONArray fileArrayModified = new JSONArray();
        JSONArray fileArrayDeleted = new JSONArray();
        JSONArray fileArrayAdded = new JSONArray();

        diffFormatter.setRepository(repository);
        diffFormatter.setDiffComparator(RawTextComparator.DEFAULT);
        diffFormatter.setDetectRenames(true);

        List<DiffEntry> diffs = diffFormatter.scan(parent.getTree(), currentCommit.getTree());

        for (DiffEntry diff : diffs) {
            switch (diff.getChangeType()) {
                case MODIFY:
                    fileArrayModified.put(MessageFormat.format("{1}", diff.getChangeType().name(), diff.getNewPath()));
                    break;
                case ADD:
                    fileArrayAdded.put(MessageFormat.format("{1}", diff.getChangeType().name(), diff.getNewPath()));
                    break;
                case DELETE:
                    fileArrayDeleted.put(MessageFormat.format("{1}", diff.getChangeType().name(), diff.getNewPath()));
                    break;
                default:
                    break;
            }
        }

        jsonCommit.put("modified_files", fileArrayModified);
        jsonCommit.put("added_files", fileArrayAdded);
        jsonCommit.put("deleted_files", fileArrayDeleted);

    }

    private static String toFile(String string) throws IOException {
        String directoryPath = "src/main/resources/";
        String fileName = "log.json";
        File logOutput = new File(directoryPath + fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logOutput, false))) {
            writer.write(string);
        }

        return directoryPath + fileName;
    }
}


