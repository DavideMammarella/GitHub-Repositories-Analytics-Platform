package backend.controller;

import backend.model.*;
import backend.model.dto.*;
import backend.model.status.RequestStatus;
import backend.service.GitRepositoryService;
import backend.utils.exception.GitRepositoryNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/repository")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://software-analytics-g2.inf.usi.ch:4200",
        "http://software-analytics-g2.inf.usi.ch:3000",
        "http://software-analytics-g2.inf.usi.ch:80",
        "*"
})
public class GitRepositoryController {

    private final GitRepositoryService gitRepositoryService;

    private static final Logger LOGGER = LoggerFactory.getLogger(GitRepositoryController.class);

    @GetMapping("")
    public ResponseEntity<Page<GitRepositoryDTO>> getRepositoriesPage(Pageable pageable) {
        return ResponseEntity.ok(gitRepositoryService.findAllPage(pageable).map(GitRepositoryDTO::new));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GitRepositoryDTO> getSingleGitRepositoryById(@PathVariable Long id) {
        return ResponseEntity.ok(new GitRepositoryDTO(gitRepositoryService.findById(id)));
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<RequestStatus> getGitRepositoryStatus(@PathVariable Long id) {
        return ResponseEntity.ok(gitRepositoryService.findById(id).getStatus());
    }

    @GetMapping("/{id}/issues")
    public ResponseEntity<Set<IssueDTO>> getRepositoryIssues(@PathVariable Long id) {
        return ResponseEntity.ok(gitRepositoryService.findById(id).getIssues()
                .stream()
                .map(IssueDTO::new)
                .collect(Collectors.toSet()));
    }

    @GetMapping("/{id}/labels")
    public ResponseEntity<Set<Label>> getRepositoryLabels(@PathVariable Long id) {
        return ResponseEntity.ok(gitRepositoryService.findById(id).getLabels());
    }

    @GetMapping("/{id}/range")
    public ResponseEntity<GitRepositoryDataDTO> getDataInTimeRange(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date end) {
        return ResponseEntity.ok(gitRepositoryService.getRepositoryDataInTimeRange(id, start, end));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<GitRepositorySmallDTO> updateRepository(@PathVariable Long id) {

        GitRepository gitRepository = gitRepositoryService.findById(id);
        gitRepositoryService.requestUpdate(gitRepository);

        return ResponseEntity.ok(new GitRepositorySmallDTO(gitRepository));
    }

    @PostMapping("")
    public ResponseEntity<GitRepositorySmallDTO> submitRepositoryRequest(@RequestBody RepositoryUrlDTO repositoryUrlDTO) {
        String url = repositoryUrlDTO.getUrl();

        // check repository exists
        if (getGitHubUrlResponse(url) == HttpURLConnection.HTTP_NOT_FOUND) {
            throw new GitRepositoryNotFoundException(url);
        }

        // check if request for this repository has already been submitted
        Optional<GitRepository> gitRepositoryOptional = gitRepositoryService.findByUrl(url);
        if (gitRepositoryOptional.isPresent()) {
            GitRepository gitRepository = gitRepositoryOptional.get();
            // if request previously failed, try again
            if (gitRepository.getStatus() == RequestStatus.FAILED) {
                gitRepositoryService.reset(gitRepository);
            }
            return ResponseEntity.ok(new GitRepositorySmallDTO(gitRepository));
        }

        // create new repository and put it in request queue
        GitRepository repository = gitRepositoryService.createRepository(url);
        return new ResponseEntity<>(new GitRepositorySmallDTO(repository), HttpStatus.CREATED);
    }

    private int getGitHubUrlResponse(String repositoryUrl) {
        URL url = null;
        try {
           url = new URL("https://github.com/" + repositoryUrl);
        } catch (MalformedURLException e) {
            LOGGER.error(e.getMessage());
        }

        HttpURLConnection huc = null;
        try {
            huc = (HttpURLConnection) Objects.requireNonNull(url).openConnection();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }

        try {
            Objects.requireNonNull(huc).setRequestMethod("HEAD");
        } catch (ProtocolException e) {
            LOGGER.error(e.getMessage());
        }

        try {
            return Objects.requireNonNull(huc).getResponseCode();
        } catch (IOException e) {
            LOGGER.error(e.getMessage());
        }
        return 0;
    }
}
