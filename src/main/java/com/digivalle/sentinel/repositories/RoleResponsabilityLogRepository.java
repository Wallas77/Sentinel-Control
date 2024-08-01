/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;


import com.digivalle.sentinel.models.RoleResponsabilityLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface RoleResponsabilityLogRepository extends JpaRepository<RoleResponsabilityLog, UUID>{
    
    RoleResponsabilityLog getByName(String name);
    List<RoleResponsabilityLog> findByName(String name);
    List<RoleResponsabilityLog> findByNameIgnoreCaseContaining(String name);
    List<RoleResponsabilityLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<RoleResponsabilityLog> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<RoleResponsabilityLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
