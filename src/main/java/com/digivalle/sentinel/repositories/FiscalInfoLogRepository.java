/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;


import com.digivalle.sentinel.models.FiscalInfoLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface FiscalInfoLogRepository extends JpaRepository<FiscalInfoLog, UUID>{
    
    FiscalInfoLog getByName(String name);
    List<FiscalInfoLog> findByName(String name);
    List<FiscalInfoLog> findByNameIgnoreCaseContaining(String name);
    List<FiscalInfoLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<FiscalInfoLog> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<FiscalInfoLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
