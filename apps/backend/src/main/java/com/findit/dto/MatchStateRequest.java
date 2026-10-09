package com.findit.dto;

import com.findit.domain.MatchState;
import javax.validation.constraints.NotNull;

public class MatchStateRequest {
    @NotNull
    private MatchState state;
    public MatchState getState() { return state; }
    public void setState(MatchState state) { this.state = state; }
}
