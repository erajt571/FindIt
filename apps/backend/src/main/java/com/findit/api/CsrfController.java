package com.findit.api;

import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/csrf")
public class CsrfController {
    @GetMapping
    public Map<String, String> token(CsrfToken csrfToken) {
        return Collections.singletonMap("token", csrfToken.getToken());
    }
}
