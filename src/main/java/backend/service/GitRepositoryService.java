package backend.service;

import backend.model.GitRepository;
import backend.model.dto.GitRepositoryDataDTO;
import backend.model.status.RequestStatus;
import org.kohsuke.github.GHRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

public interface GitRepositoryService {

    GitRepository findById(Long id);

    Optional<GitRepository> findByUrl(String url);

    Set<GitRepository> findAllByStatusIn(Set<RequestStatus> statuses);

    Page<GitRepository> findAllPage(Pageable page);

    Optional<GitRepository> getNextRepositoryInQueue();

    GitRepositoryDataDTO getRepositoryDataInTimeRange(Long id, Date start, Date end);

    GitRepository createRepository(String url);

    void mineRepository(GitRepository gitRepository);

    void save(GitRepository gitRepository);

    void addRepositoryDetails(GitRepository gitRepository, GHRepository ghRepository);

    void updateRepository(GitRepository gitRepository);

    void setRepositoryStatus(GitRepository gitRepository, RequestStatus status);

    void requestUpdate(GitRepository gitRepository);

    void saveAndFlush(GitRepository gitRepository);

    void reset(GitRepository gitRepository);
}
