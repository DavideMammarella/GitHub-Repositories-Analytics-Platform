package backend.model;

import backend.model.status.IssueStatus;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class IssueTest {

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Test
    public void testGetterSetter() throws ParseException {
        Issue issue = new Issue();

        issue.setId(1L);
        issue.setRepository(new GitRepository());
        issue.setBody("body");
        issue.setClosedAt(null);
        issue.setCreatedAt(dateFormat.parse("2021-01-01"));
        issue.setLabels(new HashSet<>());
        issue.setNumber(1);
        issue.setStatus(IssueStatus.OPEN);
        issue.setTitle("title");
        issue.setClosedByCommits(new HashSet<>());

        assertEquals(1L, issue.getId().longValue());
        assertEquals("body", issue.getBody());
        assertNull(issue.getClosedAt());
        assertEquals(dateFormat.parse("2021-01-01"), issue.getCreatedAt());
        assertEquals(new HashSet<>(), issue.getLabels());
        assertEquals(1, issue.getNumber());
        assertEquals(IssueStatus.OPEN, issue.getStatus());
        assertEquals("title", issue.getTitle());
        assertEquals(new HashSet<>(), issue.getClosedByCommits());
    }
}
