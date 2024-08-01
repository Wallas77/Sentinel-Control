/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.IncidentType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface IncidentTypeRepository extends JpaRepository<IncidentType, UUID>{
    
    IncidentType getBySerial(Integer serial);
    IncidentType getByName(String name);
    List<IncidentType> findByName(String name);
    List<IncidentType> findByNameIgnoreCaseContaining(String name);
    List<IncidentType> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<IncidentType> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<IncidentType> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
