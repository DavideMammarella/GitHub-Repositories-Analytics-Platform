package backend.model;

import backend.model.status.RequestStatus;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CommitTest {

    @Test
    public void testGetterSetter() {
        Commit basicCommit = new Commit();

        Set<String> modifiedFile = new HashSet<>();
        Set<String> addedFile = new HashSet<>();
        Set<String> deletedFile = new HashSet<>();

        modifiedFile.add("file1");
        addedFile.add("file2");
        deletedFile.add("file3");

        basicCommit.setId(1L);
        basicCommit.setHash("abc123");
        basicCommit.setAuthor("AUT");
        basicCommit.setMessage("MSG");
        basicCommit.setBody("BODY");
        basicCommit.setCreatedAt(java.sql.Date.valueOf("2020-01-01"));

        basicCommit.setAverageCboDifference(1D);
        basicCommit.setAverageLocDifference(2D);
        basicCommit.setAverageWmcDifference(3D);
        basicCommit.setAverageLcomDifference(4D);

        assertEquals(1L, basicCommit.getId().longValue());
        assertEquals( "abc123", basicCommit.getHash());
        assertEquals("AUT", basicCommit.getAuthor());
        assertEquals("MSG", basicCommit.getMessage());
        assertEquals("BODY", basicCommit.getBody());
        assertEquals(java.sql.Date.valueOf("2020-01-01"), basicCommit.getCreatedAt());
        assertEquals(1D, basicCommit.getAverageCboDifference(), 0);
        assertEquals(2D, basicCommit.getAverageLocDifference(), 0);
        assertEquals(3D, basicCommit.getAverageWmcDifference(), 0);
        assertEquals(4D, basicCommit.getAverageLcomDifference(), 0);

        basicCommit.setModifiedFiles(modifiedFile);
        basicCommit.setAddedFiles(addedFile);
        basicCommit.setDeletedFiles(deletedFile);

        assertEquals(modifiedFile, basicCommit.getModifiedFiles());
        assertEquals(addedFile, basicCommit.getAddedFiles());
        assertEquals(deletedFile, basicCommit.getDeletedFiles());
    }

    @Test
    public void testEquals() {
        final GitRepository repository = GitRepository
                .builder()
                .url("http://url.com")
                .lastUpdated(java.sql.Date.valueOf("2020-01-01"))
                .status(RequestStatus.IN_QUEUE)
                .build();

        final Commit commit1 = Commit
                .builder()
                .repository(repository)
                .hash("abc123")
                .author("AUTHOR")
                .createdAt(java.sql.Date.valueOf("2020-01-01"))
                .message("MESSAGE")
                .body("BODY")
                .build();

        assertEquals(commit1, commit1);
        assertNotNull(commit1);
    }

    @Test
    public void testHashCode() {
        final GitRepository repository = GitRepository
                .builder()
                .url("http://url.com")
                .lastUpdated(java.sql.Date.valueOf("2020-01-01"))
                .status(RequestStatus.IN_QUEUE)
                .build();

        final Commit commit = Commit
                .builder()
                .repository(repository)
                .hash("abc123")
                .author("AUTHOR")
                .createdAt(java.sql.Date.valueOf("2020-01-01"))
                .message("MESSAGE")
                .body("BODY")
                .build();

        final String hash2 = "abc123";

        assertEquals(hash2, commit.getHash());
        assertNotNull(commit.getHash());
    }

    @Test
    public void testToString() {
        Commit commit = new Commit();

        String expectedString = "Commit(hash=null," +
                " author=null," +
                " createdAt=null," +
                " message=null," +
                " body=null," +
                " addedFiles=null," +
                " modifiedFiles=null," +
                " deletedFiles=null," +
                " closingIssues=null," +
                " averageCboDifference=0.0," +
                " averageLocDifference=0.0," +
                " averageWmcDifference=0.0," +
                " averageLcomDifference=0.0)";

        assertEquals(expectedString, commit.toString());
    }
}
