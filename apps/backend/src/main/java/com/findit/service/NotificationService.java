package com.findit.service;

import com.findit.api.ApiException;
import com.findit.domain.AppUser;
import com.findit.domain.Notification;
import com.findit.domain.MatchRecord;
import com.findit.repository.MatchRecordRepository;
import com.findit.repository.NotificationRepository;
import com.findit.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;
import java.time.Instant;
import java.util.UUID;

@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final MatchRecordRepository matchRepository;
    private final UserRepository userRepository;
    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository,
                               MatchRecordRepository matchRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.matchRepository = matchRepository;
    }

    @Transactional(readOnly = true)
    public Page<Notification> list(String email, int page, int size) {
        AppUser user = findUser(email);
        return notificationRepository.findAllByUser_IdOrderByCreatedAtDesc(user.getId(), PageRequest.of(page, size));
    }

    @Transactional
    public Notification markRead(UUID id, String email) {
        AppUser user = findUser(email);
        Notification notification = notificationRepository.findByIdAndUser_Id(id, user.getId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Notification not found"));
        if (notification.getReadAt() == null) notification.setReadAt(Instant.now());
        return notification;
    }

    @Transactional(readOnly = true)
    public long unreadCount(String email) {
        return notificationRepository.countByUser_IdAndReadAtIsNull(findUser(email).getId());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void notifyMatch(UUID matchId) {
        MatchRecord match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Match not found while creating notification"));
        createMatchNotification(match, match.getReport().getOwner(), match.getCandidateReport());
        if (!match.getReport().getOwner().getId().equals(match.getCandidateReport().getOwner().getId())) {
            createMatchNotification(match, match.getCandidateReport().getOwner(), match.getReport());
        }
    }

    private void createMatchNotification(MatchRecord match, AppUser recipient, com.findit.domain.Report counterpart) {
        String key = "match:" + match.getId() + ":user:" + recipient.getId();
        if (notificationRepository.findByDeduplicationKey(key).isPresent()) return;
        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setTitle("Potential item match");
        notification.setType("MATCH_SUGGESTION");
        notification.setMessage("A possible match was found for " + counterpart.getItemName()
                + ". Review the suggestion; it is not proof of ownership.");
        notification.setRelatedReport(counterpart);
        notification.setMatch(match);
        notification.setDeduplicationKey(key);
        notificationRepository.save(notification);
    }

    private AppUser findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "Authenticated user not found"));
    }
}
