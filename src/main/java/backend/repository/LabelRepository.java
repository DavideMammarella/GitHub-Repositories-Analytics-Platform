package backend.repository;

import backend.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {

    Optional<Label> findByRepositoryIdAndName(Long repositoryId, String name);

    void deleteAllByRepositoryId(Long repositoryId);
}
