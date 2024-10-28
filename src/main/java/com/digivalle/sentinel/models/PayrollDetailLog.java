/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "PAYROLL_DETAILS_LOG")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class PayrollDetailLog extends BaseEntity{
    @Column(name = "NAME")
    private String name;
    @ManyToOne
    @JoinColumn(name = "EMPLOYEE_ID", nullable = false)
    private Employee employee;
    @Column(name = "START_DATE", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date startDate;
    @Column(name = "END_DATE", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date endDate;
    @Column(name = "SALARY_AMOUNT")
    private Double salaryAmount;
    @Transient
    private Double salaryAmount2;
    @Column(name = "PAYROLL_DAYS")
    private Integer payrollDays;
    @Transient
    private Integer payrollDays2;
    @Column(name = "SALARY_PER_DAY")
    private Double salaryPerDay;
    @Transient
    private Double salaryPerDay2;
    @Column(name = "DAYS_WORKED")
    private Integer daysWorked;
    @Transient
    private Integer daysWorked2;
    @Column(name = "PAYROLL_SALARY")
    private Double payRollSalary;
    @Transient
    private Double payRollSalary2;
    @Column(name = "INCIDENTS_AMOUNT")
    private Double incidentsAmount;
    @Transient
    private Double incidentsAmount2;
    @Column(name = "ACTIVITIES_BONUS_AMOUNT")
    private Double activitiesBonusAmount;
    @Transient
    private Double activitiesBonusAmount2;
    @Column(name = "TOTAL_PAYROLL_SALARY")
    private Double totalPayRollSalary;
    @Transient
    private Double totalPayRollSalary2;
    
    
    private UUID payrollDetailId;
    private UUID transactionId;
    private String action;
    private Boolean activeObject;

}
