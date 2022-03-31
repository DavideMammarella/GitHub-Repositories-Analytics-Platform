package backend.model.dto;


import backend.model.Issue;
import backend.model.dto.IssueSmallDTO;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class IssueSmallDTOTest {

    @Test
    public void testAllArgsConstructor() {
        IssueSmallDTO issueSmallDTO = new IssueSmallDTO(1L, new Date(), new Date());

        assertEquals(1L, issueSmallDTO.getId().longValue());
        assertNotNull(issueSmallDTO.getCreatedAt());
        assertNotNull(issueSmallDTO.getClosedAt());
    }

    @Test
    public void testGetterSetter() {
        IssueSmallDTO issueSmallDTO = new IssueSmallDTO();

        issueSmallDTO.setId(1L);
        issueSmallDTO.setCreatedAt(new Date());
        issueSmallDTO.setClosedAt(new Date());

        assertEquals(1L, issueSmallDTO.getId().longValue());
        assertNotNull(issueSmallDTO.getCreatedAt());
        assertNotNull(issueSmallDTO.getClosedAt());
    }

    @Test
    public void testBuilder() {
        IssueSmallDTO issueSmallDTO = IssueSmallDTO
                .builder()
                .id(1L)
                .createdAt(new Date())
                .closedAt(new Date())
                .build();

        assertEquals(1L, issueSmallDTO.getId().longValue());
        assertNotNull(issueSmallDTO.getCreatedAt());
        assertNotNull(issueSmallDTO.getClosedAt());
    }

    @Test
    public void testConstructorFromIssue() {
        Issue issue = Issue
                .builder()
                .createdAt(new Date())
                .closedAt(new Date())
                .build();

        IssueSmallDTO issueSmallDTO = new IssueSmallDTO(issue);

        assertNull(issueSmallDTO.getId());
        assertNotNull(issueSmallDTO.getCreatedAt());
        assertNotNull(issueSmallDTO.getClosedAt());
    }
}
