package com.findit.repository;

import com.findit.domain.ReportStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ReportStatusHistoryRepository extends JpaRepository<ReportStatusHistory, UUID> {
}
