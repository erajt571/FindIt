package com.findit.service;

import com.findit.domain.MatchJob;
import com.findit.domain.MatchJobState;
import com.findit.repository.MatchJobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class MatchJobRunner {
    private final MatchJobRepository jobRepository;
    private final MatchingService matchingService;

    public MatchJobRunner(MatchJobRepository jobRepository, MatchingService matchingService) {
        this.jobRepository = jobRepository;
        this.matchingService = matchingService;
    }

    @Transactional
    public void processOne(UUID jobId) {
        MatchJob job = jobRepository.findByIdForUpdate(jobId).orElse(null);
        if (job == null || (job.getState() != MatchJobState.PENDING && job.getState() != MatchJobState.RETRY)
                || job.getNextAttemptAt().isAfter(Instant.now())) return;
        job.setState(MatchJobState.RUNNING);
        job.setAttempts(job.getAttempts() + 1);
        matchingService.generateFor(job.getReport());
        job.setState(MatchJobState.COMPLETED);
        job.setLastErrorCode(null);
    }
}
