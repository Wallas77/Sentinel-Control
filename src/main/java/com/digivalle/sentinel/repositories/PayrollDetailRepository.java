/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.PayrollDetail;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface PayrollDetailRepository extends JpaRepository<PayrollDetail, UUID>{
    
    PayrollDetail getBySerial(Integer serial);
    PayrollDetail getByName(String name);
    List<PayrollDetail> findByName(String name);
    List<PayrollDetail> findByNameIgnoreCaseContaining(String name);
    List<PayrollDetail> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<PayrollDetail> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<PayrollDetail> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}