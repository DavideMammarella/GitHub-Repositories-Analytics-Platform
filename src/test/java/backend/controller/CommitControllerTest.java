package backend.controller;

import backend.model.Commit;
import backend.model.dto.CommitDTO;
import backend.model.GitRepository;
import backend.model.status.RequestStatus;
import backend.service.CommitService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(MockitoJUnitRunner.class)
public class CommitControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CommitService commitServiceMock;

    @Before
    public void setUp() {
        CommitController commitController = new CommitController(commitServiceMock);
        this.mockMvc = MockMvcBuilders.standaloneSetup(commitController).build();

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
                .message("MESSAGE")
                .body("BODY")
                .closingIssues(new HashSet<>())
                .build();

        final CommitDTO commitDTO = new CommitDTO(commit);

        when(commitServiceMock.findById(Mockito.any(Long.class))).thenReturn(commit);
        assertEquals(repository.getId(), commit.getRepository().getId());
    }

    @Test
    public void testGetSingleCommitById() throws Exception {
        String expectedJSON = "{\"id\":null," +
                "\"hash\":\"abc123\"," +
                "\"author\":\"AUTHOR\"," +
                "\"createdAt\":null," +
                "\"message\":\"MESSAGE\"," +
                "\"body\":\"BODY\"," +
                "\"addedFiles\":null," +
                "\"modifiedFiles\":null," +
                "\"deletedFiles\":null," +
                "\"closingIssues\":[]," +
                "\"averageCboDifference\":0.0," +
                "\"averageLocDifference\":0.0," +
                "\"averageWmcDifference\":0.0," +
                "\"averageLcomDifference\":0.0}";

        MvcResult result = this.mockMvc.perform(get("/commit/{id}", 1L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        String actualJSON = result.getResponse().getContentAsString();
        assertEquals(expectedJSON, actualJSON);
    }
}
