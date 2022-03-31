package backend.controller;

import backend.model.dto.CommitDTO;
import backend.service.CommitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/commit")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://software-analytics-g2.inf.usi.ch:4200",
        "http://software-analytics-g2.inf.usi.ch:3000",
        "http://software-analytics-g2.inf.usi.ch:80",
        "*"
})
public class CommitController {

    private final CommitService commitService;

    @GetMapping("/{id}")
    public ResponseEntity<CommitDTO> getSingleCommitById(@PathVariable Long id) {
        return ResponseEntity.ok(new CommitDTO(commitService.findById(id)));
    }
}
