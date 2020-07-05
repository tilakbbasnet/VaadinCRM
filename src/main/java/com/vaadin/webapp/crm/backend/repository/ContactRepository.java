package com.vaadin.webapp.crm.backend.repository;

import com.vaadin.webapp.crm.backend.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    @Query("select c from Contact c "+"where lower(c.firstName) like lower(concat('%', :filterValue, '%')) "+
            "or lower(c.lastName) like lower(concat('%', :filterValue, '%'))")
    List<Contact>search(@Param("filterValue") String filterValue);
}
