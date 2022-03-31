package backend.service;

import backend.utils.exception.GitHubSetupException;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.AbuseLimitHandler;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class GitHubApiServiceImpl implements GitHubApiService {

    @Override
    public GitHub getGitHub() {
        try {
            return GitHubBuilder
                    .fromPropertyFile("./.github")
                    .withAbuseLimitHandler(AbuseLimitHandler.WAIT)
                    .build();
        } catch (IOException e) {
            throw new GitHubSetupException();
        }
    }
}
