package backend.parser;

import backend.model.Commit;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class CommitsJSONtoPOJO {

    private CommitsJSONtoPOJO() { }

    public static List<Commit> run(String pathToFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Commit.class, new CustomCommitDeserializer());
        mapper.registerModule(module);

        File jsonInput = new File(pathToFile);

        return mapper.readValue(jsonInput, mapper.getTypeFactory().constructCollectionType(List.class, Commit.class));
    }
}
