/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;


import com.digivalle.sentinel.models.ActivityLog;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Waldir.Valle
 */
public interface ActivityLogRepository extends JpaRepository<ActivityLog, UUID>{
    
    ActivityLog getByName(String name);
    List<ActivityLog> findByName(String name);
    List<ActivityLog> findByNameIgnoreCaseContaining(String name);
    List<ActivityLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<ActivityLog> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<ActivityLog> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM ActivityLog a WHERE a.roleResponsability.id = :roleResponsabilityId AND a.employee.id = :employeeId AND a.activityDate > CURRENT_TIMESTAMP")
    void deleteByRoleResponsabilityIdAndEmployeeId(UUID roleResponsabilityId, UUID employeeId);
   
    @Transactional
    @Modifying
    @Query("DELETE FROM ActivityLog a WHERE a.serviceAttendance.id = :serviceAttendanceId AND a.employee.id = :employeeId AND a.activityDate > CURRENT_TIMESTAMP")
    void deleteByServiceAttendanceIdAndEmployeeId(UUID serviceAttendanceId, UUID employeeId);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM ActivityLog a WHERE a.serviceAssignment.id = :serviceAssignmentId AND a.employee.id = :employeeId AND a.activityDate > CURRENT_TIMESTAMP")
    void deleteByServiceAssignmentIdAndEmployeeId(UUID serviceAssignmentId, UUID employeeId);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM ActivityLog a WHERE a.serviceAssignment.id = :serviceAssignmentId AND a.activityDate > CURRENT_TIMESTAMP")
    void deleteByServiceAssignmentId(UUID serviceAssignmentId);
}
