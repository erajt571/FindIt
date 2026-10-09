package com.findit.dto;

import com.findit.domain.Role;

import java.util.UUID;

public class UserProfileResponse {
    private UUID id;
    private String displayName;
    private String email;
    private Role role;

    public UserProfileResponse() {
    }

    public UserProfileResponse(UUID id, String displayName, String email, Role role) {
        this.id = id;
        this.displayName = displayName;
        this.email = email;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
