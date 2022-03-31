package backend.model.dto;

import backend.model.Commit;
import backend.model.Issue;
import lombok.*;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommitDTO {

    private Long id;

    private String hash;

    private String author;

    private Date createdAt;

    private String message;

    private String body;

    private Set<String> addedFiles;

    private Set<String> modifiedFiles;

    private Set<String> deletedFiles;

    private Set<Long> closingIssues;

    private double averageCboDifference;

    private double averageLocDifference;

    private double averageWmcDifference;

    private double averageLcomDifference;

    public CommitDTO(Commit commit) {
        this.id = commit.getId();
        this.hash = commit.getHash();
        this.author = commit.getAuthor();
        this.createdAt = commit.getCreatedAt();
        this.message = commit.getMessage();
        this.body = commit.getBody();
        this.addedFiles = commit.getAddedFiles();
        this.modifiedFiles = commit.getModifiedFiles();
        this.deletedFiles = commit.getDeletedFiles();
        this.closingIssues = commit.getClosingIssues()
                .stream()
                .map(Issue::getId)
                .collect(Collectors.toSet());
        this.averageCboDifference = commit.getAverageCboDifference();
        this.averageLocDifference = commit.getAverageLocDifference();
        this.averageWmcDifference = commit.getAverageWmcDifference();
        this.averageLcomDifference = commit.getAverageLcomDifference();
    }
}
