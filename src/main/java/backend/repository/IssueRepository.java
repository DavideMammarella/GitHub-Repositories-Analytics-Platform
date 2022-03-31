package backend.repository;


import backend.model.Issue;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface IssueRepository extends CrudRepository<Issue, Long> {

    Issue findByRepositoryIdAndNumber(Long repositoryId, int issueNumber);

    @Query(value =  "SELECT i FROM Issue i " +
                    "WHERE i.repository.id = :repositoryId AND " +
                    "((i.createdAt BETWEEN :start AND :end) OR (i.closedAt BETWEEN :start AND :end) OR " +
                    "(i.createdAt <= :start AND (i.closedAt IS NULL OR i.closedAt >= :end))) " +
                    "ORDER BY i.createdAt"
    )
    List<Issue> findByRepositoryIdInTimeRange(@Param("repositoryId") Long repositoryId, @Param("start") Date start, @Param("end") Date end);

    void deleteAllByRepositoryId(Long repositoryId);
}
