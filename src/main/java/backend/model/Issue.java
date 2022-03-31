package backend.model;

import backend.model.status.IssueStatus;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "issue")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        scope = Issue.class)
public class Issue extends BaseEntity {

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    private GitRepository repository;

    @Column(columnDefinition = "text")
    private String body;

    private Date closedAt;

    private Date createdAt;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.REMOVE, CascadeType.DETACH, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    private Set<Label> labels;

    private int number;

    @Enumerated(EnumType.STRING)
    private IssueStatus status;

    @Column(columnDefinition = "text")
    private String title;

    @ManyToMany(mappedBy = "closingIssues")
    private Set<Commit> closedByCommits;

}