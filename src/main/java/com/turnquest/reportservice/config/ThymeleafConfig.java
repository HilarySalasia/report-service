package com.turnquest.reportservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.StringTemplateResolver;

@Configuration
public class ThymeleafConfig {

    /**
     * Configures and provides a TemplateEngine bean for processing Thymeleaf templates.
     *
     * @return A TemplateEngine instance configured with a StringTemplateResolver.
     */
    @Bean
    public TemplateEngine templateEngine() {
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(stringTemplateResolver());
        return templateEngine;
    }
    /**
     * Configures and provides a StringTemplateResolver bean for resolving Thymeleaf templates from strings.
     *
     * @return A StringTemplateResolver instance configured to process HTML templates without caching.
     */
    @Bean
    public StringTemplateResolver stringTemplateResolver() {
        StringTemplateResolver stringTemplateResolver = new StringTemplateResolver();
        stringTemplateResolver.setTemplateMode("HTML");
        stringTemplateResolver.setCacheable(false);
        return stringTemplateResolver;
    }
}