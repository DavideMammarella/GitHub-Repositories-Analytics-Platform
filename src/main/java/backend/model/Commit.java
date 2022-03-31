package backend.model;


import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@ToString
@Getter
@Setter
@AllArgsConstructor
@Builder
@Entity
@NoArgsConstructor
@Table(name = "commit")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id",
        scope = Commit.class)
public class Commit extends BaseEntity {

    private String hash;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.LAZY)
    @ToString.Exclude
    private GitRepository repository;

    private String author;

    private Date createdAt;

    @Column(columnDefinition = "text")
    private String message;

    @Column(columnDefinition = "text")
    private String body;

    @ElementCollection
    private Set<String> addedFiles;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> modifiedFiles;

    @ElementCollection
    private Set<String> deletedFiles;

    @ManyToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    private Set<Issue> closingIssues;

    private double averageCboDifference;

    private double averageLocDifference;

    private double averageWmcDifference;

    private double averageLcomDifference;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Commit commit = (Commit) o;

        return getId() != null && getId().equals(commit.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}