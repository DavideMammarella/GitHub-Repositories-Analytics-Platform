package backend.model.dto;

import backend.model.Commit;
import backend.model.Issue;
import backend.model.status.IssueStatus;
import backend.model.Label;
import lombok.*;

import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IssueDTO {

    private Long id;

    private String body;

    private Date createdAt;

    private Date closedAt;

    private Set<Label> labels;

    private int number;

    private IssueStatus status;

    private String title;

    private Set<Long> closedByCommits;

    public IssueDTO(Issue issue) {
        this.id = issue.getId();
        this.body = issue.getBody();
        this.closedAt = issue.getClosedAt();
        this.createdAt = issue.getCreatedAt();
        this.labels = issue.getLabels();
        this.number = issue.getNumber();
        this.status = issue.getStatus();
        this.title = issue.getTitle();
        this.closedByCommits = issue.getClosedByCommits()
                .stream()
                .map(Commit::getId)
                .collect(Collectors.toSet());
    }
}