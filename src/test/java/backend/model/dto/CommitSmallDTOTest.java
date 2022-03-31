package backend.model.dto;

import backend.model.Commit;
import backend.model.dto.CommitSmallDTO;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class CommitSmallDTOTest {

    @Test
    public void testAllArgsConstructor() {
        CommitSmallDTO commitSmallDTO = new CommitSmallDTO(1L, new Date());

        assertEquals(1L, commitSmallDTO.getId().longValue());
        assertNotNull(commitSmallDTO.getCreatedAt());
    }

    @Test
    public void testGetterSetter() {
        CommitSmallDTO commitSmallDTO = new CommitSmallDTO();

        commitSmallDTO.setId(1L);
        commitSmallDTO.setCreatedAt(new Date());

        assertEquals(1L, commitSmallDTO.getId().longValue());
        assertNotNull(commitSmallDTO.getCreatedAt());
    }

    @Test
    public void testBuilder() {
        CommitSmallDTO commitSmallDTO = CommitSmallDTO
                .builder()
                .id(1L)
                .createdAt(new Date())
                .build();

        assertEquals(1L, commitSmallDTO.getId().longValue());
        assertNotNull(commitSmallDTO.getCreatedAt());
    }

    @Test
    public void testConstructorFromCommit() {
        Commit commit = Commit
                .builder()
                .createdAt(new Date())
                .build();

        CommitSmallDTO commitSmallDTO = new CommitSmallDTO(commit);

        assertNull(commitSmallDTO.getId());
        assertNotNull(commitSmallDTO.getCreatedAt());
    }
}
