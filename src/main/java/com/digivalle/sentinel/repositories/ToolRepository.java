/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.Tool;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface ToolRepository extends JpaRepository<Tool, UUID>{
    
    Tool getBySerial(Integer serial);
    Tool getByName(String name);
    List<Tool> findByIdNumber(String idNumber);
    List<Tool> findByName(String name);
    List<Tool> findByNameIgnoreCaseContaining(String name);
    List<Tool> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<Tool> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<Tool> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
