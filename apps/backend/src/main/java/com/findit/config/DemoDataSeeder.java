package com.findit.config;

import com.findit.domain.*;
import com.findit.repository.MatchJobRepository;
import com.findit.repository.ReportRepository;
import com.findit.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Locale;

@Component
@Profile("demo")
@ConditionalOnProperty(name = "findit.demo.enabled", havingValue = "true")
public class DemoDataSeeder implements CommandLineRunner {
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final MatchJobRepository jobRepository;
    private final PasswordEncoder passwordEncoder;
    private final String environment;
    private final String adminEmail;
    private final String adminPassword;
    private final String userEmail;
    private final String userPassword;

    public DemoDataSeeder(UserRepository userRepository, ReportRepository reportRepository,
                          MatchJobRepository jobRepository, PasswordEncoder passwordEncoder,
                          @Value("${APP_ENV:local}") String environment,
                          @Value("${DEMO_ADMIN_EMAIL:admin@findit.local}") String adminEmail,
                          @Value("${DEMO_ADMIN_PASSWORD:}") String adminPassword,
                          @Value("${DEMO_USER_EMAIL:student@findit.local}") String userEmail,
                          @Value("${DEMO_USER_PASSWORD:}") String userPassword) {
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.jobRepository = jobRepository;
        this.passwordEncoder = passwordEncoder;
        this.environment = environment;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!"local".equalsIgnoreCase(environment) && !"demo".equalsIgnoreCase(environment)) {
            throw new IllegalStateException("The demo data profile must not run in production");
        }
        validatePassword(adminPassword, "DEMO_ADMIN_PASSWORD");
        validatePassword(userPassword, "DEMO_USER_PASSWORD");
        AppUser admin = seedUser(adminEmail, "FindIt Demo Admin", adminPassword, Role.ADMIN);
        AppUser student = seedUser(userEmail, "FindIt Demo Student", userPassword, Role.USER);
        if (reportRepository.count() == 0) seedReports(student, admin);
    }

    private AppUser seedUser(String email, String name, String password, Role role) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        AppUser existing = userRepository.findByEmail(normalizedEmail).orElse(null);
        if (existing != null) {
            if (existing.getRole() != role) {
                throw new IllegalStateException("Existing demo account has an unexpected role: " + normalizedEmail);
            }
            return existing;
        }
        AppUser user = new AppUser();
        user.setEmail(normalizedEmail);
        user.setDisplayName(name);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setRole(role);
        user.setAccountStatus(AccountStatus.ACTIVE);
        return userRepository.save(user);
    }

    private void seedReports(AppUser student, AppUser admin) {
        saveReport(student, ReportType.LOST, "Black wireless headphones", "Electronics",
                "Over-ear headphones with a blue carrying case.", "Central Library", Instant.now().minusSeconds(86400));
        saveReport(admin, ReportType.FOUND, "Black headphones in blue case", "Electronics",
                "Found near the library study area. Ask the owner to verify details privately.", "Central Library",
                Instant.now().minusSeconds(82800));
    }

    private void saveReport(AppUser owner, ReportType type, String itemName, String category,
                            String description, String location, Instant incidentDate) {
        Report report = new Report();
        report.setOwner(owner);
        report.setReportType(type);
        report.setItemName(itemName);
        report.setCategory(category);
        report.setDescription(description);
        report.setLocationName(location);
        report.setIncidentDate(incidentDate);
        report.setStatus(ReportStatus.ACTIVE);
        Report saved = reportRepository.save(report);
        MatchJob job = new MatchJob();
        job.setReport(saved);
        jobRepository.save(job);
    }

    private void validatePassword(String password, String variable) {
        if (password == null || password.length() < 12) {
            throw new IllegalStateException(variable + " must be set and contain at least 12 characters");
        }
    }
}
