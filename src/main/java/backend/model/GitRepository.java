package backend.model;

import backend.model.status.RequestStatus;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Entity
@NoArgsConstructor
@Table(name ="repository")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        scope = GitRepository.class)
public class GitRepository extends BaseEntity {

    private String description;

    private int forksCount;

    private int commitsCount;

    private int issuesCount;

    private int labelsCount;

    private String title;

    private String url;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "repository")
    private Set<Commit> commits;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "repository")
    private Set<Issue> issues;

    @OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER, mappedBy = "repository")
    private Set<Label> labels;

    private Date lastUpdated;

    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    private boolean isUpdating;

    public void reset() {
        this.description = null;
        this.forksCount = 0;
        this.title = null;
        this.commits = null;
        this.issues = null;
        this.labels = null;
        this.lastUpdated = new Date();
        this.status = RequestStatus.IN_QUEUE;
    }
}
