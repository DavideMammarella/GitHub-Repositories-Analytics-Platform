package backend.service;

import backend.model.GitRepository;
import backend.model.Issue;
import backend.model.status.IssueStatus;
import backend.repository.IssueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHIssueState;
import org.kohsuke.github.GHRepository;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {IssueServiceImpl.class})
@ExtendWith(SpringExtension.class)
public class IssueServiceTest {

    @MockBean
    private IssueRepository issueRepository;

    @MockBean
    private LabelService labelService;

    @Autowired
    private IssueServiceImpl issueService;

    @Mock
    private Issue issue;

    @Mock
    private final GitRepository gitRepository = new GitRepository();

    @Mock
    private final GHIssue ghIssue = new GHIssue();

    @Mock
    private final GHRepository ghRepository = new GHRepository();

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @BeforeEach
    public void setUp() throws ParseException {
        issue = Issue
                .builder()
                .body("BODY")
                .createdAt(dateFormat.parse("2020-01-01"))
                .closedAt(dateFormat.parse("2021-01-01"))
                .number(1)
                .status(IssueStatus.valueOf("CLOSED"))
                .title("TITLE")
                .repository(gitRepository)
                .build();
    }

    @Test
    public void testFindById() {
        when(issueRepository.findById(any(Long.class))).thenReturn(Optional.of(issue));

        issueService.findById(1L);
        verify(issueRepository, times(1)).findById(any(Long.class));
        assertEquals(issue, issueRepository.findById(1L).orElse(null));
    }

    @Test
    public void testFindByRepositoryIdAndNumber() {
        when(issueService.findByRepositoryIdAndNumber(any(Long.class), any(Integer.class)))
                .thenReturn(issue);

        assertEquals(issue, issueService.findByRepositoryIdAndNumber(1L, 1));
    }

    @Test
    public void testFindByRepositoryIdInTimeRange() throws ParseException {
        List<Issue> issueList = new ArrayList<>();
        issueList.add(issue);

        when(issueService.findByRepositoryIdInTimeRange(any(Long.class), any(Date.class), any(Date.class)))
                .thenReturn(issueList);

        // issue in time range
        assertEquals(issueList, issueService.findByRepositoryIdInTimeRange(1L, dateFormat.parse("2019-01-01"), dateFormat.parse("2021-02-02")));
        // issue with createdAt = time range start
        assertEquals(dateFormat.parse("2020-01-01"), issue.getCreatedAt());
        assertEquals(issueList, issueService.findByRepositoryIdInTimeRange(1L, dateFormat.parse("2020-01-01"), dateFormat.parse("2021-02-02")));
        // issue with closedAt = time range end
        assertEquals(dateFormat.parse("2021-01-01"), issue.getClosedAt());
        assertEquals(issueList, issueService.findByRepositoryIdInTimeRange(1L, dateFormat.parse("2019-01-01"), dateFormat.parse("2021-01-01")));
    }

    @Test
    public void testDeleteAllByRepositoryId() {
        issueService.deleteAllByRepositoryId(issue.getRepository().getId());
        verify(issueRepository, times(1)).deleteAllByRepositoryId(issue.getRepository().getId());
    }

    @Test
    public void testMineIssues() throws IOException {
        when(ghIssue.getState()).thenReturn(GHIssueState.OPEN);

        List<GHIssue> issues = new ArrayList<>();
        issues.add(ghIssue);
        when(ghRepository.getIssues(any(GHIssueState.class))).thenReturn(issues);

        issueService.mineIssues(ghRepository, issue.getRepository());
        verify(issueRepository, times(2)).save(any(Issue.class));

        when(ghIssue.getCreatedAt()).thenThrow(IOException.class);
        assertThrows(IOException.class, ghIssue::getCreatedAt);
        verify(issueRepository, times(2)).save(any(Issue.class));
    }

    @Test
    public void testMineNewIssues() throws IOException, ParseException {
        when(ghIssue.getState()).thenReturn(GHIssueState.OPEN);

        List<GHIssue> issues = new ArrayList<>();
        issues.add(ghIssue);

        when(ghIssue.isPullRequest()).thenReturn(true);

        when(issue.getRepository().getLastUpdated()).thenReturn(dateFormat.parse("2020-01-01"));

        when(ghRepository.getIssues(any(GHIssueState.class))).thenReturn(issues);

        issueService.mineNewIssues(ghRepository, issue.getRepository());
        verify(issueRepository, times(0)).save(any(Issue.class));
    }

}