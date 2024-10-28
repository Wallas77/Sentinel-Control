/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.digivalle.sentinel.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author Wallas
 */
@Entity
@Table(name = "PAYROLL_ROLES_LOG")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class PayrollRoleLog extends BaseEntity {
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "PAYROLL_ID", nullable = false)
    private Payroll payroll;
    @ManyToOne
    @JoinColumn(name = "ROLE_ID", nullable = false)
    private Role role;
    
    private UUID payrollRoleId;
    private UUID transactionId;
    private String action;
    private Boolean activeObject;
}
