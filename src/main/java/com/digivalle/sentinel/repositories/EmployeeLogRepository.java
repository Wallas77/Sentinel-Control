/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;


import com.digivalle.sentinel.models.EmployeeLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface EmployeeLogRepository extends JpaRepository<EmployeeLog, UUID>{
    
    EmployeeLog getByName(String name);
    List<EmployeeLog> findByName(String name);
    List<EmployeeLog> findByNameIgnoreCaseContaining(String name);
    List<EmployeeLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<EmployeeLog> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<EmployeeLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
