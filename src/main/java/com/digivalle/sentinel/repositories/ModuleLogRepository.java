/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.ModuleLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface ModuleLogRepository extends JpaRepository<ModuleLog, UUID>{
    @Override
    ModuleLog getById(UUID id);
    List<ModuleLog> getByName(String name);
    List<ModuleLog> findByNameIgnoreCaseContaining(String name);
    List<ModuleLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<ModuleLog> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<ModuleLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
    List<ModuleLog> findByNameIgnoreCaseContainingAndApplication_NameIgnoreCaseContaining(String name, String nameApplication);
    List<ModuleLog> findByNameIgnoreCaseContainingAndDeletedAndApplication_NameIgnoreCaseContaining(String name, Boolean deleted, String nameApplication);
    Page<ModuleLog> findByNameIgnoreCaseContainingAndApplication_NameIgnoreCaseContaining(String name, String nameApplication, Pageable pageRequest);
    Page<ModuleLog> findByNameIgnoreCaseContainingAndDeletedAndApplication_NameIgnoreCaseContaining(String name, Boolean deleted, String nameApplication, Pageable pageRequest);
}
