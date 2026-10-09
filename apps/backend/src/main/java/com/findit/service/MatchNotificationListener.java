package com.findit.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.context.event.EventListener;

@Component
public class MatchNotificationListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchNotificationListener.class);
    private final NotificationService notificationService;
    public MatchNotificationListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @EventListener
    public void onMatchCreated(MatchCreatedEvent event) {
        try {
            notificationService.notifyMatch(event.getMatchId());
        } catch (RuntimeException exception) {
            LOGGER.warn("Unable to create notifications for match {} ({})",
                    event.getMatchId(), exception.getClass().getSimpleName());
        }
    }
}
