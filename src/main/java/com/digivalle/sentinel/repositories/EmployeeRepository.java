/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.Employee;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface EmployeeRepository extends JpaRepository<Employee, UUID>{
    
    Employee getBySerial(Integer serial);
    Employee getByName(String name);
    List<Employee> findByName(String name);
    List<Employee> findByCode(String code);
    List<Employee> findByNameIgnoreCaseContaining(String name);
    List<Employee> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<Employee> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<Employee> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
