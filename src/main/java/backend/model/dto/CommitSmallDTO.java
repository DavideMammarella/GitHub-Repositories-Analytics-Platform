package backend.model.dto;

import backend.model.Commit;
import lombok.*;

import java.util.Date;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CommitSmallDTO {

    private Long id;

    private Date createdAt;

    public CommitSmallDTO(Commit commit) {
        this.id = commit.getId();
        this.createdAt = commit.getCreatedAt();
    }
}
