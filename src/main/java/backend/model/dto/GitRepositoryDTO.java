package backend.model.dto;

import backend.model.GitRepository;
import backend.model.status.RequestStatus;
import lombok.*;

import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GitRepositoryDTO {

    private Long id;

    private String description;

    private int forksCount;

    private int commitsCount;

    private int issuesCount;

    private int labelsCount;

    private String title;

    private String url;

    private Date lastUpdated;

    private RequestStatus status;

    public GitRepositoryDTO(GitRepository repository) {
        this.id = repository.getId();
        this.description = repository.getDescription();
        this.forksCount = repository.getForksCount();
        this.title = repository.getTitle();
        this.url = repository.getUrl();
        this.lastUpdated = repository.getLastUpdated();
        this.status = repository.getStatus();
        this.commitsCount = repository.getCommitsCount();
        this.issuesCount = repository.getIssuesCount();
        this.labelsCount = repository.getLabelsCount();
    }
}
