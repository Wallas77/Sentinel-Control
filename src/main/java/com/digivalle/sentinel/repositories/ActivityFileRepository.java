/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.ActivityFile;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface ActivityFileRepository extends JpaRepository<ActivityFile, UUID>{
    
    ActivityFile getBySerial(Integer serial);
    ActivityFile getByName(String name);
    List<ActivityFile> findByName(String name);
    List<ActivityFile> findByNameIgnoreCaseContaining(String name);
    List<ActivityFile> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<ActivityFile> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<ActivityFile> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
