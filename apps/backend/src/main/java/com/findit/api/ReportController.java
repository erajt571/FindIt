package com.findit.api;

import com.findit.domain.Report;
import com.findit.domain.ReportType;
import com.findit.dto.PageResponse;
import com.findit.dto.ReportRequest;
import com.findit.dto.ReportResponse;
import com.findit.dto.StatusRequest;
import com.findit.service.ReportService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;
import java.util.stream.Collectors;
import java.time.Instant;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public PageResponse<ReportResponse> browse(
            @RequestParam(required = false) ReportType type,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Instant dateFrom,
            @RequestParam(required = false) Instant dateTo,
            @RequestParam(required = false, name = "q") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sort) {
        validatePage(page, size);
        if (!"createdAt".equals(sort) && !"itemName".equals(sort)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Unsupported sort field");
        }
        return new PageResponse<>(reportService.browse(type, category, location, dateFrom, dateTo, query, page, size, sort), ReportResponse::new);
    }

    @GetMapping("/mine")
    public PageResponse<ReportResponse> mine(Authentication authentication,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        validatePage(page, size);
        return new PageResponse<>(reportService.mine(authentication.getName(), page, size),
                report -> new ReportResponse(report, true));
    }

    @GetMapping("/{id}")
    public ReportResponse detail(@PathVariable UUID id, Authentication authentication) {
        Report report = reportService.get(id, authentication == null ? null : authentication.getName(),
                isAdmin(authentication));
        boolean privateDetails = isAdmin(authentication)
                || (authentication != null && report.getOwner().getEmail().equals(authentication.getName()));
        return new ReportResponse(report, privateDetails);
    }

    @PostMapping
    public ResponseEntity<ReportResponse> create(@Valid @RequestBody ReportRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ReportResponse(reportService.create(request, authentication.getName()), true));
    }

    @PutMapping("/{id}")
    public ReportResponse update(@PathVariable UUID id, @Valid @RequestBody ReportRequest request,
                                 Authentication authentication) {
        return new ReportResponse(reportService.update(id, request, authentication.getName(), isAdmin(authentication)), true);
    }

    @PatchMapping("/{id}/status")
    public ReportResponse status(@PathVariable UUID id, @Valid @RequestBody StatusRequest request,
                                 Authentication authentication) {
        return new ReportResponse(reportService.changeStatus(id, request.getStatus(), authentication.getName(),
                isAdmin(authentication)), true);
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }

    private void validatePage(int page, int size) {
        if (page < 0 || size < 1 || size > 50) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Page must be non-negative and size must be between 1 and 50");
        }
    }
}
