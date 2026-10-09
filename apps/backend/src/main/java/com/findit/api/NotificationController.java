package com.findit.api;

import com.findit.domain.Notification;
import com.findit.dto.NotificationResponse;
import com.findit.dto.PageResponse;
import com.findit.service.NotificationService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    private final NotificationService notificationService;
    public NotificationController(NotificationService notificationService) { this.notificationService = notificationService; }

    @GetMapping
    public PageResponse<NotificationResponse> list(Authentication authentication,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        validatePage(page, size);
        return new PageResponse<>(notificationService.list(authentication.getName(), page, size), NotificationResponse::new);
    }

    @GetMapping("/unread-count")
    public long unreadCount(Authentication authentication) {
        return notificationService.unreadCount(authentication.getName());
    }

    @PatchMapping("/{id}/read")
    @ResponseStatus(HttpStatus.OK)
    public NotificationResponse markRead(@PathVariable UUID id, Authentication authentication) {
        return new NotificationResponse(notificationService.markRead(id, authentication.getName()));
    }

    private void validatePage(int page, int size) {
        if (page < 0 || size < 1 || size > 50)
            throw new ApiException(HttpStatus.BAD_REQUEST, "Page must be non-negative and size must be between 1 and 50");
    }
}
