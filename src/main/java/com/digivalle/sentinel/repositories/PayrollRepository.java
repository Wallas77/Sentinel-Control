/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.Payroll;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface PayrollRepository extends JpaRepository<Payroll, UUID>{
    
    Payroll getBySerial(Integer serial);
    Payroll getByName(String name);
    List<Payroll> findByName(String name);
    List<Payroll> findByNameIgnoreCaseContaining(String name);
    List<Payroll> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<Payroll> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<Payroll> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
