/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.ToolType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface ToolTypeRepository extends JpaRepository<ToolType, UUID>{
    
    ToolType getBySerial(Integer serial);
    ToolType getByName(String name);
    List<ToolType> findByName(String name);
    List<ToolType> findByNameIgnoreCaseContaining(String name);
    List<ToolType> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<ToolType> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<ToolType> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
