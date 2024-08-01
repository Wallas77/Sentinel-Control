/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;


import com.digivalle.sentinel.models.EmployeeHealthCheckupLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface EmployeeHealthCheckupLogRepository extends JpaRepository<EmployeeHealthCheckupLog, UUID>{
    
    EmployeeHealthCheckupLog getByName(String name);
    List<EmployeeHealthCheckupLog> findByName(String name);
    List<EmployeeHealthCheckupLog> findByNameIgnoreCaseContaining(String name);
    List<EmployeeHealthCheckupLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<EmployeeHealthCheckupLog> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<EmployeeHealthCheckupLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
