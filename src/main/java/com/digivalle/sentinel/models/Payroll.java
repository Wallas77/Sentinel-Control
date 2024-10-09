/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.models;

import com.digivalle.sentinel.models.enums.PayrollStatusEnum;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author Waldir.Valle
 */
@Entity
@Table(name = "PAYROLLS", indexes = @Index(columnList = "name"))
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class Payroll extends BaseSerializedEntity{
    @Column(name = "NAME")
    private String name;
    @Column(name = "START_DATE", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date startDate;
    @Column(name = "END_DATE", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date endDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "PAYROLL_STATUS", columnDefinition = "VARCHAR(20)")
    private PayrollStatusEnum payrollStatusEnum;
    @Column(name = "SCHEDULED_DATE", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date scheduledDate;
    @Transient
    private Date scheduledDate2;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "payroll", orphanRemoval = true)
    private List<PayrollRole> payrollRoles = new ArrayList<>();
}
