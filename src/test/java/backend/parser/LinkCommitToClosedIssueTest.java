package backend.parser;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;

public class LinkCommitToClosedIssueTest {

    @Test
    public void testHashtagFixCloseResolveFinder(){
        Set<Integer> expectedClosingIssues = Stream.of(123, 456).collect(Collectors.toCollection(HashSet::new));
        Set<Integer> actualClosingIssues = LinkCommitToClosedIssue.fixCloseResolveFinder("Fix #123 abc123./# Fix #456", "google/guava");
        assertEquals(expectedClosingIssues, actualClosingIssues);

        Set<Integer> expectedClosingIssues1 = Stream.of(1011).collect(Collectors.toCollection(HashSet::new));
        Set<Integer> actualClosingIssues1 = LinkCommitToClosedIssue.fixCloseResolveFinder("Close [External Bug No Issue] #789 Closed #1011", "google/guava");
        assertEquals(expectedClosingIssues1, actualClosingIssues1);
    }

    @Test
    public void testUrlFixCloseResolveFinder() {
        Set<Integer> expectedClosingIssues = Stream.of(1, 2).collect(Collectors.toCollection(HashSet::new));
        Set<Integer> actualClosingIssues = LinkCommitToClosedIssue.fixCloseResolveFinder("http #436345 Fixed https://www.github.com/google/guava/issues/1. Closed https://github.com/google/guava/issues/2 12adfda", "google/guava");
        assertEquals(expectedClosingIssues, actualClosingIssues);
    }

    @Test
    public void testVoidFixCloseResolveFinder() {
        Set<Integer> expectedClosingIssues = new HashSet<>();
        Set<Integer> actualClosingIssues = LinkCommitToClosedIssue.fixCloseResolveFinder("No issue fix here!", "google/guava");
        assertEquals(expectedClosingIssues, actualClosingIssues);
    }
}
