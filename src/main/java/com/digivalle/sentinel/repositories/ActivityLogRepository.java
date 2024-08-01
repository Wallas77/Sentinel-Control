/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;


import com.digivalle.sentinel.models.ActivityLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID>{
    
    ActivityLog getByName(String name);
    List<ActivityLog> findByName(String name);
    List<ActivityLog> findByNameIgnoreCaseContaining(String name);
    List<ActivityLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<ActivityLog> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<ActivityLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
