/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.GrantLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface GrantLogRepository extends JpaRepository<GrantLog, UUID>{
    @SuppressWarnings("null")
    @Override
    GrantLog getById(UUID id);
    List<GrantLog> getByName(String name);
    List<GrantLog> findByNameIgnoreCaseContaining(String name);
    List<GrantLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<GrantLog> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<GrantLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
