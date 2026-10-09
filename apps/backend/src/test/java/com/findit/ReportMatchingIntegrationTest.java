package com.findit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.findit.domain.*;
import com.findit.repository.*;
import com.findit.service.MatchFlowService;
import com.findit.service.MatchJobRunner;
import com.findit.service.MatchingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReportMatchingIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private ReportRepository reportRepository;
    @Autowired private MatchRecordRepository matchRepository;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private MatchJobRepository jobRepository;
    @Autowired private MatchFeedbackRepository feedbackRepository;
    @Autowired private ModerationActionRepository moderationRepository;
    @Autowired private ReportStatusHistoryRepository statusHistoryRepository;
    @Autowired private MatchingService matchingService;
    @Autowired private MatchJobRunner matchJobRunner;
    @Autowired private MatchFlowService matchFlowService;
    @Autowired private PasswordEncoder passwordEncoder;

    @BeforeEach
    void cleanDatabase() {
        feedbackRepository.deleteAll();
        notificationRepository.deleteAll();
        moderationRepository.deleteAll();
        statusHistoryRepository.deleteAll();
        jobRepository.deleteAll();
        matchRepository.deleteAll();
        reportRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void reportsCanBeCreatedSearchedAndOnlyEditedByTheirOwner() throws Exception {
        AppUser owner = user("owner@example.com", "Owner");
        userRepository.save(owner);
        String body = "{\"reportType\":\"LOST\",\"itemName\":\"Black headphones\","
                + "\"category\":\"Electronics\",\"description\":\"Wireless headphones with a blue case\","
                + "\"distinguishingDetails\":\"Private serial ending 4242\",\"locationName\":\"Library\"}";

        String response = mockMvc.perform(post("/api/reports")
                        .with(SecurityMockMvcRequestPostProcessors.user(owner.getEmail()).roles("USER"))
                        .with(csrf()).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ownerDisplayName").value("Owner"))
                .andExpect(jsonPath("$.distinguishingDetails").value("Private serial ending 4242"))
                .andReturn().getResponse().getContentAsString();
        JsonNode created = objectMapper.readTree(response);
        String id = created.get("id").asText();

        mockMvc.perform(get("/api/reports").param("type", "LOST").param("q", "headphones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].distinguishingDetails").value(org.hamcrest.Matchers.nullValue()))
                .andExpect(jsonPath("$.totalElements").value(1));

        mockMvc.perform(get("/api/reports/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.distinguishingDetails").value(org.hamcrest.Matchers.nullValue()));

        mockMvc.perform(get("/api/reports/" + id)
                        .with(SecurityMockMvcRequestPostProcessors.user(owner.getEmail()).roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.distinguishingDetails").value("Private serial ending 4242"));

        mockMvc.perform(put("/api/reports/" + id)
                        .with(SecurityMockMvcRequestPostProcessors.user("other@example.com").roles("USER"))
                        .with(csrf()).contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isNotFound());

        mockMvc.perform(patch("/api/reports/" + id + "/status")
                        .with(SecurityMockMvcRequestPostProcessors.user(owner.getEmail()).roles("USER"))
                        .with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"RESOLVED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESOLVED"));

        mockMvc.perform(get("/api/reports").param("type", "LOST"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void matchingIsDeduplicatedAndVerificationIsParticipantOnly() {
        AppUser owner = userRepository.save(user("lost@example.com", "Lost owner"));
        AppUser finder = userRepository.save(user("found@example.com", "Finder"));
        AppUser outsider = userRepository.save(user("outsider@example.com", "Outsider"));
        Report lost = report(owner, ReportType.LOST);
        Report found = report(finder, ReportType.FOUND);
        reportRepository.save(lost);
        reportRepository.save(found);

        matchingService.generateFor(lost);
        matchingService.generateFor(lost);
        MatchRecord match = matchRepository.findByReport_IdAndCandidateReport_Id(lost.getId(), found.getId()).get();
        org.junit.jupiter.api.Assertions.assertEquals(1, matchRepository.findAllForReport(lost.getId()).size());
        org.junit.jupiter.api.Assertions.assertEquals(2, notificationRepository.count());

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () ->
                matchFlowService.transition(match.getId(), MatchState.ACCEPTED_FOR_VERIFICATION, outsider.getEmail()));
        matchFlowService.transition(match.getId(), MatchState.ACCEPTED_FOR_VERIFICATION, owner.getEmail());
        org.junit.jupiter.api.Assertions.assertEquals(ReportStatus.PENDING_VERIFICATION,
                reportRepository.findById(lost.getId()).get().getStatus());
        org.junit.jupiter.api.Assertions.assertEquals(ReportStatus.PENDING_VERIFICATION,
                reportRepository.findById(found.getId()).get().getStatus());
        org.junit.jupiter.api.Assertions.assertEquals(MatchState.ACCEPTED_FOR_VERIFICATION,
                matchRepository.findById(match.getId()).get().getState());
        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class, () ->
                matchFlowService.transition(match.getId(), MatchState.CLOSED, owner.getEmail()));
        matchFlowService.transition(match.getId(), MatchState.CLOSED, finder.getEmail());
        org.junit.jupiter.api.Assertions.assertEquals(ReportStatus.RESOLVED,
                reportRepository.findById(lost.getId()).get().getStatus());
    }

    @Test
    void persistedMatchJobsRunInsideATransaction() {
        AppUser owner = userRepository.save(user("job-lost@example.com", "Lost owner"));
        AppUser finder = userRepository.save(user("job-found@example.com", "Finder"));
        Report lost = reportRepository.save(report(owner, ReportType.LOST));
        reportRepository.save(report(finder, ReportType.FOUND));
        MatchJob job = new MatchJob();
        job.setReport(lost);
        job = jobRepository.save(job);

        matchJobRunner.processOne(job.getId());

        MatchJob completed = jobRepository.findById(job.getId()).get();
        org.junit.jupiter.api.Assertions.assertEquals(MatchJobState.COMPLETED, completed.getState());
        org.junit.jupiter.api.Assertions.assertEquals(1, completed.getAttempts());
        org.junit.jupiter.api.Assertions.assertEquals(1, matchRepository.findAllForReport(lost.getId()).size());
    }

    @Test
    void twoReportOwnersCanRequestAndConfirmRecoveryThroughTheApi() throws Exception {
        AppUser owner = user("journey-lost@example.com", "Lost owner");
        owner.setPasswordHash(passwordEncoder.encode("password123"));
        AppUser finder = user("journey-found@example.com", "Finder");
        finder.setPasswordHash(passwordEncoder.encode("password123"));
        userRepository.save(owner);
        userRepository.save(finder);
        Report lost = report(owner, ReportType.LOST);
        Report found = report(finder, ReportType.FOUND);
        reportRepository.save(lost);
        reportRepository.save(found);
        matchingService.generateFor(lost);
        MatchRecord match = matchRepository.findByReport_IdAndCandidateReport_Id(lost.getId(), found.getId()).get();

        MockHttpSession ownerSession = login(owner.getEmail());
        MockHttpSession finderSession = login(finder.getEmail());
        mockMvc.perform(post("/api/matches/" + match.getId() + "/verification")
                        .session(ownerSession).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("ACCEPTED_FOR_VERIFICATION"));

        mockMvc.perform(get("/api/notifications").session(finderSession))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].type").value("VERIFICATION_REQUEST"));

        mockMvc.perform(patch("/api/matches/" + match.getId() + "/status")
                        .session(finderSession).with(csrf()).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"state\":\"CLOSED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.state").value("CLOSED"));
        org.junit.jupiter.api.Assertions.assertEquals(ReportStatus.RESOLVED,
                reportRepository.findById(lost.getId()).get().getStatus());
        org.junit.jupiter.api.Assertions.assertEquals(ReportStatus.RESOLVED,
                reportRepository.findById(found.getId()).get().getStatus());
    }

    private MockHttpSession login(String email) throws Exception {
        org.springframework.test.web.servlet.MvcResult result = mockMvc.perform(post("/api/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"password\":\"password123\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return (MockHttpSession) result.getRequest().getSession(false);
    }

    private AppUser user(String email, String name) {
        AppUser user = new AppUser();
        user.setEmail(email);
        user.setDisplayName(name);
        user.setPasswordHash("not-used-in-test");
        user.setRole(Role.USER);
        user.setAccountStatus(AccountStatus.ACTIVE);
        return user;
    }

    private Report report(AppUser owner, ReportType type) {
        Report report = new Report();
        report.setOwner(owner);
        report.setReportType(type);
        report.setItemName("Black wireless headphones");
        report.setCategory("Electronics");
        report.setDescription("Black wireless headphones in a blue carrying case");
        report.setLocationName("Central Library");
        report.setIncidentDate(Instant.now());
        report.setStatus(ReportStatus.ACTIVE);
        return report;
    }
}
