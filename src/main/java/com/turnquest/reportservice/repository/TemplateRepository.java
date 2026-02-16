package com.turnquest.reportservice.repository;

import com.turnquest.reportservice.models.Template;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemplateRepository extends JpaRepository<Template, Long> {
    Template findByName(String name);
}
