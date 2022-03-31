package backend.model.dto;

import backend.model.dto.GitRepositorySmallDTO;
import backend.model.status.RequestStatus;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GitRepositorySmallDTOTest {

    @Test
    public void testNoArgsConstructor() {
        GitRepositorySmallDTO gitRepositorySmallDTO = new GitRepositorySmallDTO();

        gitRepositorySmallDTO.setId(1L);
        gitRepositorySmallDTO.setTitle("title");
        gitRepositorySmallDTO.setUrl("google/guava");
        gitRepositorySmallDTO.setLastUpdated(new Date());
        gitRepositorySmallDTO.setStatus(RequestStatus.DONE);

        check(gitRepositorySmallDTO);
    }

    @Test
    public void testAllArgsConstructor() {
        GitRepositorySmallDTO gitRepositorySmallDTO = new GitRepositorySmallDTO(1L, "title", "google/guava", new Date(), RequestStatus.DONE);

        check(gitRepositorySmallDTO);
    }

    @Test
    public void testBuilder() {
        GitRepositorySmallDTO gitRepositorySmallDTO = GitRepositorySmallDTO
                .builder()
                .id(1L)
                .title("title")
                .url("google/guava")
                .lastUpdated(new Date())
                .status(RequestStatus.DONE)
                .build();

        check(gitRepositorySmallDTO);
    }

    private void check(GitRepositorySmallDTO gitRepositorySmallDTO) {
        assertEquals(1L, gitRepositorySmallDTO.getId().longValue());
        assertEquals("title", gitRepositorySmallDTO.getTitle());
        assertEquals("google/guava", gitRepositorySmallDTO.getUrl());
        assertNotNull(gitRepositorySmallDTO.getLastUpdated());
        assertEquals(RequestStatus.DONE, gitRepositorySmallDTO.getStatus());
        assertEquals(1L, gitRepositorySmallDTO.getId().longValue());
        assertEquals("title", gitRepositorySmallDTO.getTitle());
        assertEquals("google/guava", gitRepositorySmallDTO.getUrl());
        assertNotNull(gitRepositorySmallDTO.getLastUpdated());
        assertEquals(RequestStatus.DONE, gitRepositorySmallDTO.getStatus());
        assertEquals(1L, gitRepositorySmallDTO.getId().longValue());
        assertEquals("title", gitRepositorySmallDTO.getTitle());
        assertEquals("google/guava", gitRepositorySmallDTO.getUrl());
        assertNotNull(gitRepositorySmallDTO.getLastUpdated());
        assertEquals(RequestStatus.DONE, gitRepositorySmallDTO.getStatus());
    }
}
