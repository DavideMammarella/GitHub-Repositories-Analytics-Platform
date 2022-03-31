package backend.repository;

import backend.model.Commit;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Repository
public interface CommitRepository extends CrudRepository<Commit, Long> {

    Set<Commit> findAllByRepositoryId(Long id);

    List<Commit> findByRepositoryIdAndCreatedAtBetweenOrderByCreatedAt(Long repositoryId, Date start, Date end);

    void deleteAllByRepositoryId(Long repositoryId);
}