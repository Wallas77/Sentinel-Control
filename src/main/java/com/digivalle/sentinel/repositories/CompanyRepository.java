/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.Company;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface CompanyRepository extends JpaRepository<Company, UUID>{
    
    Company getBySerial(Integer serial);
    Company getByName(String name);
    List<Company> findByName(String name);
    List<Company> findByNameIgnoreCaseContaining(String name);
    List<Company> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<Company> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<Company> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
