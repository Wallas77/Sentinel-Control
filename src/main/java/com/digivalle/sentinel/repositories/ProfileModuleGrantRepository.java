/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.Module;
import com.digivalle.sentinel.models.Grant;
import com.digivalle.sentinel.models.Profile;
import com.digivalle.sentinel.models.ProfileModuleGrant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Waldir.Valle
 */
public interface ProfileModuleGrantRepository extends JpaRepository<ProfileModuleGrant, UUID>{
    
    List<ProfileModuleGrant> findByProfile(Profile profile);
    List<ProfileModuleGrant> findByModule(Module module);
    List<ProfileModuleGrant> findByGrant(Grant grant);
    List<ProfileModuleGrant> findByProfileAndModule_Application_Name(Profile profile, String applicationName);
    List<ProfileModuleGrant> findByProfileAndModule_NameAndGrant_Name(Profile profile, String moduleName, String grantName);
    Page<ProfileModuleGrant> findByProfile(Profile profile, Pageable pageRequest);
    Page<ProfileModuleGrant> findByModule(Module module, Pageable pageRequest);
    Page<ProfileModuleGrant> findByGrant(Grant grant, Pageable pageRequest);
    
    @Modifying
    @Query("delete from ProfileModuleGrant b where b.profile=:profile")
    void deleteByProfile(@Param("profile") Profile profile);
   
}
