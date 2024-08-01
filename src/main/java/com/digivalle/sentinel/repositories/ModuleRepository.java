/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.Module;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface ModuleRepository extends JpaRepository<Module, UUID>{
    @Override
    Module getById(UUID id);
    Module getBySerial(Integer serial);
    List<Module> getByName(String name);
    List<Module> findByNameIgnoreCaseContaining(String name);
    List<Module> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<Module> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<Module> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
    List<Module> findByNameIgnoreCaseContainingAndApplication_NameIgnoreCaseContaining(String name, String nameApplication);
    List<Module> findByNameIgnoreCaseContainingAndDeletedAndApplication_NameIgnoreCaseContaining(String name, Boolean deleted, String nameApplication);
    Page<Module> findByNameIgnoreCaseContainingAndApplication_NameIgnoreCaseContaining(String name, String nameApplication, Pageable pageRequest);
    Page<Module> findByNameIgnoreCaseContainingAndDeletedAndApplication_NameIgnoreCaseContaining(String name, Boolean deleted, String nameApplication, Pageable pageRequest);
}
