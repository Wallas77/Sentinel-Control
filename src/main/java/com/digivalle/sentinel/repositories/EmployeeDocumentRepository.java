/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.Employee;
import com.digivalle.sentinel.models.EmployeeDocument;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument, UUID>{
    
    EmployeeDocument getBySerial(Integer serial);
    EmployeeDocument getByName(String name);
    //List<EmployeeDocument> findByName(String name);
    List<EmployeeDocument> findByNameAndEmployeeAndDeleted(String name, Employee employee, Boolean deleted);
    List<EmployeeDocument> findByNameIgnoreCaseContaining(String name);
    List<EmployeeDocument> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<EmployeeDocument> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<EmployeeDocument> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
