/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;


import com.digivalle.sentinel.models.ActivityFileLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface ActivityFileLogRepository extends JpaRepository<ActivityFileLog, UUID>{
    
    ActivityFileLog getByName(String name);
    List<ActivityFileLog> findByName(String name);
    List<ActivityFileLog> findByNameIgnoreCaseContaining(String name);
    List<ActivityFileLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<ActivityFileLog> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<ActivityFileLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
