/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;


import com.digivalle.sentinel.models.ServiceAttendanceLog;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Waldir.Valle
 */
public interface ServiceAttendanceLogRepository extends JpaRepository<ServiceAttendanceLog, UUID>{
    
   @Transactional
    @Modifying
    @Query("DELETE FROM ServiceAttendanceLog sal WHERE sal.serviceAssignment.id = :serviceAssignmentId AND sal.startDate > CURRENT_TIMESTAMP")
    void deleteByServiceAssignmentId(UUID serviceAssignmentId);
   
    @Transactional
    @Modifying
    @Query("DELETE FROM ServiceAttendanceLog sal WHERE sal.serviceAssignment.id = :serviceAssignmentId AND sal.employee.id = :employeeId AND sal.startDate > CURRENT_TIMESTAMP")
    void deleteByServiceAssignmentIdAndEmployeeId(UUID serviceAssignmentId, UUID employeeId);
}
