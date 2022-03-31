package backend.service;

import backend.model.Commit;
import backend.model.GitRepository;
import backend.model.Issue;
import backend.repository.CommitRepository;
import backend.utils.exception.CommitNotFoundException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {CommitServiceImpl.class})
@ExtendWith(SpringExtension.class)
public class CommitServiceTest {

    @MockBean
    private CommitRepository commitRepository;

    @MockBean
    private IssueService issueService;

    @Autowired
    private CommitServiceImpl commitService;

    @Mock
    private Commit commit;

    @Mock
    private final GitRepository gitRepository = new GitRepository();

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @BeforeEach
    public void setUp() {
        commit = Commit
                .builder()
                .hash("abc123")
                .author("AUTHOR")
                .createdAt(java.sql.Date.valueOf("2020-01-01"))
                .message("MESSAGE")
                .body("BODY")
                .modifiedFiles(Collections.singleton("file.java"))
                .repository(gitRepository)
                .build();
    }

    @Test
    public void testFindById() {
        when(commitRepository.findById(any(Long.class))).thenReturn(Optional.of(commit));

        commitService.findById(1L);
        verify(commitRepository, times(1)).findById(any(Long.class));
        assertEquals(commit, commitRepository.findById(1L).orElse(null));
    }

    @Test
    public void testFindByIdThrowsException() {
        when(commitRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThrows(CommitNotFoundException.class, () -> commitService.findById(2L));
    }

    @Test
    public void testFindAllByRepositoryId() {
        Set<Commit> commits = Set.of(commit);

        when(commitRepository.findAllByRepositoryId(any(Long.class))).thenReturn(commits);

        commitService.findAllByRepositoryId(1L);
        verify(commitRepository, times(1)).findAllByRepositoryId(any(Long.class));
        assertEquals(commits, commitRepository.findAllByRepositoryId(1L));
    }

    @Test
    public void testFindByRepositoryIdInTimeRange() throws ParseException {
        List<Commit> commitList = new ArrayList<>();
        commitList.add(commit);

        when(commitService.findByRepositoryIdInTimeRange(any(Long.class), any(Date.class), any(Date.class)))
                .thenReturn(commitList);

        assertEquals(commitList, commitService.findByRepositoryIdInTimeRange(1L, dateFormat.parse("2019-01-01"), dateFormat.parse("2019-01-01")));
    }

    @Test
    public void testDeleteAllByRepositoryId() {
        commitService.deleteAllByRepositoryId(commit.getRepository().getId());
        verify(commitRepository, times(1)).deleteAllByRepositoryId(commit.getRepository().getId());
    }

    @Test
    public void testMineCommitsThrowsException(){
        commitService.mineCommits(commit.getRepository());
        verify(commitRepository, times(0)).saveAll(anyIterable());
    }


    @Test
    public void testGetCommitsOf() throws GitAPIException, IOException {
        Git git = mock(Git.class);
        LogCommand logCommand = mock(LogCommand.class);
        Repository repository = mock(Repository.class);

        RevCommit revCommit = mock(RevCommit.class);
        PersonIdent personIdent = mock(PersonIdent.class);

        RevWalk revWalk = mock(RevWalk.class);

        List<RevCommit> list = new ArrayList<>();
        list.add(revCommit);

        when(git.log()).thenReturn(logCommand);
        when(logCommand.call()).thenReturn(list);
        when(revWalk.parseCommit(any())).thenReturn(revCommit);

        when(revCommit.getAuthorIdent()).thenReturn(personIdent);
        when(revCommit.getCommitTime()).thenReturn(0);
        when(revCommit.getShortMessage()).thenReturn("");
        when(revCommit.getFullMessage()).thenReturn("");
        when(revCommit.getParentCount()).thenReturn(1);
        when(revCommit.getParent(anyInt())).thenReturn(revCommit);
        when(personIdent.getName()).thenReturn("name");
        when(personIdent.getTimeZone()).thenReturn(TimeZone.getDefault());

        assertThrows(NullPointerException.class, () -> commitService.getCommitsOf(git, revWalk, repository));
    }

    @Test
    public void testFilesDiff() throws IOException {
        DiffFormatter diffFormatter = mock(DiffFormatter.class);
        RevCommit revCommit = mock(RevCommit.class);

        doNothing().when(diffFormatter).setRepository(any(Repository.class));
        doNothing().when(diffFormatter).setDiffComparator(any(RawTextComparator.class));
        doNothing().when(diffFormatter).setDetectRenames(any(Boolean.class));

        List<DiffEntry> entries = new ArrayList<>();
        DiffEntry modify = mock(DiffEntry.class);
        DiffEntry add = mock(DiffEntry.class);
        DiffEntry delete = mock(DiffEntry.class);
        DiffEntry copy = mock(DiffEntry.class);

        when(modify.getChangeType()).thenReturn(DiffEntry.ChangeType.MODIFY);
        when(add.getChangeType()).thenReturn(DiffEntry.ChangeType.ADD);
        when(delete.getChangeType()).thenReturn(DiffEntry.ChangeType.DELETE);
        when(copy.getChangeType()).thenReturn(DiffEntry.ChangeType.COPY);

        entries.add(modify);
        entries.add(add);
        entries.add(delete);
        entries.add(copy);

        RevTree tree = mock(RevTree.class);
        when(revCommit.getTree()).thenReturn(tree);

        when(diffFormatter.scan(any(RevTree.class), any(RevTree.class))).thenReturn(entries);

        commitService.filesDiff(null, new JSONObject(), revCommit, revCommit, diffFormatter);
    }

    @Test
    public void testLinkCommitsClosingIssues() {
        Set<Commit> commits = Set.of(commit);
        when(commitRepository.findAllByRepositoryId(any(Long.class))).thenReturn(commits);

        Issue issue = new Issue();
        when(issueService.findByRepositoryIdAndNumber(any(Long.class), any(Integer.class)))
                .thenReturn(issue);

        commitService.linkCommitsClosingIssues(commit.getRepository());
        verify(commitRepository, times(1)).save(any(Commit.class));
    }

    @Test
    public void testComputeMetrics() {
        Set<Commit> commits = Set.of(commit);
        when(commitRepository.findAllByRepositoryId(any(Long.class))).thenReturn(commits);

        commitService.computeMetrics(commit.getRepository());
        verify(commitRepository, times(1)).save(any(Commit.class));
    }
}
