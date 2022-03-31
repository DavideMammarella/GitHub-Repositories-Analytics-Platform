package backend.model;

import backend.model.status.RequestStatus;
import org.junit.Before;
import org.junit.Test;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.*;

public class GitRepositoryTest {
    Issue basicIssue = new Issue();
    Commit basicCommit = new Commit();
    Label basicLabel = new Label();
    GitRepository basicGitRepository = new GitRepository();

    @Before
    public void setUp(){
        basicIssue.setId(1L);
        basicIssue.setTitle("ISSUETITLE");

        basicCommit.setId(1L);
        basicCommit.setHash("abc123");

        basicLabel.setId(1L);
        basicLabel.setName("LABELTITLE");
    }

    @Test
    public void testGetterSetter(){
        basicGitRepository.setId(1L);
        basicGitRepository.setDescription("DESC");
        basicGitRepository.setTitle("TITLE");
        basicGitRepository.setUrl("http://a.com");
        basicGitRepository.setUpdating(false);
        basicGitRepository.setForksCount(1);
        basicGitRepository.setCommits(Stream.of(basicCommit).collect(Collectors.toSet()));
        basicGitRepository.setIssues(Stream.of(basicIssue).collect(Collectors.toSet()));
        basicGitRepository.setLabels(Stream.of(basicLabel).collect(Collectors.toSet()));

        assertEquals(1L, basicGitRepository.getId().longValue());
        assertEquals("DESC", basicGitRepository.getDescription());
        assertEquals("TITLE", basicGitRepository.getTitle());
        assertEquals("http://a.com", basicGitRepository.getUrl());
        assertFalse(basicGitRepository.isUpdating());
        assertEquals(1, basicGitRepository.getForksCount());
        assertEquals(Stream.of(basicCommit).collect(Collectors.toSet()), basicGitRepository.getCommits());
        assertEquals(Stream.of(basicIssue).collect(Collectors.toSet()), basicGitRepository.getIssues());
        assertEquals(Stream.of(basicLabel).collect(Collectors.toSet()), basicGitRepository.getLabels());
    }

    @Test
    public void testReset() {
        basicGitRepository.setId(1L);
        basicGitRepository.setDescription("DESC");
        basicGitRepository.setTitle("TITLE");
        basicGitRepository.setUrl("http://a.com");
        basicGitRepository.setUpdating(false);
        basicGitRepository.setForksCount(1);
        basicGitRepository.setCommits(Stream.of(basicCommit).collect(Collectors.toSet()));
        basicGitRepository.setIssues(Stream.of(basicIssue).collect(Collectors.toSet()));
        basicGitRepository.setLabels(Stream.of(basicLabel).collect(Collectors.toSet()));

        basicGitRepository.reset();

        assertNull(basicGitRepository.getDescription());
        assertEquals(0, basicGitRepository.getForksCount());
        assertNull(basicGitRepository.getTitle());
        assertNull(basicGitRepository.getCommits());
        assertNull(basicGitRepository.getIssues());
        assertNull(basicGitRepository.getLabels());
        assertEquals(RequestStatus.IN_QUEUE, basicGitRepository.getStatus());
    }

}
