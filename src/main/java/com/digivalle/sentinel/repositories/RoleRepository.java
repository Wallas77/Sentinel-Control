/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.Role;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface RoleRepository extends JpaRepository<Role, UUID>{
    
    Role getBySerial(Integer serial);
    Role getByName(String name);
    List<Role> findByName(String name);
    List<Role> findByNameIgnoreCaseContaining(String name);
    List<Role> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<Role> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<Role> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
