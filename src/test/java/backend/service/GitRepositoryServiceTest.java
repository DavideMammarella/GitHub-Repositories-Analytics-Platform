package backend.service;

import backend.model.GitRepository;
import backend.model.status.RequestStatus;
import backend.repository.GitRepositoryRepository;
import backend.utils.exception.GitRepositoryNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {GitRepositoryServiceImpl.class})
@ExtendWith(SpringExtension.class)
public class GitRepositoryServiceTest {

    @MockBean
    private GitRepositoryRepository repository;

    @MockBean
    private CommitService commitService;

    @MockBean
    private IssueService issueService;

    @MockBean
    private LabelService labelService;

    @MockBean
    private GitHubApiService gitHubApiService;

    @Autowired
    private GitRepositoryServiceImpl gitRepositoryService;

    @Mock
    private GitRepository gitRepository;

    @Mock
    private GitHub gitHub;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @BeforeEach
    public void setUp() throws ParseException {
        when(gitHubApiService.getGitHub()).thenReturn(gitHub);

        gitRepository = GitRepository
                .builder()
                .description("DESC")
                .title("TITLE")
                .url("http://a.com")
                .lastUpdated(dateFormat.parse("2020-01-01"))
                .isUpdating(false)
                .forksCount(1)
                .status(RequestStatus.IN_QUEUE)
                .build();
    }

