package com.turnquest.reportservice.service.impl;

import com.lowagie.text.DocumentException;
import com.turnquest.reportservice.models.Template;
import com.turnquest.reportservice.service.WordGenerationService;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class WordGenerationServiceImpl implements WordGenerationService {
    private final TemplateEngine templateEngine;
    private final TemplateServiceImpl templateService;
    private final FirebaseService firebaseService;

    public WordGenerationServiceImpl(TemplateEngine templateEngine,
                                      TemplateServiceImpl templateService,
                                      FirebaseService firebaseService) {
        this.templateEngine = templateEngine;
        this.templateService = templateService;
        this.firebaseService = firebaseService;
    }

    /**
     * Generates a Word document based on a template stored in Firebase.
     *
     * @param templateName The name of the template to use for generating the Word document.
     * @param data A map containing the data to populate the template.
     * @return A byte array representing the generated Word document.
     * @throws IOException If there is an error reading the template or writing the Word document.
     * @throws DocumentException If there is an error processing the template or generating the Word document.
     */
    public byte[] generateWordFromTemplate(String templateName, Map<String, Object> data) throws IOException, DocumentException {
        Template template = templateService.getTemplateByName(templateName)
                .orElseThrow(() -> new IllegalArgumentException("Template not found"));

        InputStream templateStream = new ByteArrayInputStream(firebaseService.downloadFromFirebase(template.getFirebaseUrl()));
        return generateWordFromTemplate(templateStream, data);
    }

    /**
     * Generates a Word document based on a template provided as an InputStream.
     *
     * @param fileInputStream An InputStream containing the template to use for generating the Word document.
     * @param data A map containing the data to populate the template.
     * @return A byte array representing the generated Word document.
     * @throws IOException If there is an error reading the template or writing the Word document.
     * @throws DocumentException If there is an error processing the template or generating the Word document.
     */
    public byte[] generateWordFromFile(InputStream fileInputStream, Map<String, Object> data) throws IOException, DocumentException {
        return generateWordFromTemplate(fileInputStream, data);
    }

    /**
     * Generates a Word document based on a template located at a URL.
     *
     * @param url The URL of the template to use for generating the Word document.
     * @param data A map containing the data to populate the template.
     * @return A byte array representing the generated Word document.
     * @throws IOException If there is an error reading the template or writing the Word document.
     * @throws DocumentException If there is an error processing the template or generating the Word document.
     */
    public byte[] generateWordFromUrl(String url, Map<String, Object> data) throws IOException, DocumentException {
        InputStream inputStream = new URL(url).openStream();
        return generateWordFromTemplate(inputStream, data);
    }

    /**
     * Helper method to generate a Word document from an InputStream template and data.
     *
     * @param templateStream An InputStream containing the template to use for generating the Word document.
     * @param data A map containing the data to populate the template.
     * @return A byte array representing the generated Word document.
     * @throws IOException If there is an error reading the template or writing the Word document.
     * @throws DocumentException If there is an error processing the template or generating the Word document.
     */
    public byte[] generateWordFromTemplate(InputStream templateStream, Map<String, Object> data) throws IOException {
        // Read template as string
        String htmlContent = new BufferedReader(new InputStreamReader(templateStream, StandardCharsets.UTF_8))
                .lines()
                .reduce("", (accumulator, actual) -> accumulator + actual);

        // Process Thymeleaf template with data
        Context context = new Context();
        context.setVariables(data);
        String processedHtml = templateEngine.process(htmlContent, context);

        // Create a new Word document
        XWPFDocument document = new XWPFDocument();
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText(processedHtml);

        // Write the document to a byte array
        ByteArrayOutputStream wordStream = new ByteArrayOutputStream();
        document.write(wordStream);
        document.close();

        return wordStream.toByteArray();
    }
}
