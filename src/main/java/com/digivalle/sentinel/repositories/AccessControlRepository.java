/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.AccessControl;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface AccessControlRepository extends JpaRepository<AccessControl, UUID>{
    
    AccessControl getBySerial(Integer serial);
    AccessControl getByName(String name);
    List<AccessControl> findByName(String name);
    List<AccessControl> findByNameIgnoreCaseContaining(String name);
    List<AccessControl> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<AccessControl> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<AccessControl> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
