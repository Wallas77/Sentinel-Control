/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.Employee;
import com.digivalle.sentinel.models.EmployeeTraining;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface EmployeeTrainingRepository extends JpaRepository<EmployeeTraining, UUID>{
    
    EmployeeTraining getBySerial(Integer serial);
    EmployeeTraining getByName(String name);
    List<EmployeeTraining> findByNameAndEmployeeAndDeleted(String name, Employee employee, Boolean deleted);
    List<EmployeeTraining> findByNameIgnoreCaseContaining(String name);
    List<EmployeeTraining> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<EmployeeTraining> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<EmployeeTraining> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}