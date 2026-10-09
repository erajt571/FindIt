package com.findit.service;

import com.findit.domain.MatchJob;
import com.findit.domain.MatchJobState;
import com.findit.repository.MatchJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class MatchJobProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchJobProcessor.class);

    private final MatchJobRepository jobRepository;
    private final MatchingService matchingService;
    private final MatchJobFailureService failureService;

    public MatchJobProcessor(MatchJobRepository jobRepository, MatchingService matchingService,
                              MatchJobFailureService failureService) {
        this.jobRepository = jobRepository;
        this.matchingService = matchingService;
        this.failureService = failureService;
    }

    @Scheduled(fixedDelayString = "${findit.match.poll-ms:5000}")
    public void processDueJobs() {
        List<UUID> jobIds = jobRepository.findTop10ByStateInAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(
                Arrays.asList(MatchJobState.PENDING, MatchJobState.RETRY), Instant.now())
                .stream().map(MatchJob::getId).collect(Collectors.toList());
        for (UUID jobId : jobIds) {
            try {
                processOne(jobId);
            } catch (RuntimeException exception) {
                LOGGER.warn("Match job {} failed with {}", jobId, exception.getClass().getSimpleName());
                failureService.recordFailure(jobId);
            }
        }
    }

    @Transactional
    public void processOne(UUID jobId) {
        MatchJob job = jobRepository.findByIdForUpdate(jobId).orElse(null);
        if (job == null || (job.getState() != MatchJobState.PENDING && job.getState() != MatchJobState.RETRY)
                || job.getNextAttemptAt().isAfter(Instant.now())) return;
        job.setState(MatchJobState.RUNNING);
        job.setAttempts(job.getAttempts() + 1);
        jobRepository.save(job);
        matchingService.generateFor(job.getReport());
        job.setState(MatchJobState.COMPLETED);
        job.setLastErrorCode(null);
    }

}
