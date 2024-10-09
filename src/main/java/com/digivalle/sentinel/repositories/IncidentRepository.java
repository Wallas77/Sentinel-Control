/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.Employee;
import com.digivalle.sentinel.models.Incident;
import com.digivalle.sentinel.models.IncidentType;
import com.digivalle.sentinel.models.Service;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface IncidentRepository extends JpaRepository<Incident, UUID>{
    
    Incident getBySerial(Integer serial);
    Incident getByNotes(String notes);
    List<Incident> findByNotes(String notes);
    List<Incident> findByNotesIgnoreCaseContaining(String notes);
    List<Incident> findByNotesIgnoreCaseContainingAndDeleted(String notes, Boolean deleted);
    List<Incident> findByServiceAndIncidentTypeAndEmployeeAndDeleted(Service service, IncidentType incidentType, Employee employee, Boolean deleted);
    Page<Incident> findByNotesIgnoreCaseContaining(String notes, Pageable pageRequest);
    Page<Incident> findByNotesIgnoreCaseContainingAndDeleted(String notes, Boolean deleted, Pageable pageRequest);
}
