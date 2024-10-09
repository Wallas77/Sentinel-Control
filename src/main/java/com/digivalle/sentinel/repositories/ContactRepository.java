/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.Contact;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface ContactRepository extends JpaRepository<Contact, UUID>{
    
    Contact getBySerial(Integer serial);
    Contact getByName(String name);
    List<Contact> findByName(String name);
    List<Contact> findByNameIgnoreCaseContaining(String name);
    List<Contact> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<Contact> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<Contact> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
