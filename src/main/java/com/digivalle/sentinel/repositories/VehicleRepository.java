/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.Vehicle;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface VehicleRepository extends JpaRepository<Vehicle, UUID>{
    
    Vehicle getBySerial(Integer serial);
    Vehicle getByPlates(String plates);
    List<Vehicle> findByPlates(String plates);
    List<Vehicle> findByPlatesIgnoreCaseContaining(String plates);
    List<Vehicle> findByPlatesIgnoreCaseContainingAndDeleted(String plates, Boolean deleted);
    Page<Vehicle> findByPlatesIgnoreCaseContaining(String plates, Pageable pageRequest);
    Page<Vehicle> findByPlatesIgnoreCaseContainingAndDeleted(String plates, Boolean deleted, Pageable pageRequest);
}
