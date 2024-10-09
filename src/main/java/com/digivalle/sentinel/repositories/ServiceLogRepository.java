/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;


import com.digivalle.sentinel.models.ServiceLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface ServiceLogRepository extends JpaRepository<ServiceLog, UUID>{
    
    ServiceLog getByName(String name);
    List<ServiceLog> findByName(String name);
    List<ServiceLog> findByNameIgnoreCaseContaining(String name);
    List<ServiceLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<ServiceLog> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<ServiceLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
