package backend.repository;

import backend.model.GitRepository;
import backend.model.status.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface GitRepositoryRepository extends JpaRepository<GitRepository, Long> {

    Optional<GitRepository> findByUrl(String url);

    Set<GitRepository> findAllByStatusIn(Set<RequestStatus> statuses);

    Optional<GitRepository> findFirstByStatusEqualsOrderByLastUpdatedAsc(RequestStatus status);
}
