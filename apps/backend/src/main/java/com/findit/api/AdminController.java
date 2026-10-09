package com.findit.api;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @GetMapping("/check")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Object> adminCheck() {
        return Collections.<String, Object>singletonMap("status", "ADMIN_OK");
    }
}
