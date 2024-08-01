/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;


import com.digivalle.sentinel.models.BranchLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface BranchLogRepository extends JpaRepository<BranchLog, UUID>{
    
    BranchLog getByName(String name);
    List<BranchLog> findByName(String name);
    List<BranchLog> findByNameIgnoreCaseContaining(String name);
    List<BranchLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<BranchLog> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<BranchLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
