package com.turnquest.reportservice.service.impl;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.lowagie.text.DocumentException;
import com.turnquest.reportservice.models.Template;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PDFGenerationServiceImpl {

    private final TemplateServiceImpl templateService;
    private final TemplateEngine templateEngine;
    private final FirebaseService firebaseService;

    /**
     * Generates a PDF file based on a template stored in Firebase.
     *
     * @param templateName The name of the template to use for generating the PDF file.
     * @param data A map containing the data to populate the template.
     * @return A byte array representing the generated PDF file.
     * @throws IOException If there is an error reading the template or writing the PDF file.
     * @throws DocumentException If there is an error processing the template or generating the PDF file.
     */
    public byte[] generatePdfFromTemplate(String templateName, Map<String, Object> data) throws IOException, DocumentException {
        Template template = templateService.getTemplateByName(templateName)
                .orElseThrow(() -> new IllegalArgumentException("Template not found"));
        InputStream templateStream = new FileInputStream(template.getFirebaseUrl());
        return generatePdfFromStream(templateStream, data);
    }

    /**
     * Generates a PDF file based on a template provided as an InputStream.
     *
     * @param fileInputStream An InputStream containing the template to use for generating the PDF file.
     * @param data A map containing the data to populate the template.
     * @return A byte array representing the generated PDF file.
     * @throws IOException If there is an error reading the template or writing the PDF file.
     * @throws DocumentException If there is an error processing the template or generating the PDF file.
     */
    public byte[] generatePdfFromFile(InputStream fileInputStream, Map<String, Object> data) throws IOException, DocumentException {
        return generatePdfFromStream(fileInputStream, data);
    }

    /**
     * Generates a PDF file based on a template located at a URL.
     *
     * @param url The URL of the template to use for generating the PDF file.
     * @param data A map containing the data to populate the template.
     * @return A byte array representing the generated PDF file.
     * @throws IOException If there is an error reading the template or writing the PDF file.
     * @throws DocumentException If there is an error processing the template or generating the PDF file.
     */
    public byte[] generatePdfFromUrl(String url, Map<String, Object> data) throws IOException, DocumentException {
        InputStream inputStream = new URL(url).openStream();
        return generatePdfFromStream(inputStream, data);
    }

    /**
     * Helper method to generate a PDF file from an InputStream template and data.
     *
     * @param templateStream An InputStream containing the template to use for generating the PDF file.
     * @param data A map containing the data to populate the template.
     * @return A byte array representing the generated PDF file.
     * @throws IOException If there is an error reading the template or writing the PDF file.
     * @throws DocumentException If there is an error processing the template or generating the PDF file.
     */
    private byte[] generatePdfFromStream(InputStream templateStream, Map<String, Object> data) throws IOException, DocumentException {
        // Read template as string
        String htmlContent = new BufferedReader(new InputStreamReader(templateStream, StandardCharsets.UTF_8))
                .lines()
                .reduce("", (accumulator, actual) -> accumulator + actual);

        // Process Thymeleaf template with data
        Context context = new Context();
        context.setVariables(data);
        String processedHtml = templateEngine.process(htmlContent, context);

        // Generate PDF

        ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
        ConverterProperties converterProperties = new ConverterProperties();
        converterProperties.setBaseUri("http://localhost:8088");
        /* Call convert method */
        HtmlConverter.convertToPdf(processedHtml, pdfStream, converterProperties);

        return pdfStream.toByteArray();
    }
}