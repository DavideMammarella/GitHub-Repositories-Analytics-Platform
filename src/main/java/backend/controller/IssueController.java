package backend.controller;

import backend.model.dto.IssueDTO;
import backend.service.IssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/issue")
@RequiredArgsConstructor
@CrossOrigin(origins = {
        "http://software-analytics-g2.inf.usi.ch:4200",
        "http://software-analytics-g2.inf.usi.ch:3000",
        "http://software-analytics-g2.inf.usi.ch:80",
        "*"
})
public class IssueController {

    private final IssueService issueService;

    @GetMapping("/{id}")
    public ResponseEntity<IssueDTO> getSingleIssueById(@PathVariable Long id) {
        return ResponseEntity.ok(new IssueDTO(issueService.findById(id)));
    }
}
