/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;


import com.digivalle.sentinel.models.PayrollDetailLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface PayrollDetailLogRepository extends JpaRepository<PayrollDetailLog, UUID>{
    
    PayrollDetailLog getByName(String name);
    List<PayrollDetailLog> findByName(String name);
    List<PayrollDetailLog> findByNameIgnoreCaseContaining(String name);
    List<PayrollDetailLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<PayrollDetailLog> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<PayrollDetailLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
