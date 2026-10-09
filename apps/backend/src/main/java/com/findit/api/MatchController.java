package com.findit.api;

import com.findit.domain.MatchRecord;
import com.findit.dto.MatchFeedbackRequest;
import com.findit.dto.MatchResponse;
import com.findit.dto.MatchStateRequest;
import com.findit.service.MatchFlowService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class MatchController {
    private final MatchFlowService matchFlowService;

    public MatchController(MatchFlowService matchFlowService) {
        this.matchFlowService = matchFlowService;
    }

    @GetMapping("/reports/{reportId}/matches")
    public List<MatchResponse> listForReport(@PathVariable UUID reportId, Authentication authentication) {
        return matchFlowService.listForReport(reportId, authentication.getName())
                .stream().map(MatchResponse::new).collect(Collectors.toList());
    }

    @GetMapping("/matches/{id}")
    public MatchResponse detail(@PathVariable UUID id, Authentication authentication) {
        return new MatchResponse(matchFlowService.get(id, authentication.getName()));
    }

    @PostMapping("/matches/{id}/feedback")
    public void feedback(@PathVariable UUID id, @Valid @RequestBody MatchFeedbackRequest request,
                         Authentication authentication) {
        matchFlowService.feedback(id, request.getFeedback(), authentication.getName());
    }

    @PostMapping("/matches/{id}/verification")
    public MatchResponse verify(@PathVariable UUID id, Authentication authentication) {
        return new MatchResponse(matchFlowService.transition(id,
                com.findit.domain.MatchState.ACCEPTED_FOR_VERIFICATION, authentication.getName()));
    }

    @PatchMapping("/matches/{id}/status")
    public MatchResponse status(@PathVariable UUID id, @Valid @RequestBody MatchStateRequest request,
                                Authentication authentication) {
        return new MatchResponse(matchFlowService.transition(id, request.getState(), authentication.getName()));
    }
}
