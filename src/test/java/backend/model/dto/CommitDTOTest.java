package backend.model.dto;

import backend.model.Commit;
import backend.model.Issue;
import backend.model.dto.CommitDTO;
import org.junit.Test;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CommitDTOTest {

    @Test
    public void testNoArgsConstructor() {
        CommitDTO commitDTO = new CommitDTO();

        commitDTO.setId(1L);
        commitDTO.setHash("hash");
        commitDTO.setAuthor("author");
        commitDTO.setCreatedAt(new Date());
        commitDTO.setMessage("message");
        commitDTO.setBody("body");
        commitDTO.setAddedFiles(new HashSet<>());
        commitDTO.setModifiedFiles(new HashSet<>());
        commitDTO.setDeletedFiles(new HashSet<>());
        commitDTO.setClosingIssues(new HashSet<>());
        commitDTO.setAverageCboDifference(1D);
        commitDTO.setAverageLocDifference(1D);
        commitDTO.setAverageWmcDifference(1D);
        commitDTO.setAverageLcomDifference(1D);

        check(commitDTO);
    }

    @Test
    public void testAllArgsConstructor() {
        CommitDTO commitDTO = new CommitDTO(1L, "hash", "author", new Date(), "message", "body", new HashSet<>(),
                                            new HashSet<>(), new HashSet<>(), new HashSet<>(), 1D, 1D, 1D, 1D);

        check(commitDTO);
    }

    @Test
    public void testBuilder() {
        CommitDTO commitDTO = CommitDTO
                .builder()
                .id(1L)
                .hash("hash")
                .author("author")
                .createdAt(new Date())
                .message("message")
                .body("body")
                .addedFiles(new HashSet<>())
                .modifiedFiles(new HashSet<>())
                .deletedFiles(new HashSet<>())
                .closingIssues(new HashSet<>())
                .averageCboDifference(1D)
                .averageLocDifference(1D)
                .averageWmcDifference(1D)
                .averageLcomDifference(1D)
                .build();

        check(commitDTO);
    }

    @Test
    public void testConstructorFromCommit() {
        Commit commit = Commit
                .builder()
                .message("message")
                .closingIssues(Collections.singleton(new Issue()))
                .build();

        CommitDTO commitDTO = new CommitDTO(commit);

        assertEquals(commit.getMessage(), commitDTO.getMessage());
        assertEquals(1, commitDTO.getClosingIssues().size());
    }

    private void check(CommitDTO commitDTO) {
        assertEquals(1L, commitDTO.getId().longValue());
        assertEquals("hash", commitDTO.getHash());
        assertEquals("author", commitDTO.getAuthor());
        assertNotNull(commitDTO.getCreatedAt());
        assertEquals("message", commitDTO.getMessage());
        assertEquals("body", commitDTO.getBody());
        assertEquals(new HashSet<>(), commitDTO.getAddedFiles());
        assertEquals(new HashSet<>(), commitDTO.getModifiedFiles());
        assertEquals(new HashSet<>(), commitDTO.getDeletedFiles());
        assertEquals(new HashSet<>(), commitDTO.getClosingIssues());
        assertEquals(1D, commitDTO.getAverageCboDifference(), 0);
        assertEquals(1D, commitDTO.getAverageLocDifference(), 0);
        assertEquals(1D, commitDTO.getAverageWmcDifference(), 0);
        assertEquals(1D, commitDTO.getAverageLcomDifference(), 0);
    }
}
