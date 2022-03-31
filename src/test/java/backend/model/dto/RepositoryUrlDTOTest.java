package backend.model.dto;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RepositoryUrlDTOTest {

    @Test
    public void testNoArgsConstructor() {
        RepositoryUrlDTO repositoryUrlDTO = new RepositoryUrlDTO();

        repositoryUrlDTO.setUrl("google/guava");

        assertEquals("google/guava", repositoryUrlDTO.getUrl());
    }

    @Test
    public void testAllArgsConstructor() {
        RepositoryUrlDTO repositoryUrlDTO = new RepositoryUrlDTO("google/guava");

        assertEquals("google/guava", repositoryUrlDTO.getUrl());
    }

    @Test
    public void testBuilder() {
        RepositoryUrlDTO repositoryUrlDTO = RepositoryUrlDTO
                .builder()
                .url("google/guava")
                .build();

        assertEquals("google/guava", repositoryUrlDTO.getUrl());
    }
}
