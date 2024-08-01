/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.ProfileLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface ProfileLogRepository extends JpaRepository<ProfileLog, UUID>{
    @Override
    ProfileLog getById(UUID id);
    List<ProfileLog> getByName(String name);
    List<ProfileLog> findByNameIgnoreCaseContaining(String name);
    List<ProfileLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<ProfileLog> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<ProfileLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
