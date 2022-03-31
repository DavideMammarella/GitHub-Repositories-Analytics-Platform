package backend.controller;

import backend.model.GitRepository;
import backend.model.Issue;
import backend.model.status.IssueStatus;
import backend.service.IssueService;
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

import java.text.SimpleDateFormat;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(MockitoJUnitRunner.class)
public class IssueControllerTest {

    private MockMvc mockMvc;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    @Mock
    private IssueService issueService;

    @Before
    public void setUp() {
        IssueController issueController = new IssueController(issueService);
        this.mockMvc = MockMvcBuilders.standaloneSetup(issueController).build();
    }

    @Test
    public void testGetSingleIssueById() throws Exception {
        final Issue issue = Issue
                .builder()
                .body("BODY")
                .number(1)
                .status(IssueStatus.OPEN)
                .title("TITLE")
                .closedByCommits(new HashSet<>())
                .repository(new GitRepository())
                .build();

        when(issueService.findById(Mockito.any(Long.class))).thenReturn(issue);

        String expectedJson = "{\"id\":null," +
                "\"body\":\"BODY\"," +
                "\"createdAt\":null," +
                "\"closedAt\":null," +
                "\"labels\":null," +
                "\"number\":1," +
                "\"status\":\"OPEN\"," +
                "\"title\":\"TITLE\"," +
                "\"closedByCommits\":[]}";

        MvcResult result = mockMvc.perform(get("/issue/{id}", 1L))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals(expectedJson, result.getResponse().getContentAsString());
    }
}
