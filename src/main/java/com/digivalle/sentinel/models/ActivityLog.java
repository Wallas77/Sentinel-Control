/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.models;

import com.digivalle.sentinel.models.enums.ActivityStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author Waldir.Valle
 */
@Entity
@Table(name = "ACTIVITIES_LOG", indexes = @Index(columnList = "name"))
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class ActivityLog extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "SERVICE_ASSIGNMENT_ID", nullable = false)
    private ServiceAssignment serviceAssignment;
    @ManyToOne
    @JoinColumn(name = "SERVICE_ATTENDANCE_ID", nullable = false)
    private ServiceAttendance serviceAttendance;
    @ManyToOne
    @JoinColumn(name = "SERVICE_ID", nullable = false)
    private Service service;
    @Column(name = "NAME" , columnDefinition = "VARCHAR(100)")
    private String name;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "ACTIVITY_DATE", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date activityDate;
    @Transient
    private Date activityDate2;
    @Column(name = "EXACT_TIME")
    private Boolean exactTime;
    @Column(name = "START_DATE", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date startDate;
    @Transient
    private Date startDate2;
    @Column(name = "END_DATE", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date endDate;
    @Transient
    private Date endDate2;
    @Column(name = "CANCELED_DATE", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date canceledDate;
    @Transient
    private Date canceledDate2;
    @Column(name = "REQUIRED_FILES")
    private Integer requiredFiles;
    @ManyToOne
    @JoinColumn(name = "ROLE_RESPONSABILITY_ID", nullable = false)
    private RoleResponsability roleResponsability;
    @ManyToOne
    @JoinColumn(name = "EMPLOYEE_ID", nullable = false)
    private Employee employee;
    @Column(name = "EMPLOYEE_BONUS")
    private Double employeeBonus;
    @Enumerated(EnumType.STRING)
    @Column(name = "ACTIVITY_STATUS", columnDefinition = "VARCHAR(20)")
    private ActivityStatusEnum activityStatus;
    
    private UUID activityId;
    private UUID transactionId;
    private String action;
    private Boolean activeObject;
    

}
