/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.models;

import com.digivalle.sentinel.models.enums.PayrollStatusEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "PAYROLLS_LOG")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class PayrollLog extends BaseEntity{
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
    
    private UUID payrollId;
    private UUID transactionId;
    private String action;

}
