/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.Activity;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Waldir.Valle
 */
public interface ActivityRepository extends JpaRepository<Activity, UUID>{
    
    @Query("SELECT a FROM Activity a " +
       "WHERE a.employee.id = :employeeId " +
       "AND a.active = :active " +
       "AND a.deleted = :deleted " +
       "AND a.roleResponsability.id = :roleResponsabilityId " +     
       "AND a.activityDate BETWEEN :activityDate AND :activityDate2")
    List<Activity> findFiltered(
        @Param("employeeId") UUID employeeId,
        @Param("activityDate") Date activityDate,
        @Param("activityDate2") Date activityDate2,
        @Param("active") Boolean active,
        @Param("deleted") Boolean deleted,
        @Param("roleResponsabilityId") UUID roleResponsabilityId    
    );
    
    @Query("SELECT a FROM Activity a " +
       "WHERE a.serviceAssignment.id = :serviceAssignmentId " +
       "AND a.employee.id = :employeeId " +     
       "AND a.active = :active " +
       "AND a.deleted = :deleted " +
       "AND a.roleResponsability.id = :roleResponsabilityId " +     
       "AND a.activityDate BETWEEN :activityDate AND :activityDate2 "+
       "Order by a.activityDate desc")
    List<Activity> findFilteredServiceAssignment(
        @Param("serviceAssignmentId") UUID serviceAssignmentId,
        @Param("employeeId") UUID employeeId,
        @Param("activityDate") Date activityDate,
        @Param("activityDate2") Date activityDate2,
        @Param("active") Boolean active,
        @Param("deleted") Boolean deleted,
        @Param("roleResponsabilityId") UUID roleResponsabilityId    
    );
    
    Activity getByName(String name);
    List<Activity> findByName(String name);
    List<Activity> findByNameIgnoreCaseContaining(String name);
    List<Activity> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<Activity> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<Activity> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM Activity a WHERE a.roleResponsability.id = :roleResponsabilityId AND a.employee.id = :employeeId AND a.activityDate > CURRENT_TIMESTAMP")
    void deleteByRoleResponsabilityIdAndEmployeeId(UUID roleResponsabilityId, UUID employeeId);
   
    @Transactional
    @Modifying
    @Query("DELETE FROM Activity a WHERE a.serviceAttendance.id = :serviceAttendanceId AND a.employee.id = :employeeId AND a.activityDate > CURRENT_TIMESTAMP")
    void deleteByServiceAttendanceIdAndEmployeeId(UUID serviceAttendanceId, UUID employeeId);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM Activity a WHERE a.serviceAssignment.id = :serviceAssignmentId AND a.employee.id = :employeeId AND a.activityDate > CURRENT_TIMESTAMP")
    void deleteByServiceAssignmentIdAndEmployeeId(UUID serviceAssignmentId, UUID employeeId);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM Activity a WHERE a.serviceAssignment.id = :serviceAssignmentId AND a.activityDate > CURRENT_TIMESTAMP")
    void deleteByServiceAssignmentId(UUID serviceAssignmentId);
   
}
