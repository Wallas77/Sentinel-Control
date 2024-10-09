/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;


import com.digivalle.sentinel.models.ContactLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface ContactLogRepository extends JpaRepository<ContactLog, UUID>{
    
    ContactLog getByName(String name);
    List<ContactLog> findByName(String name);
    List<ContactLog> findByNameIgnoreCaseContaining(String name);
    List<ContactLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<ContactLog> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<ContactLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
