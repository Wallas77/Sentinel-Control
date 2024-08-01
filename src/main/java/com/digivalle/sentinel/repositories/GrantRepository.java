/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.Grant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface GrantRepository extends JpaRepository<Grant, UUID>{
    
    Grant getBySerial(Integer serial);
    List<Grant> getByName(String name);
    List<Grant> findByNameIgnoreCaseContaining(String name);
    List<Grant> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<Grant> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<Grant> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
