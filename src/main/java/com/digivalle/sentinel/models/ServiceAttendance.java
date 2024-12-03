/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.models;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author Waldir.Valle
 */
@Entity
@Table(name = "SERVICE_ATTENDANCES", indexes = @Index(columnList = "service_id, role_id, employee_id, employee_replacement_id"))
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class ServiceAttendance extends BaseEntity{
    
    @ManyToOne
    @JoinColumn(name = "SERVICE_ASSIGNMENT_ID", nullable = false)
    private ServiceAssignment serviceAssignment;
    @ManyToOne
    @JoinColumn(name = "SERVICE_ID", nullable = false)
    private Service service;
    @ManyToOne
    @JoinColumn(name = "ROLE_ID", nullable = false)
    private Role role;
    @ManyToOne
    @JoinColumn(name = "EMPLOYEE_ID", nullable = true)
    private Employee employee;
    @Column(name = "SALARY_PER_DAY_AMOUNT")
    private Double salaryParDayAmount;
    @Transient
    private Double salaryParDayAmount2;
    @Column(name = "HOURS_PER_DAY")
    private Integer hoursPerDay;
    @Column(name = "START_DATE", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date startDate;
    @Transient
    private Date startDate2;
    @Column(name = "END_DATE", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date endDate;
    @Transient
    private Date endDate2;
    @Column(name = "REAL_START_DATE", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date realStartDate;
    @Transient
    private Date realStartDate2;
    @Column(name = "REAL_END_DATE", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date realEndDate;
    @Transient
    private Date realEndDate2;
    @ManyToOne
    @JoinColumn(name = "EMPLOYEE_REPLACEMENT_ID", nullable = true)
    private Employee employeeReplacement;
    
}
