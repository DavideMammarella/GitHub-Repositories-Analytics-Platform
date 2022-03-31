package backend.model.dto;

import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GitRepositoryDataDTO {

    private List<CommitSmallDTO> commits;

    private List<IssueSmallDTO> issues;
}
