package backend.model.dto;

import backend.model.Issue;
import lombok.*;

import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IssueSmallDTO {

    private Long id;

    private Date createdAt;

    private Date closedAt;

    public IssueSmallDTO(Issue issue) {
        this.id = issue.getId();
        this.createdAt = issue.getCreatedAt();
        this.closedAt = issue.getClosedAt();
    }
}