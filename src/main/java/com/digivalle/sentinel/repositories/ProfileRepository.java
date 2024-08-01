/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.Profile;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface ProfileRepository extends JpaRepository<Profile, UUID>{
    @Override
    Profile getById(UUID id);
    Profile getBySerial(Integer serial);
    List<Profile> findByName(String name);
    List<Profile> findByNameIgnoreCaseContaining(String name);
    List<Profile> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<Profile> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<Profile> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
