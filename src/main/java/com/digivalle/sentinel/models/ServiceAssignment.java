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
import java.time.LocalTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author Waldir.Valle
 */
@Entity
@Table(name = "SERVICE_ASSIGNMENTS", indexes = @Index(columnList = "service_id, role_id, employee_id"))
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class ServiceAssignment extends BaseSerializedEntity{
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
    @Column(name = "RECURRENCE_IN_DAYS")
    private Integer recurrenceInDays;
    @Column(name = "RECURRENCE_PER_WEEK_DAYS")
    private String recurrencePerWeekDays;
    @Column(name = "RECURRENCE_PER_NUMBER_DAYS")
    private String recurrencePerNumberDays;
    @Column(name = "ENTRY_TIME")
    public LocalTime entryTime;

    

}
