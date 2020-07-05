package com.vaadin.webapp.crm.backend.repository;

import com.vaadin.webapp.crm.backend.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
}
