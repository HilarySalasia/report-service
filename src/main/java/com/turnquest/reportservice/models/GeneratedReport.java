package com.turnquest.reportservice.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "generated_reports")
public class GeneratedReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_name", nullable = false)
    private String reportName;

    @Column(name = "template_name", nullable = false)
    private String templateName;

    @Column(name = "data_hash", nullable = false)
    private String dataHash;

    @Column(name = "local_file_path")
    private String localFilePath;

    @Column(name = "firebase_url")
    private String firebaseUrl;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;
}
