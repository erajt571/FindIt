package com.findit.service;

import com.findit.domain.MatchJob;
import com.findit.domain.MatchJobState;
import com.findit.repository.MatchJobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.UUID;

@Service
public class MatchJobFailureService {
    private static final int MAX_ATTEMPTS = 4;
    private final MatchJobRepository jobRepository;
    public MatchJobFailureService(MatchJobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }
    @Transactional
    public void recordFailure(UUID jobId) {
        MatchJob job = jobRepository.findByIdForUpdate(jobId).orElse(null);
        if (job == null || job.getState() == MatchJobState.COMPLETED) return;
        int attempts = job.getAttempts() + 1;
        job.setAttempts(attempts);
        job.setLastErrorCode("MATCH_PROCESSING_ERROR");
        if (attempts >= MAX_ATTEMPTS) {
            job.setState(MatchJobState.FAILED);
        } else {
            job.setState(MatchJobState.RETRY);
            job.setNextAttemptAt(Instant.now().plusSeconds(30L * (1L << attempts)));
        }
    }
}
