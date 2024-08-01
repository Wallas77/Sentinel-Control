/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;


import com.digivalle.sentinel.models.CountryLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface CountryLogRepository extends JpaRepository<CountryLog, UUID>{
    
    CountryLog getByName(String name);
    List<CountryLog> findByName(String name);
    List<CountryLog> findByNameIgnoreCaseContaining(String name);
    List<CountryLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<CountryLog> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<CountryLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
