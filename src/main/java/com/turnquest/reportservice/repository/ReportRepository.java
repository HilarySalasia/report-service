package com.turnquest.reportservice.repository;

import com.turnquest.reportservice.models.GeneratedReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReportRepository extends JpaRepository<GeneratedReport, Long> {
    Optional<GeneratedReport> findByDataHashAndTemplateName(String dataHash, String templateName);
}