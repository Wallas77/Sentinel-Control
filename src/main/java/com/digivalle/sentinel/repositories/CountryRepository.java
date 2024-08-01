/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.Country;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface CountryRepository extends JpaRepository<Country, UUID>{
    
    Country getBySerial(Integer serial);
    Country getByName(String name);
    List<Country> findByName(String name);
    List<Country> findByNameIgnoreCaseContaining(String name);
    List<Country> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<Country> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<Country> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
