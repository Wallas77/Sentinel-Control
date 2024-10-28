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
@Table(name = "SERVICES_LOG")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class ServiceLog extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    private Customer customer;
    @Column(name = "NAME" , columnDefinition = "VARCHAR(100)")
    private String name;
    @Column(name = "START_CONTRACT_DATE", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date startContractDate;
    @Transient
    private Date startContractDate2;
    @Column(name = "END_CONTRACT_DATE", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date endContractDate;
    @Transient
    private Date endContractDate2;
    
    private UUID serviceId;
    private UUID transactionId;
    private String action;
    private Boolean activeObject;

}
