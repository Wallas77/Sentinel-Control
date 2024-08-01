/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;


import com.digivalle.sentinel.models.CustomerLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface CustomerLogRepository extends JpaRepository<CustomerLog, UUID>{
    
    CustomerLog getByName(String name);
    List<CustomerLog> findByName(String name);
    List<CustomerLog> findByNameIgnoreCaseContaining(String name);
    List<CustomerLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<CustomerLog> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<CustomerLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
