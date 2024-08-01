/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.UserLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface UserLogRepository extends JpaRepository<UserLog, UUID>{
    @Override
    UserLog getById(UUID id);
    List<UserLog> getByName(String name);
    List<UserLog> findByNameIgnoreCaseContaining(String name);
    List<UserLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<UserLog> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<UserLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