    @Test
    public void testFindAllPage() {
        Pageable pageable = mock(Pageable.class);
        when(repository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(new ArrayList<>()));

        gitRepositoryService.findAllPage(pageable);
        verify(repository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void testFindById(){
        when(repository.findById(anyLong())).thenReturn(Optional.of(gitRepository));

        assertEquals(gitRepositoryService.findById(0L), gitRepository);
        verify(repository, times(1)).findById(anyLong());
    }

    @Test
    public void testNotFoundById() {
        when(repository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(GitRepositoryNotFoundException.class, () -> gitRepositoryService.findById(0L));
        verify(repository, times(1)).findById(anyLong());
    }

    @Test
    public void testFindByUrl() {
        when(repository.findByUrl(anyString())).thenReturn(Optional.of(gitRepository));

        assertTrue(gitRepositoryService.findByUrl("url").isPresent());
        assertEquals(gitRepositoryService.findByUrl("url").get(), gitRepository);

        verify(repository, times(2)).findByUrl(anyString());
    }

    @Test
    public void testFindAllByStatusIn() {
        when(repository.findAllByStatusIn(anySet())).thenReturn(Collections.emptySet());

        assertEquals(0, gitRepositoryService.findAllByStatusIn(new HashSet<>()).size());
        verify(repository, times(1)).findAllByStatusIn(anySet());
    }

    @Test
    public void testSaveAndFlush() {
        when(repository.saveAndFlush(any(GitRepository.class))).thenReturn(gitRepository);
        gitRepositoryService.saveAndFlush(gitRepository);
        verify(repository, times(1)).saveAndFlush(any(GitRepository.class));
    }

    @Test
    public void testReset() {
        doNothing().when(labelService).deleteAllByRepositoryId(anyLong());
        doNothing().when(commitService).deleteAllByRepositoryId(anyLong());
        doNothing().when(issueService).deleteAllByRepositoryId(anyLong());

        gitRepositoryService.reset(gitRepository);

        verify(repository, times(1)).save(any(GitRepository.class));
    }

    @Test
    public void testGetNextRepositoryInQueue() {
        when(repository.findFirstByStatusEqualsOrderByLastUpdatedAsc(any(RequestStatus.class)))
                .thenReturn(Optional.empty());

        assertTrue(gitRepositoryService.getNextRepositoryInQueue().isEmpty());
        verify(repository, times(1))
                .findFirstByStatusEqualsOrderByLastUpdatedAsc(any(RequestStatus.class));
    }

    @Test
    public void testGetRepositoryDataInTimeRange() throws ParseException {
        when(commitService.findByRepositoryIdInTimeRange(anyLong(), any(Date.class), any(Date.class)))
                .thenReturn(new ArrayList<>());
        when(issueService.findByRepositoryIdInTimeRange(anyLong(), any(Date.class), any(Date.class)))
                .thenReturn(new ArrayList<>());

        gitRepositoryService.getRepositoryDataInTimeRange(1L,
                dateFormat.parse("2020-01-01"),
                dateFormat.parse("2021-01-01"));

        verify(commitService, times(1))
                .findByRepositoryIdInTimeRange(anyLong(), any(Date.class), any(Date.class));
        verify(issueService, times(1))
                .findByRepositoryIdInTimeRange(anyLong(), any(Date.class), any(Date.class));
    }

    @Test
    public void testSetRepositoryStatus() {
        gitRepositoryService.setRepositoryStatus(gitRepository, RequestStatus.DONE);

        assertEquals(gitRepository.getStatus(), RequestStatus.DONE);
        verify(repository, times(1)).saveAndFlush(any(GitRepository.class));
    }

    @Test
    public void testCreateRepository() {
        GitRepository returned = gitRepositoryService.createRepository("url");
        assertEquals(returned.getStatus(), RequestStatus.IN_QUEUE);

        verify(repository, times(1)).save(any(GitRepository.class));
    }

    @Test
    public void testAddRepositoryDetails() {
        GHRepository ghRepository = mock(GHRepository.class);
        when(ghRepository.getDescription()).thenReturn("description");
        when(ghRepository.getName()).thenReturn("new name");
        when(ghRepository.getForksCount()).thenReturn(0);

        gitRepositoryService.addRepositoryDetails(gitRepository, ghRepository);
        assertEquals("new name", ghRepository.getName());

        verify(repository, times(1)).save(any(GitRepository.class));
    }

    @Test
    public void testMineRepositoryFail2() throws IOException {
        GHRepository ghRepository = mock(GHRepository.class);
        when(ghRepository.getDescription()).thenReturn("description");
        when(ghRepository.getName()).thenReturn("new name");
        when(ghRepository.getForksCount()).thenReturn(0);

        doThrow(IOException.class).when(labelService).mineLabels(any(GHRepository.class), any(GitRepository.class));

        assertThrows(GitRepositoryNotFoundException.class,
                () -> gitRepositoryService.mineRepository(gitRepository, ghRepository));

        verify(labelService, times(1)).mineLabels(any(GHRepository.class), any(GitRepository.class));
    }

    @Test
    public void testMineRepository() throws IOException {
        GHRepository ghRepository = mock(GHRepository.class);
        when(ghRepository.getDescription()).thenReturn("description");
        when(ghRepository.getName()).thenReturn("new name");
        when(ghRepository.getForksCount()).thenReturn(0);

        doNothing().when(labelService).mineLabels(any(GHRepository.class), any(GitRepository.class));
        doNothing().when(commitService).mineCommits(any(GitRepository.class));
        doNothing().when(issueService).mineIssues(any(GHRepository.class), any(GitRepository.class));
        doNothing().when(commitService).linkCommitsClosingIssues(any(GitRepository.class));
        doNothing().when(commitService).computeMetrics(any(GitRepository.class));

        gitRepositoryService.mineRepository(gitRepository, ghRepository);

        verify(labelService, times(1)).mineLabels(any(GHRepository.class), any(GitRepository.class));
        verify(commitService, times(1)).mineCommits(any(GitRepository.class));
        verify(issueService, times(1)).mineIssues(any(GHRepository.class), any(GitRepository.class));
        verify(commitService, times(1)).linkCommitsClosingIssues(any(GitRepository.class));
        verify(commitService, times(1)).computeMetrics(any(GitRepository.class));
    }

    @Test
    public void testRequestUpdate() {
        gitRepositoryService.requestUpdate(gitRepository);

        assertTrue(gitRepository.isUpdating());
        assertEquals(RequestStatus.IN_QUEUE, gitRepository.getStatus());
        verify(repository, times(1)).saveAndFlush(any(GitRepository.class));
    }

    @Test
    public void testUpdateRepositoryNothing() throws ParseException {
        GHRepository ghRepository = mock(GHRepository.class);
        when(ghRepository.getPushedAt()).thenReturn(dateFormat.parse("2019-01-01"));

        gitRepositoryService.updateRepository(gitRepository, ghRepository);

        assertFalse(gitRepository.isUpdating());
        assertEquals(RequestStatus.DONE, gitRepository.getStatus());

        verify(repository, times(1)).saveAndFlush(any(GitRepository.class));
    }

    @Test
    public void testUpdateRepository() throws ParseException, IOException {
        GHRepository ghRepository = mock(GHRepository.class);
        when(ghRepository.getPushedAt()).thenReturn(dateFormat.parse("2021-01-01"));

        gitRepositoryService.updateRepository(gitRepository, ghRepository);

        doNothing().when(labelService).mineNewLabels(any(GHRepository.class), any(GitRepository.class));
        doNothing().when(commitService).deleteAllByRepositoryId(anyLong());
        doNothing().when(commitService).mineCommits(any(GitRepository.class));
        doNothing().when(issueService).mineNewIssues(any(GHRepository.class), any(GitRepository.class));

        assertFalse(gitRepository.isUpdating());
        assertEquals(RequestStatus.DONE, gitRepository.getStatus());

        verify(labelService, times(1)).mineNewLabels(any(GHRepository.class), any(GitRepository.class));
        verify(commitService, times(1)).deleteAllByRepositoryId(any());
        verify(commitService, times(1)).mineCommits(any(GitRepository.class));
        verify(issueService, times(1)).mineNewIssues(any(GHRepository.class), any(GitRepository.class));
    }

    @Test
    public void testUpdateWithThrow() throws IOException, ParseException {
        GHRepository ghRepository = mock(GHRepository.class);
        when(ghRepository.getPushedAt()).thenReturn(dateFormat.parse("2021-01-01"));

        doThrow(IOException.class).when(labelService)
                .mineNewLabels(any(GHRepository.class), any(GitRepository.class));

        gitRepositoryService.updateRepository(gitRepository, ghRepository);

        assertEquals(RequestStatus.FAILED, gitRepository.getStatus());

        verify(labelService, times(1)).mineNewLabels(any(GHRepository.class), any(GitRepository.class));
        verify(commitService, times(0)).deleteAllByRepositoryId(any());
        verify(commitService, times(0)).mineCommits(any(GitRepository.class));
        verify(issueService, times(0)).mineNewIssues(any(GHRepository.class), any(GitRepository.class));
    }
}
