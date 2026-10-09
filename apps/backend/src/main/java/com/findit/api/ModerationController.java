package com.findit.api;

import com.findit.domain.ModerationAction;
import com.findit.domain.Report;
import com.findit.dto.*;
import com.findit.service.ModerationService;
import com.findit.service.MatchJobAdminService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class ModerationController {
    private final ModerationService moderationService;
    private final MatchJobAdminService matchJobAdminService;

    public ModerationController(ModerationService moderationService, MatchJobAdminService matchJobAdminService) {
        this.moderationService = moderationService;
        this.matchJobAdminService = matchJobAdminService;
    }

    @GetMapping("/reports")
    public PageResponse<ReportResponse> queue(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        validatePage(page, size);
        return new PageResponse<>(moderationService.queue(page, size), ReportResponse::new);
    }

    @PostMapping("/reports/{id}/remove")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeReport(@PathVariable UUID id, @Valid @RequestBody ModerationRequest request,
                             Authentication authentication) {
        moderationService.removeReport(id, authentication.getName(), request.getReason());
    }

    @PostMapping("/users/{id}/suspend")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void suspendUser(@PathVariable UUID id, @Valid @RequestBody ModerationRequest request,
                            Authentication authentication) {
        moderationService.suspendUser(id, authentication.getName(), request.getReason());
    }

    @PostMapping("/match-jobs/{id}/retry")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void retryMatchJob(@PathVariable UUID id, Authentication authentication) {
        matchJobAdminService.retry(id, authentication.getName());
    }

    @GetMapping("/audit")
    public PageResponse<ModerationActionResponse> audit(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        validatePage(page, size);
        return new PageResponse<>(moderationService.actions(page, size), ModerationActionResponse::new);
    }

    private void validatePage(int page, int size) {
        if (page < 0 || size < 1 || size > 50)
            throw new ApiException(HttpStatus.BAD_REQUEST, "Page must be non-negative and size must be between 1 and 50");
    }
}
