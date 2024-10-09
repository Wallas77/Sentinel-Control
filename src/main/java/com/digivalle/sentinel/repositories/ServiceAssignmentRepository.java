/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.Employee;
import com.digivalle.sentinel.models.Role;
import com.digivalle.sentinel.models.Service;
import com.digivalle.sentinel.models.ServiceAssignment;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface ServiceAssignmentRepository extends JpaRepository<ServiceAssignment, UUID>{
    
    ServiceAssignment getBySerial(Integer serial);
    List<ServiceAssignment> getByServiceAndRoleAndEmployeeAndActiveAndDeleted(Service service, Role role, Employee employee, Boolean active, Boolean deleted);
   
}
