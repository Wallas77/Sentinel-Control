/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.FiscalInfo;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface FiscalInfoRepository extends JpaRepository<FiscalInfo, UUID>{
    
    FiscalInfo getBySerial(Integer serial);
    FiscalInfo getByName(String name);
    List<FiscalInfo> findByName(String name);
    List<FiscalInfo> findByNameIgnoreCaseContaining(String name);
    List<FiscalInfo> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<FiscalInfo> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<FiscalInfo> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
