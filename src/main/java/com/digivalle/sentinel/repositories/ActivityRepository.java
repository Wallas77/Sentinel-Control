/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.Activity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface ActivityRepository extends JpaRepository<Activity, UUID>{
    
    Activity getBySerial(Integer serial);
    Activity getByName(String name);
    List<Activity> findByName(String name);
    List<Activity> findByNameIgnoreCaseContaining(String name);
    List<Activity> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<Activity> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<Activity> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
