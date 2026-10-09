package com.findit.dto;

public class DashboardSummary {
    private final long activeReports;
    private final long resolvedReports;
    private final long suggestedMatches;
    private final long unreadNotifications;
    private final long campusActiveReports;
    public DashboardSummary(long activeReports, long resolvedReports, long suggestedMatches,
                            long unreadNotifications, long campusActiveReports) {
        this.activeReports = activeReports;
        this.resolvedReports = resolvedReports;
        this.suggestedMatches = suggestedMatches;
        this.unreadNotifications = unreadNotifications;
        this.campusActiveReports = campusActiveReports;
    }
    public long getActiveReports() { return activeReports; }
    public long getResolvedReports() { return resolvedReports; }
    public long getSuggestedMatches() { return suggestedMatches; }
    public long getUnreadNotifications() { return unreadNotifications; }
    public long getCampusActiveReports() { return campusActiveReports; }
}
