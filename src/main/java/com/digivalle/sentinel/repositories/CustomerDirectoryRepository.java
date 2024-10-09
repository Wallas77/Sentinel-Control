/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.Customer;
import com.digivalle.sentinel.models.CustomerDirectory;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface CustomerDirectoryRepository extends JpaRepository<CustomerDirectory, UUID>{
    
    CustomerDirectory getBySerial(Integer serial);
    CustomerDirectory getByName(String name);
    List<CustomerDirectory> findByName(String name);
    List<CustomerDirectory> findByNameIgnoreCaseContainingAndCustomerAndDeleted(String name, Customer customer, Boolean deleted);
    List<CustomerDirectory> findByNameIgnoreCaseContaining(String name);
    List<CustomerDirectory> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<CustomerDirectory> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<CustomerDirectory> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
}
