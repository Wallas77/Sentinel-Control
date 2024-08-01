/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.Supplier;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface SupplierRepository extends JpaRepository<Supplier, UUID>{
    
    Supplier getBySerial(Integer serial);
    Supplier getByName(String name);
    List<Supplier> findByName(String name);
    List<Supplier> findByNameIgnoreCaseContaining(String name);
    List<Supplier> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<Supplier> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<Supplier> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
