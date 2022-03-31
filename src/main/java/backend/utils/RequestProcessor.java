package backend.utils;

import backend.model.GitRepository;
import backend.service.GitRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Configurable
public class RequestProcessor implements Runnable {

    private GitRepositoryService gitRepositoryService;

    private static final AtomicBoolean continueProcessing = new AtomicBoolean(true);

    @Autowired
    public void setGitRepositoryService(GitRepositoryService gitRepositoryService) {
        this.gitRepositoryService = gitRepositoryService;
    }

    @Override
    public void run() {
        while (continueProcessing.get()) {
            Optional<GitRepository> nextRepository;
            while ((nextRepository = gitRepositoryService.getNextRepositoryInQueue()).isPresent()) {

                if (nextRepository.get().isUpdating()) {
                    gitRepositoryService.updateRepository(nextRepository.get());
                } else {
                    gitRepositoryService.mineRepository(nextRepository.get());
                }
            }
        }
    }

    public static void stop() {
        continueProcessing.set(false);
    }
}
