package com.turnquest.reportservice.service;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface ExcelGenerationService {
    byte[] generateExcelFromTemplate(String templateName, Map<String, Object> data) throws IOException, DocumentException;
    byte[] generateExcelFromFile(InputStream fileInputStream, Map<String, Object> data) throws IOException, DocumentException;
    byte[] generateExcelFromUrl(String url, Map<String, Object> data) throws IOException, DocumentException;
}