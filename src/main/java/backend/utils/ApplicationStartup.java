package backend.utils;

import backend.model.GitRepository;
import backend.model.status.RequestStatus;
import backend.service.GitRepositoryService;
import com.google.common.collect.ImmutableSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private GitRepositoryService gitRepositoryService;

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {
        Set<RequestStatus> statuses = ImmutableSet.of(RequestStatus.DOWNLOADING, RequestStatus.MINING, RequestStatus.ANALYZING);
        Set<GitRepository> repositories = gitRepositoryService.findAllByStatusIn(statuses);
        repositories.forEach(repository -> gitRepositoryService.setRepositoryStatus(repository, RequestStatus.FAILED));

        RequestProcessor requestProcessor = new RequestProcessor();
        applicationContext.getAutowireCapableBeanFactory().autowireBean(requestProcessor);
        new Thread(requestProcessor).start();
    }
}