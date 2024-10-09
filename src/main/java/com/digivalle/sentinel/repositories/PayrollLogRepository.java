/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;


import com.digivalle.sentinel.models.PayrollLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface PayrollLogRepository extends JpaRepository<PayrollLog, UUID>{
    
    PayrollLog getByName(String name);
    List<PayrollLog> findByName(String name);
    List<PayrollLog> findByNameIgnoreCaseContaining(String name);
    List<PayrollLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<PayrollLog> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<PayrollLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
