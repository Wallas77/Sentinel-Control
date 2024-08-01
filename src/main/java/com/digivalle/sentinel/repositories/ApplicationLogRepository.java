/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;


import com.digivalle.sentinel.models.ApplicationLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface ApplicationLogRepository extends JpaRepository<ApplicationLog, UUID>{
    
    ApplicationLog getByName(String name);
    List<ApplicationLog> findByName(String name);
    List<ApplicationLog> findByNameIgnoreCaseContaining(String name);
    List<ApplicationLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<ApplicationLog> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<ApplicationLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
