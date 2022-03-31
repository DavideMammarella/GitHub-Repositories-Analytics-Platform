package backend.model.dto;

import backend.model.status.RequestStatus;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GitRepositoryDTOTest {

    @Test
    public void testNoArgsConstructor() {
        GitRepositoryDTO gitRepositoryDTO = new GitRepositoryDTO();

        gitRepositoryDTO.setId(1L);
        gitRepositoryDTO.setDescription("description");
        gitRepositoryDTO.setForksCount(1);
        gitRepositoryDTO.setCommitsCount(1);
        gitRepositoryDTO.setIssuesCount(1);
        gitRepositoryDTO.setLabelsCount(1);
        gitRepositoryDTO.setTitle("title");
        gitRepositoryDTO.setUrl("google/guava");
        gitRepositoryDTO.setLastUpdated(new Date());
        gitRepositoryDTO.setStatus(RequestStatus.DONE);

        check(gitRepositoryDTO);
    }

    @Test
    public void testAllArgsConstructor() {
        GitRepositoryDTO gitRepositoryDTO = new GitRepositoryDTO(1L, "description", 1, 1,
                1, 1, "title", "google/guava", new Date(), RequestStatus.DONE);

        check(gitRepositoryDTO);
    }

    @Test
    public void testBuilder() {
        GitRepositoryDTO gitRepositoryDTO = GitRepositoryDTO
                .builder()
                .id(1L)
                .description("description")
                .forksCount(1)
                .title("title")
                .url("google/guava")
                .lastUpdated(new Date())
                .status(RequestStatus.DONE)
                .commitsCount(1)
                .issuesCount(1)
                .labelsCount(1)
                .build();

        check(gitRepositoryDTO);
    }

    private void check(GitRepositoryDTO gitRepositoryDTO) {
        assertEquals(1L, gitRepositoryDTO.getId().longValue());
        assertEquals("description", gitRepositoryDTO.getDescription());
        assertEquals(1, gitRepositoryDTO.getForksCount());
        assertEquals("title", gitRepositoryDTO.getTitle());
        assertEquals("google/guava", gitRepositoryDTO.getUrl());
        assertNotNull(gitRepositoryDTO.getLastUpdated());
        assertEquals(RequestStatus.DONE, gitRepositoryDTO.getStatus());
        assertEquals(1, gitRepositoryDTO.getCommitsCount());
        assertEquals(1, gitRepositoryDTO.getIssuesCount());
        assertEquals(1, gitRepositoryDTO.getLabelsCount());
    }
}
