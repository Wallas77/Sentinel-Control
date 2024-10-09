/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;


import com.digivalle.sentinel.models.ToolTypeLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface ToolTypeLogRepository extends JpaRepository<ToolTypeLog, UUID>{
    
    ToolTypeLog getByName(String name);
    List<ToolTypeLog> findByName(String name);
    List<ToolTypeLog> findByNameIgnoreCaseContaining(String name);
    List<ToolTypeLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<ToolTypeLog> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<ToolTypeLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
