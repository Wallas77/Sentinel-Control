/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;


import com.digivalle.sentinel.models.AccessControlObjectLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface AccessControlObjectLogRepository extends JpaRepository<AccessControlObjectLog, UUID>{
    
    AccessControlObjectLog getByName(String name);
    List<AccessControlObjectLog> findByName(String name);
    List<AccessControlObjectLog> findByNameIgnoreCaseContaining(String name);
    List<AccessControlObjectLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<AccessControlObjectLog> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<AccessControlObjectLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
