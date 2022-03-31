package backend.service;

import backend.model.GitRepository;
import backend.model.Label;
import backend.repository.LabelRepository;
import lombok.RequiredArgsConstructor;
import org.kohsuke.github.GHLabel;
import org.kohsuke.github.GHRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;

    private static final Logger LOGGER = LoggerFactory.getLogger(LabelServiceImpl.class);

    @Override
    public Optional<Label> findByRepositoryIdAndName(Long id, String name) {
        return labelRepository.findByRepositoryIdAndName(id, name);
    }

    @Override
    @Transactional
    public void deleteAllByRepositoryId(Long repositoryId) {
        labelRepository.deleteAllByRepositoryId(repositoryId);
    }

    @Override
    @Transactional
    public void mineLabels(GHRepository gitHubRepository, GitRepository repository) throws IOException {
        LOGGER.info("Mining labels");
        Set<Label> labels = mapLabels(gitHubRepository.listLabels().toList(), repository);
        labelRepository.saveAll(labels);
        repository.setLabelsCount(labels.size());
    }

    @Override
    @Transactional
    public void mineNewLabels(GHRepository gitHubRepository, GitRepository repository) throws IOException {
        LOGGER.info("Mining new labels");
        Set<GHLabel> newLabels = gitHubRepository
                .listLabels()
                .toList()
                .stream()
                .filter(label -> labelRepository.findByRepositoryIdAndName(repository.getId(), label.getName()).isEmpty())
                .collect(Collectors.toSet());

        labelRepository.saveAll(mapLabels(newLabels, repository));
        repository.setLabelsCount(repository.getLabelsCount() + newLabels.size());
    }

    private Set<Label> mapLabels(Collection<GHLabel> labels, GitRepository repository) {
        return labels
                .stream()
                .map(label -> Label
                        .builder()
                        .repository(repository)
                        .color(label.getColor())
                        .description(label.getDescription())
                        .name(label.getName())
                        .build())
                .collect(Collectors.toSet());
    }
}
