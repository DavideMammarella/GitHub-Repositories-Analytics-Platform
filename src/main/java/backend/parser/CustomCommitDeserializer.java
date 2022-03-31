package backend.parser;

import backend.model.Commit;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Component
public class CustomCommitDeserializer extends StdDeserializer<Commit> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomCommitDeserializer.class);

    public CustomCommitDeserializer() {
        this(null);
    }

    public CustomCommitDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Commit deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {

        ObjectCodec codec = jsonParser.getCodec();
        JsonNode node = codec.readTree(jsonParser);

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z");

        Commit commit = null;
        try {
            commit = Commit
                    .builder()
                    .hash(node.get("commit").asText())
                    .author(node.get("author").asText())
                    .createdAt(dateFormat.parse(node.get("date").asText()))
                    .message(node.get("message").asText())
                    .body(node.get("body").asText())
                    .build();
        } catch (ParseException e) {
            LOGGER.error(e.getMessage());
        }

        if (commit != null) {
            commit.setAddedFiles(getFiles((ArrayNode) node.get("added_files")));
            commit.setModifiedFiles(getFiles((ArrayNode) node.get("modified_files")));
            commit.setDeletedFiles(getFiles((ArrayNode) node.get("deleted_files")));
        }

        return commit;
    }

    private Set<String> getFiles(ArrayNode filesNode) {
        Iterator<JsonNode> itr = filesNode.elements();
        Set<String> files = new HashSet<>();

        while (itr.hasNext()) {
            files.add(itr.next().asText());
        }

        return files;
    }
}

