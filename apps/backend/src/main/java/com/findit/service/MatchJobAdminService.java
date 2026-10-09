package com.findit.service;

import com.findit.api.ApiException;
import com.findit.domain.MatchJob;
import com.findit.domain.MatchJobState;
import com.findit.repository.MatchJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.UUID;

@Service
public class MatchJobAdminService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchJobAdminService.class);
    private final MatchJobRepository jobRepository;
    public MatchJobAdminService(MatchJobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }
    @Transactional
    public void retry(UUID id, String adminEmail) {
        MatchJob job = jobRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Match job not found"));
        if (job.getState() != MatchJobState.FAILED) {
            throw new ApiException(HttpStatus.CONFLICT, "Only failed match jobs can be retried");
        }
        job.setAttempts(0);
        job.setLastErrorCode(null);
        job.setNextAttemptAt(Instant.now());
        job.setState(MatchJobState.RETRY);
        LOGGER.info("Administrator {} queued retry for match job {}", adminEmail, id);
    }
}
