/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.ServiceAttendance;
import java.util.Date;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Waldir.Valle
 */
public interface ServiceAttendanceRepository extends JpaRepository<ServiceAttendance, UUID>{
    
    @Query("SELECT sa FROM ServiceAttendance sa " +
       "WHERE sa.employee.id = :employeeId " +
       "AND sa.active = :active " +
       "AND sa.deleted = :deleted " +
       "AND ((sa.startDate BETWEEN :startDate AND :startDate2) " +
       "OR (sa.endDate BETWEEN :endDate AND :endDate2))")
    List<ServiceAttendance> findFiltered(
        @Param("employeeId") UUID employeeId,
        @Param("startDate") Date startDate,
        @Param("startDate2") Date startDate2,
        @Param("endDate") Date endDate,
        @Param("endDate2") Date endDate2,
        @Param("active") Boolean active,
        @Param("deleted") Boolean deleted
    );

    @Transactional
    @Modifying
    @Query("DELETE FROM ServiceAttendance sa WHERE sa.serviceAssignment.id = :serviceAssignmentId AND sa.startDate > CURRENT_TIMESTAMP")
    void deleteByServiceAssignmentId(UUID serviceAssignmentId);
   
    @Transactional
    @Modifying
    @Query("DELETE FROM ServiceAttendance sa WHERE sa.serviceAssignment.id = :serviceAssignmentId AND sa.employee.id = :employeeId AND sa.startDate > CURRENT_TIMESTAMP")
    void deleteByServiceAssignmentIdAndEmployeeId(UUID serviceAssignmentId, UUID employeeId);
   
}
