package com.turnquest.reportservice.service.impl;

import com.lowagie.text.DocumentException;
import com.turnquest.reportservice.models.Template;
import com.turnquest.reportservice.service.ExcelGenerationService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class ExcelGenerationServiceImpl implements ExcelGenerationService {
    private final TemplateEngine templateEngine;
    private final TemplateServiceImpl templateService;
    private final FirebaseService firebaseService;

    public ExcelGenerationServiceImpl(TemplateEngine templateEngine,
                                      TemplateServiceImpl templateService,
                                      FirebaseService firebaseService) {
        this.templateEngine = templateEngine;
        this.templateService = templateService;
        this.firebaseService = firebaseService;
    }

    /**
     * Generates an Excel file based on a template stored in Firebase.
     *
     * @param templateName The name of the template to use for generating the Excel file.
     * @param data A map containing the data to populate the template.
     * @return A byte array representing the generated Excel file.
     * @throws IOException If there is an error reading the template or writing the Excel file.
     * @throws DocumentException If there is an error processing the template or generating the Excel file.
     */
    public byte[] generateExcelFromTemplate(String templateName, Map<String, Object> data) throws IOException, DocumentException {
        Template template = templateService.getTemplateByName(templateName)
                .orElseThrow(() -> new IllegalArgumentException("Template not found"));

        InputStream templateStream = new ByteArrayInputStream(firebaseService.downloadFromFirebase(template.getFirebaseUrl()));
        return generateExcelFromTemplate(templateStream, data);
    }

    /**
     * Generates an Excel file based on a template provided as an InputStream.
     *
     * @param fileInputStream An InputStream containing the template to use for generating the Excel file.
     * @param data A map containing the data to populate the template.
     * @return A byte array representing the generated Excel file.
     * @throws IOException If there is an error reading the template or writing the Excel file.
     * @throws DocumentException If there is an error processing the template or generating the Excel file.
     */
    public byte[] generateExcelFromFile(InputStream fileInputStream, Map<String, Object> data) throws IOException, DocumentException {
        return generateExcelFromTemplate(fileInputStream, data);
    }

    /**
     * Generates an Excel file based on a template located at a specified URL.
     *
     * @param url The URL of the template to use for generating the Excel file.
     * @param data A map containing the data to populate the template.
     * @return A byte array representing the generated Excel file.
     * @throws IOException If there is an error reading the template from the URL or writing the Excel file.
     * @throws DocumentException If there is an error processing the template or generating the Excel file.
     */
    public byte[] generateExcelFromUrl(String url, Map<String, Object> data) throws IOException, DocumentException {
        InputStream inputStream = new URL(url).openStream();
        return generateExcelFromTemplate(inputStream, data);
    }

    /**
     * Generates an Excel file based on a template provided as an InputStream and a data map.
     *
     * @param templateStream An InputStream containing the template to use for generating the Excel file.
     * @param data A map containing the data to populate the template.
     * @return A byte array representing the generated Excel file.
     * @throws IOException If there is an error reading the template or writing the Excel file.
     */
    public byte[] generateExcelFromTemplate(InputStream templateStream, Map<String, Object> data) throws IOException {
        // Read template as string
        String htmlContent = new BufferedReader(new InputStreamReader(templateStream, StandardCharsets.UTF_8))
                .lines()
                .reduce("", (accumulator, actual) -> accumulator + actual);

        // Process Thymeleaf template with data
        Context context = new Context();
        context.setVariables(data);
        String processedHtml = templateEngine.process(htmlContent, context);

        // Create a new Excel document
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Report");

        // Write the processed HTML content into the Excel document
        int rowNum = 0;
        for (String line : processedHtml.split("\n")) {
            Row row = sheet.createRow(rowNum++);
            Cell cell = row.createCell(0);
            cell.setCellValue(line);
        }

        // Write the document to a byte array
        ByteArrayOutputStream excelStream = new ByteArrayOutputStream();
        workbook.write(excelStream);
        workbook.close();

        return excelStream.toByteArray();
    }
}