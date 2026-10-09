package com.findit.service;

import com.findit.api.ApiException;
import com.findit.domain.AppUser;
import com.findit.domain.ReportStatus;
import com.findit.dto.DashboardSummary;
import com.findit.repository.MatchRecordRepository;
import com.findit.repository.NotificationRepository;
import com.findit.repository.ReportRepository;
import com.findit.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {
    private final ReportRepository reportRepository;
    private final MatchRecordRepository matchRepository;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    public DashboardService(ReportRepository reportRepository, MatchRecordRepository matchRepository,
                            NotificationRepository notificationRepository, UserRepository userRepository) {
        this.reportRepository = reportRepository;
        this.matchRepository = matchRepository;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }
    @Transactional(readOnly = true)
    public DashboardSummary summary(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
        return new DashboardSummary(
                reportRepository.countByOwner_IdAndStatus(user.getId(), ReportStatus.ACTIVE),
                reportRepository.countByOwner_IdAndStatus(user.getId(), ReportStatus.RESOLVED),
                matchRepository.countSuggestedForUser(user.getId()),
                notificationRepository.countByUser_IdAndReadAtIsNull(user.getId()),
                reportRepository.countByStatus(ReportStatus.ACTIVE));
    }
}
