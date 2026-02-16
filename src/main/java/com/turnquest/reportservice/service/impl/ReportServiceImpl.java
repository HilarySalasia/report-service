package com.turnquest.reportservice.service.impl;

import com.turnquest.reportservice.models.GeneratedReport;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ReportServiceImpl {

    private final FirebaseService firebaseService;

    public ReportServiceImpl(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    /**
     * Saves the generated report locally with a unique name based on the template and data hash.
     *
     * @param pdfBytes The byte array representing the generated PDF report.
     * @param templateName The name of the template used to generate the report.
     * @param dataHash A hash representing the input data used for generating the report, ensuring uniqueness.
     * @return The file path where the report is saved locally.
     * @throws IOException If there is an error during file writing.
     */
    private String saveReportLocally(byte[] pdfBytes, String templateName, String dataHash) throws IOException {
        String fileName = templateName + "_" + dataHash + ".pdf";
        Path filePath = Paths.get("reports", fileName);
        Files.write(filePath, pdfBytes);
        return filePath.toString();
    }

//    private String uploadReportToFirebase(String localFilePath) {
//        // Firebase upload logic, returning the file URL
//        String firebaseUrl = ...;
//        return firebaseUrl;
//    }

    /**
     * Retrieves the generated report either from local storage or Firebase based on the provided report information.
     *
     * @param report The GeneratedReport object containing information about where the report is stored.
     * @return A byte array representing the content of the retrieved report.
     * @throws IOException If there is an error during file reading or if the report is not found.
     */
    private byte[] retrieveReport(GeneratedReport report) throws IOException {
        if (report.getLocalFilePath() != null) {
            return Files.readAllBytes(Paths.get(report.getLocalFilePath()));
        } else if (report.getFirebaseUrl() != null) {
            return firebaseService.downloadFromFirebase(report.getFirebaseUrl());
        }
        throw new FileNotFoundException("Report not found");
    }
}
