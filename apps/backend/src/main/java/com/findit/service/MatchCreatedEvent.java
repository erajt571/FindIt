package com.findit.service;

import java.util.UUID;

public class MatchCreatedEvent {
    private final UUID matchId;
    public MatchCreatedEvent(UUID matchId) { this.matchId = matchId; }
    public UUID getMatchId() { return matchId; }
}
