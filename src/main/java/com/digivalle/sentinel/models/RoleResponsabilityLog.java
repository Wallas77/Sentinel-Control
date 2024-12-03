/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.models;

import com.digivalle.sentinel.models.enums.TimePeriodEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalTime;
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
@Table(name = "ROLE_RESPONSABILITIES_LOG")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class RoleResponsabilityLog extends BaseEntity{
    @Column(name = "NAME" , columnDefinition = "VARCHAR(100)")
    private String name;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "RECURRENCE")
    private Integer recurrence;
    @Enumerated(EnumType.STRING)
    @Column(name = "TIME_PERIOD")
    private TimePeriodEnum timePeriod;
    @Column(name = "START_DATE", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date startDate;
    @Column(name = "END_DATE", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date endDate;
    @Column(name = "ENTRY_TIME")
    public LocalTime entryTime;
    @ManyToOne
    @JoinColumn(name = "ROLE_ID", nullable = false)
    private Role role;
    @Column(name = "EMPLOYEE_BONUS")
    private Double employeeBonus;
    @Column(name = "REQUIRED_FILES")
    private Integer requiredFiles;
    
    private UUID roleResponsabilityId;
    private UUID transactionId;
    private String action;
    private Boolean activeObject;
}
