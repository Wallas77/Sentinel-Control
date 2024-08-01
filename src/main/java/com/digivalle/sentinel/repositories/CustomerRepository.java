/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.Customer;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface CustomerRepository extends JpaRepository<Customer, UUID>{
    
    Customer getBySerial(Integer serial);
    Customer getByName(String name);
    List<Customer> findByName(String name);
    List<Customer> findByNameIgnoreCaseContaining(String name);
    List<Customer> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<Customer> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<Customer> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
