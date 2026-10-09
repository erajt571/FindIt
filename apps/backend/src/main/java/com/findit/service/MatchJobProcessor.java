package com.findit.service;

import com.findit.domain.MatchJob;
import com.findit.domain.MatchJobState;
import com.findit.repository.MatchJobRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class MatchJobProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchJobProcessor.class);

    private final MatchJobRepository jobRepository;
    private final MatchJobRunner matchJobRunner;
    private final MatchJobFailureService failureService;

    public MatchJobProcessor(MatchJobRepository jobRepository, MatchJobRunner matchJobRunner,
                              MatchJobFailureService failureService) {
        this.jobRepository = jobRepository;
        this.matchJobRunner = matchJobRunner;
        this.failureService = failureService;
    }

    @Scheduled(fixedDelayString = "${findit.match.poll-ms:5000}")
    public void processDueJobs() {
        List<UUID> jobIds = jobRepository.findTop10ByStateInAndNextAttemptAtLessThanEqualOrderByCreatedAtAsc(
                Arrays.asList(MatchJobState.PENDING, MatchJobState.RETRY), Instant.now())
                .stream().map(MatchJob::getId).collect(Collectors.toList());
        for (UUID jobId : jobIds) {
            try {
                matchJobRunner.processOne(jobId);
            } catch (RuntimeException exception) {
                LOGGER.warn("Match job {} failed; scheduling retry", jobId, exception);
                failureService.recordFailure(jobId);
            }
        }
    }
}
