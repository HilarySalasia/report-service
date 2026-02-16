package com.turnquest.reportservice.controller;

import com.turnquest.reportservice.service.ExcelGenerationService;
import com.turnquest.reportservice.service.WordGenerationService;
import com.turnquest.reportservice.service.impl.PDFGenerationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.Map;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final PDFGenerationServiceImpl pdfGenerationService;
    private final ExcelGenerationService excelGenerationService;
    private final WordGenerationService wordGenerationService;

    /**
     * Endpoint to generate a report based on a template stored in Firebase.
     *
     * @param templateName The name of the template to use for generating the report.
     * @param type The type of report to generate (pdf, excel, word).
     * @param data A map containing the data to populate the template.
     * @return A ResponseEntity containing the generated report as a byte array and appropriate headers for file download.
     * @throws Exception If there is an error during report generation.
     */
    @PostMapping("/generate/from-template")
    public ResponseEntity<byte[]> generateReportFromTemplate(@RequestParam("templateName") String templateName,
                                                             @RequestParam("type") String type,
                                                             @RequestBody Map<String, Object> data) throws Exception {
        System.out.println("templateName: " + templateName);
        try {
            byte[] fileContent;
            String fileName;
            MediaType mediaType;

            switch (type.toLowerCase()) {
                case "pdf":
                    fileContent = pdfGenerationService.generatePdfFromTemplate(templateName, data);
                    fileName = templateName + ".pdf";
                    mediaType = MediaType.APPLICATION_PDF;
                    break;
                case "excel":
                    fileContent = excelGenerationService.generateExcelFromTemplate(templateName, data);
                    fileName = templateName + ".xlsx";
                    mediaType = MediaType.APPLICATION_OCTET_STREAM;
                    break;
                case "word":
                    fileContent = wordGenerationService.generateWordFromTemplate(templateName, data);
                    fileName = templateName + ".docx";
                    mediaType = MediaType.APPLICATION_OCTET_STREAM;
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported file type: " + type);
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(mediaType)
                    .body(fileContent);

        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Endpoint to generate a report based on a template provided as a file upload.
     *
     * @param file The uploaded file containing the template to use for generating the report.
     * @param type The type of report to generate (pdf, excel, word).
     * @param data A map containing the data to populate the template.
     * @return A ResponseEntity containing the generated report as a byte array and appropriate headers for file download.
     */
    @PostMapping("/generate/from-file")
    public ResponseEntity<byte[]> generateReportFromFile(@RequestParam("file") MultipartFile file,
                                                         @RequestParam("type") String type,
                                                         @RequestBody Map<String, Object> data) {
        try {
            InputStream fileStream = file.getInputStream();
            byte[] fileContent;
            String fileName;
            MediaType mediaType;

            switch (type.toLowerCase()) {
                case "pdf":
                    fileContent = pdfGenerationService.generatePdfFromFile(fileStream, data);
                    fileName = "report.pdf";
                    mediaType = MediaType.APPLICATION_PDF;
                    break;
                case "excel":
                    fileContent = excelGenerationService.generateExcelFromFile(fileStream, data);
                    fileName = "report.xlsx";
                    mediaType = MediaType.APPLICATION_OCTET_STREAM;
                    break;
                case "word":
                    fileContent = wordGenerationService.generateWordFromFile(fileStream, data);
                    fileName = "report.docx";
                    mediaType = MediaType.APPLICATION_OCTET_STREAM;
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported file type: " + type);
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(mediaType)
                    .body(fileContent);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    /**
     * Endpoint to generate a report based on a template located at a URL.
     *
     * @param url The URL of the template to use for generating the report.
     * @param type The type of report to generate (pdf, excel, word).
     * @param data A map containing the data to populate the template.
     * @return A ResponseEntity containing the generated report as a byte array and appropriate headers for file download.
     */
    @PostMapping("/generate/from-url")
    public ResponseEntity<byte[]> generateReportFromUrl(@RequestParam("url") String url,
                                                        @RequestParam("type") String type,
                                                        @RequestBody Map<String, Object> data) {
        try {
            byte[] fileContent;
            String fileName;
            MediaType mediaType;

            switch (type.toLowerCase()) {
                case "pdf":
                    fileContent = pdfGenerationService.generatePdfFromUrl(url, data);
                    fileName = "report.pdf";
                    mediaType = MediaType.APPLICATION_PDF;
                    break;
                case "excel":
                    fileContent = excelGenerationService.generateExcelFromUrl(url, data);
                    fileName = "report.xlsx";
                    mediaType = MediaType.APPLICATION_OCTET_STREAM;
                    break;
                case "word":
                    fileContent = wordGenerationService.generateWordFromUrl(url, data);
                    fileName = "report.docx";
                    mediaType = MediaType.APPLICATION_OCTET_STREAM;
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported file type: " + type);
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                    .contentType(mediaType)
                    .body(fileContent);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}