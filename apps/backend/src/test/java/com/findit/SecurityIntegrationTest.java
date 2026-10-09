package com.findit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.findit.domain.AccountStatus;
import com.findit.domain.AppUser;
import com.findit.domain.Role;
import com.findit.repository.UserRepository;
import com.findit.repository.MatchFeedbackRepository;
import com.findit.repository.MatchJobRepository;
import com.findit.repository.MatchRecordRepository;
import com.findit.repository.ModerationActionRepository;
import com.findit.repository.NotificationRepository;
import com.findit.repository.ReportRepository;
import com.findit.repository.ReportStatusHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatchFeedbackRepository matchFeedbackRepository;

    @Autowired
    private MatchJobRepository matchJobRepository;

    @Autowired
    private MatchRecordRepository matchRecordRepository;

    @Autowired
    private ModerationActionRepository moderationActionRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ReportStatusHistoryRepository reportStatusHistoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        matchFeedbackRepository.deleteAll();
        notificationRepository.deleteAll();
        moderationActionRepository.deleteAll();
        reportStatusHistoryRepository.deleteAll();
        matchJobRepository.deleteAll();
        matchRecordRepository.deleteAll();
        reportRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void registerAndLoginFlowWorks() throws Exception {
        Map<String, String> body = new HashMap<String, String>();
        body.put("displayName", "Ada Student");
        body.put("email", "ada@example.com");
        body.put("password", "password123");

        mockMvc.perform(post("/api/auth/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("ada@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));

        Map<String, String> login = new HashMap<String, String>();
        login.put("email", "ada@example.com");
        login.put("password", "password123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ada@example.com"))
                .andReturn();

        org.springframework.mock.web.MockHttpSession session = (org.springframework.mock.web.MockHttpSession) result.getRequest().getSession(false);
        mockMvc.perform(get("/api/users/me").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Ada Student"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void userMustBeAuthenticatedForProfileAndAdminEndpoints() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());

        AppUser user = new AppUser();
        user.setDisplayName("Regular User");
        user.setEmail("user@example.com");
        user.setPasswordHash(passwordEncoder.encode("password123"));
        user.setRole(Role.USER);
        user.setAccountStatus(AccountStatus.ACTIVE);
        userRepository.save(user);

        Map<String, String> login = new HashMap<String, String>();
        login.put("email", "user@example.com");
        login.put("password", "password123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        org.springframework.mock.web.MockHttpSession session = (org.springframework.mock.web.MockHttpSession) result.getRequest().getSession(false);
        mockMvc.perform(get("/api/admin/check").session(session))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminRoleCanAccessAdminEndpoint() throws Exception {
        AppUser admin = new AppUser();
        admin.setDisplayName("Admin User");
        admin.setEmail("admin@example.com");
        admin.setPasswordHash(passwordEncoder.encode("password123"));
        admin.setRole(Role.ADMIN);
        admin.setAccountStatus(AccountStatus.ACTIVE);
        userRepository.save(admin);

        Map<String, String> login = new HashMap<String, String>();
        login.put("email", "admin@example.com");
        login.put("password", "password123");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        org.springframework.mock.web.MockHttpSession session = (org.springframework.mock.web.MockHttpSession) result.getRequest().getSession(false);
        mockMvc.perform(get("/api/admin/check").session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ADMIN_OK"));
    }
}
