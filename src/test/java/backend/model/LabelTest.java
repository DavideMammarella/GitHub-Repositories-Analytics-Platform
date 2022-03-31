package backend.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LabelTest {

    @Test
    public void testGetterSetter() {
        GitRepository gitRepository = new GitRepository();

        Label label = new Label();

        label.setId(1L);
        label.setRepository(gitRepository);
        label.setColor("ffffff");
        label.setDescription("description");
        label.setName("name");

        assertEquals(1L, label.getId().longValue());
        assertEquals(gitRepository, label.getRepository());
        assertEquals("ffffff", label.getColor());
        assertEquals("description", label.getDescription());
        assertEquals("name", label.getName());
    }

}
