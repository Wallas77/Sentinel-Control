/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;


import com.digivalle.sentinel.models.SupplierLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface SupplierLogRepository extends JpaRepository<SupplierLog, UUID>{
    
    SupplierLog getByName(String name);
    List<SupplierLog> findByName(String name);
    List<SupplierLog> findByNameIgnoreCaseContaining(String name);
    List<SupplierLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<SupplierLog> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<SupplierLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}