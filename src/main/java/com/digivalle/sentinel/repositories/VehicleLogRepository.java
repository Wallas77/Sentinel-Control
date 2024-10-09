/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;


import com.digivalle.sentinel.models.VehicleLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface VehicleLogRepository extends JpaRepository<VehicleLog, UUID>{
    
    VehicleLog getByPlates(String plates);
    List<VehicleLog> findByPlates(String plates);
    List<VehicleLog> findByPlatesIgnoreCaseContaining(String plates);
    List<VehicleLog> findByPlatesIgnoreCaseContainingAndDeleted(String plates, Boolean deleted);
    Page<VehicleLog> findByPlatesIgnoreCaseContaining(String plates, Pageable pageRequest);
    Page<VehicleLog> findByPlatesIgnoreCaseContainingAndDeleted(String plates, Boolean deleted, Pageable pageRequest);
}
