/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.Role;
import com.digivalle.sentinel.models.RoleResponsability;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface RoleResponsabilityRepository extends JpaRepository<RoleResponsability, UUID>{
    
    RoleResponsability getBySerial(Integer serial);
    RoleResponsability getByName(String name);
    List<RoleResponsability> findByName(String name);
    List<RoleResponsability> findByRoleAndActiveAndDeleted(Role role, Boolean active, Boolean deleted);
    List<RoleResponsability> findByNameIgnoreCaseContaining(String name);
    List<RoleResponsability> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<RoleResponsability> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<RoleResponsability> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
