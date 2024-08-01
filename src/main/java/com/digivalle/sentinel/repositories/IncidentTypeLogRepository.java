/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;


import com.digivalle.sentinel.models.IncidentTypeLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface IncidentTypeLogRepository extends JpaRepository<IncidentTypeLog, UUID>{
    
    IncidentTypeLog getByName(String name);
    List<IncidentTypeLog> findByName(String name);
    List<IncidentTypeLog> findByNameIgnoreCaseContaining(String name);
    List<IncidentTypeLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<IncidentTypeLog> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<IncidentTypeLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
