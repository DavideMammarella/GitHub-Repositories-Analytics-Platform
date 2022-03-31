package backend.model.dto;

import backend.model.dto.IssueDTO;
import backend.model.status.IssueStatus;
import org.junit.Test;

import java.util.Date;
import java.util.HashSet;

import static org.junit.Assert.*;

public class IssueDTOTest {

    @Test
    public void testNoArgsConstructor() {
        IssueDTO issueDTO = new IssueDTO();

        issueDTO.setId(1L);
        issueDTO.setBody("body");
        issueDTO.setCreatedAt(new Date());
        issueDTO.setClosedAt(new Date());
        issueDTO.setLabels(new HashSet<>());
        issueDTO.setNumber(1);
        issueDTO.setStatus(IssueStatus.CLOSED);
        issueDTO.setTitle("title");
        issueDTO.setClosedByCommits(new HashSet<>());

        check(issueDTO);
    }

    @Test
    public void testAllArgsConstructor() {
        IssueDTO issueDTO = new IssueDTO(1L, "body", new Date(), new Date(), new HashSet<>(),
                1, IssueStatus.CLOSED, "title", new HashSet<>());

        check(issueDTO);
    }

    @Test
    public void testBuilder() {
        IssueDTO issueDTO = IssueDTO
                .builder()
                .id(1L)
                .body("body")
                .createdAt(new Date())
                .closedAt(new Date())
                .labels(new HashSet<>())
                .number(1)
                .status(IssueStatus.CLOSED)
                .title("title")
                .closedByCommits(new HashSet<>())
                .build();

        check(issueDTO);
    }

    private void check(IssueDTO issueDTO) {
        assertEquals(1L, issueDTO.getId().longValue());
        assertEquals("body", issueDTO.getBody());
        assertNotNull(issueDTO.getCreatedAt());
        assertNotNull(issueDTO.getClosedAt());
        assertEquals(new HashSet<>(), issueDTO.getLabels());
        assertEquals(1, issueDTO.getNumber());
        assertEquals(IssueStatus.CLOSED, issueDTO.getStatus());
        assertEquals(new HashSet<>(), issueDTO.getClosedByCommits());
    }
}
