package com.findit.api;

import com.findit.dto.DashboardSummary;
import com.findit.service.DashboardService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;
    public DashboardController(DashboardService dashboardService) { this.dashboardService = dashboardService; }
    @GetMapping("/summary")
    public DashboardSummary summary(Authentication authentication) {
        return dashboardService.summary(authentication.getName());
    }
}
