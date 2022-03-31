package backend.parser;

import backend.model.Commit;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ParserTest {

    @Test
    public void testUnmarshallJSONtoPOJO() throws IOException {
        List<Commit> commitList = CommitsJSONtoPOJO.run("src/test/java/backend/parser/sample.json");
        assertEquals(2,commitList.size());
    }

}
