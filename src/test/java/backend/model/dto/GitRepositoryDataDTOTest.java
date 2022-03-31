package backend.model.dto;

import backend.model.dto.GitRepositoryDataDTO;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class GitRepositoryDataDTOTest {

    @Test
    public void testSetter() {
        GitRepositoryDataDTO gitRepositoryDataDTO = new GitRepositoryDataDTO();

        gitRepositoryDataDTO.setCommits(new ArrayList<>());
        gitRepositoryDataDTO.setIssues(new ArrayList<>());

        assertEquals(new ArrayList<>(), gitRepositoryDataDTO.getCommits());
        assertEquals(new ArrayList<>(), gitRepositoryDataDTO.getIssues());
    }
}
