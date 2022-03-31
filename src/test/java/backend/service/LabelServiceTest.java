package backend.service;

import backend.model.GitRepository;
import backend.model.Label;
import backend.repository.LabelRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.PagedIterable;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {LabelServiceImpl.class})
@ExtendWith(SpringExtension.class)
public class LabelServiceTest {

    @MockBean
    private LabelRepository labelRepository;

    @Autowired
    private LabelServiceImpl labelService;

    @Mock
    private Label label;

    @Mock
    private final GitRepository gitRepository = new GitRepository();

    @Mock
    private final GHRepository ghRepository = new GHRepository();

    @Mock
    private PagedIterable<GHLabel> it;

    @Mock
    private GHLabel ghLabel;

    @BeforeEach
    public void setUp() {
        label = Label
                .builder()
                .repository(gitRepository)
                .name("name")
                .build();
    }

    @Test
    public void testFindByRepositoryIdAndName() {
        when(labelService.findByRepositoryIdAndName(any(Long.class), any(String.class)))
                .thenReturn(Optional.of(label));

        assertEquals(label, labelService.findByRepositoryIdAndName(1L, "name").orElse(null));
    }

    @Test
    public void testDeleteAllByRepositoryId() {
        labelService.deleteAllByRepositoryId(label.getRepository().getId());
        verify(labelRepository, times(1)).deleteAllByRepositoryId(label.getRepository().getId());
    }


    @Test
    public void testMineLabels() throws IOException {
        List<GHLabel> ghLabels = new ArrayList<>();
        ghLabels.add(ghLabel);

        when(ghLabel.getName()).thenReturn("name");
        when(ghLabel.getColor()).thenReturn("ffffff");
        when(ghLabel.getDescription()).thenReturn("description");


        when(ghRepository.listLabels()).thenReturn(it);
        when(it.toList()).thenReturn(ghLabels);


        labelService.mineLabels(ghRepository, label.getRepository());
        verify(labelRepository, times(1)).saveAll(anyIterable());
    }

    @Test
    public void testMineNewLabels() throws IOException {
        List<GHLabel> ghLabels = new ArrayList<>();
        ghLabels.add(ghLabel);

        when(ghRepository.listLabels()).thenReturn(it);
        when(it.toList()).thenReturn(ghLabels);

        when(labelRepository.findByRepositoryIdAndName(any(Long.class), any(String.class)))
                .thenReturn(Optional.of(label));

        labelService.mineNewLabels(ghRepository, label.getRepository());
        verify(labelRepository, times(1)).saveAll(anyIterable());
    }
}
