/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;


import com.digivalle.sentinel.models.RoleLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface RoleLogRepository extends JpaRepository<RoleLog, UUID>{
    
    RoleLog getByName(String name);
    List<RoleLog> findByName(String name);
    List<RoleLog> findByNameIgnoreCaseContaining(String name);
    List<RoleLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<RoleLog> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<RoleLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
