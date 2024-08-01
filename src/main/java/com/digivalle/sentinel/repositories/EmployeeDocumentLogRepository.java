/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;


import com.digivalle.sentinel.models.EmployeeDocumentLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface EmployeeDocumentLogRepository extends JpaRepository<EmployeeDocumentLog, UUID>{
    
    EmployeeDocumentLog getByName(String name);
    List<EmployeeDocumentLog> findByName(String name);
    List<EmployeeDocumentLog> findByNameIgnoreCaseContaining(String name);
    List<EmployeeDocumentLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<EmployeeDocumentLog> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<EmployeeDocumentLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
