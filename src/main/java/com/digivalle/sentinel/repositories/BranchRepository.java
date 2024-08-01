/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.Branch;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface BranchRepository extends JpaRepository<Branch, UUID>{
    
    Branch getBySerial(Integer serial);
    Branch getByName(String name);
    List<Branch> findByName(String name);
    List<Branch> findByNameIgnoreCaseContaining(String name);
    List<Branch> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<Branch> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<Branch> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
