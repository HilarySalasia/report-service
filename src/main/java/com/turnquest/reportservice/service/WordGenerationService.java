package com.turnquest.reportservice.service;

import com.lowagie.text.DocumentException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public interface WordGenerationService {
    byte[] generateWordFromTemplate(String templateName, Map<String, Object> data) throws IOException, DocumentException;
    byte[] generateWordFromFile(InputStream fileInputStream, Map<String, Object> data) throws IOException, DocumentException;
    byte[] generateWordFromUrl(String url, Map<String, Object> data) throws IOException, DocumentException;
}