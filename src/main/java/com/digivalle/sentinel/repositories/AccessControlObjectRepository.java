/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.AccessControlObject;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface AccessControlObjectRepository extends JpaRepository<AccessControlObject, UUID>{
    
    AccessControlObject getBySerial(Integer serial);
    AccessControlObject getByName(String name);
    List<AccessControlObject> findByName(String name);
    List<AccessControlObject> findByNameIgnoreCaseContaining(String name);
    List<AccessControlObject> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<AccessControlObject> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<AccessControlObject> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
