package com.turnquest.reportservice.service.impl;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import com.turnquest.reportservice.models.Template;
import com.turnquest.reportservice.repository.TemplateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl  {

    private final TemplateRepository templateRepository;

    /**
     * Saves a template file to the server and stores its metadata in the database.
     *
     * @param name The name of the template.
     * @param templateStream An InputStream containing the template file to be saved.
     * @return The saved Template object containing metadata about the template.
     * @throws IOException If there is an error during file saving or database operations.
     */
    public Template saveTemplate(String name, InputStream templateStream) throws IOException{
        // Define the directory where templates will be saved
        String directoryPath = "./templates";
        File directory = new File(directoryPath);

        // Create the directory if it doesn't exist
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                throw new IOException("Failed to create directory: " + directoryPath);
            }
        }

        // Define the file path
        String filePath = directoryPath + "/" + name + ".html";
        File file = new File(filePath);

        // Save the file to the server
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = templateStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }

        // Save template metadata in the database
        Template template = new Template();
        template.setName(name);
        template.setFirebaseUrl(filePath); // Update this to reflect the local file path
        return templateRepository.save(template);
    }

    /**
     * Retrieves a template by its name from the database.
     *
     * @param name The name of the template to retrieve.
     * @return An Optional containing the Template if found, or empty if not found.
     */
    public Optional<Template> getTemplateByName(String name) {
        return Optional.ofNullable(templateRepository.findByName(name));
    }
}