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
public class GitRepositorySmallDTO {

    private Long id;

    private String title;

    private String url;

    private Date lastUpdated;

    private RequestStatus status;

    public GitRepositorySmallDTO(GitRepository repository) {
        this.id = repository.getId();
        this.title = repository.getTitle();
        this.url = repository.getUrl();
        this.lastUpdated = repository.getLastUpdated();
        this.status = repository.getStatus();
    }
}
