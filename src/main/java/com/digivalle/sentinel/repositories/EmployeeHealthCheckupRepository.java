/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.Employee;
import com.digivalle.sentinel.models.EmployeeHealthCheckup;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface EmployeeHealthCheckupRepository extends JpaRepository<EmployeeHealthCheckup, UUID>{
    
    EmployeeHealthCheckup getBySerial(Integer serial);
    EmployeeHealthCheckup getByName(String name);
    List<EmployeeHealthCheckup> findByNameAndEmployeeAndDeleted(String name, Employee employee, Boolean deleted);
    List<EmployeeHealthCheckup> findByNameIgnoreCaseContaining(String name);
    List<EmployeeHealthCheckup> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<EmployeeHealthCheckup> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<EmployeeHealthCheckup> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
