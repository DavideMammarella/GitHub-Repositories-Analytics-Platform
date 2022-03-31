package backend.controller;

import backend.model.GitRepository;
import backend.model.dto.GitRepositoryDataDTO;
import backend.model.status.RequestStatus;
import backend.service.GitRepositoryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {GitRepositoryController.class})
@RunWith(SpringRunner.class)
public class GitRepositoryControllerTest {

    @Autowired
    private GitRepositoryController gitRepositoryController;

    @MockBean
    private GitRepositoryService gitRepositoryService;

    @Mock
    private GitRepository gitRepository;

    @Test
    public void testGetRepositoriesPage() {
        when(gitRepositoryService.findAllPage(any(Pageable.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        Pageable pageable = PageRequest.of(1, 3);
        gitRepositoryController.getRepositoriesPage(pageable);

        verify(gitRepositoryService, times(1)).findAllPage(any(Pageable.class));
    }

    @Test
    public void testGetSingleRepositoryById() throws Exception {
        when(gitRepositoryService.findById(any(Long.class))).thenReturn(gitRepository);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/repository/{id}", 1L);

        MockMvcBuilders.standaloneSetup(gitRepositoryController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetGitRepositoryStatus() throws Exception {
        when(gitRepositoryService.findById(anyLong())).thenReturn(gitRepository);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/repository/{id}/status", 1L);

        MockMvcBuilders.standaloneSetup(gitRepositoryController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetGitRepositoryIssues() throws Exception {
        when(gitRepositoryService.findById(anyLong())).thenReturn(gitRepository);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/repository/{id}/issues", 1L);

        MockMvcBuilders.standaloneSetup(gitRepositoryController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetGitRepositoryLabels() throws Exception {
        when(gitRepositoryService.findById(anyLong())).thenReturn(gitRepository);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/repository/{id}/labels", 1L);

        MockMvcBuilders.standaloneSetup(gitRepositoryController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetDataInTimeRange() throws Exception {
        when(gitRepositoryService.getRepositoryDataInTimeRange(anyLong(), any(Date.class), any(Date.class)))
                .thenReturn(new GitRepositoryDataDTO());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get("/repository/{id}/range?start={start}&end={end}", 1L, "2020-01-01", "2021-01-01");

        MockMvcBuilders.standaloneSetup(gitRepositoryController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testUpdateRepository() throws Exception {
        when(gitRepositoryService.findById(any(Long.class))).thenReturn(gitRepository);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .patch("/repository/{id}", 1L);

        MockMvcBuilders.standaloneSetup(gitRepositoryController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testSubmitRepositoryRequest() throws Exception {
        when(gitRepositoryService.createRepository(any(String.class))).thenReturn(gitRepository);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/repository")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"url\":\"google/guava\"}");

        MockMvcBuilders.standaloneSetup(gitRepositoryController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated());

    }

    @Test
    public void testSubmitRepositoryRequestUpdate() throws Exception {
        when(gitRepositoryService.findByUrl(any(String.class))).thenReturn(Optional.of(gitRepository));
        when(gitRepository.getStatus()).thenReturn(RequestStatus.FAILED);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/repository")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"url\":\"google/guava\"}");

        MockMvcBuilders.standaloneSetup(gitRepositoryController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


}
