package com.turnquest.reportservice.controller;

import com.turnquest.reportservice.models.Template;
import com.turnquest.reportservice.service.impl.TemplateServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/templates")
@RequiredArgsConstructor
public class TemplateController {

    private final TemplateServiceImpl templateService;

    /**
     * Endpoint to upload a template file and save its metadata in the database.
     *
     * @param name The name of the template.
     * @param file The template file to be uploaded.
     * @return A ResponseEntity containing the saved Template object with metadata about the uploaded template.
     * @throws IOException If there is an error during file upload or database operations.
     */
    @PostMapping("/upload")
    public ResponseEntity<Template> uploadTemplate(@RequestParam("name") String name,
                                                   @RequestParam("file") MultipartFile file) throws IOException {
        InputStream templateStream = file.getInputStream();
        Template template = templateService.saveTemplate(name, templateStream);
        return ResponseEntity.ok(template);
    }

    /**
     * Endpoint to retrieve a template by its name.
     *
     * @param name The name of the template to retrieve.
     * @return A ResponseEntity containing the Template object if found, or a 404 Not Found response if the template does not exist.
     */
    @GetMapping("/{name}")
    public ResponseEntity<Template> getTemplateByName(@PathVariable String name) {
        return templateService.getTemplateByName(name)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}