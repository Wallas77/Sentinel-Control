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
@Table(name = "SERVICES", indexes = @Index(columnList = "name, customer_id"))
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class Service extends BaseSerializedEntity{
    @ManyToOne
    @JoinColumn(name = "CUSTOMER_ID", nullable = false)
    private Customer customer;
    @ManyToOne
    @JoinColumn(name = "BRANCH_ID", nullable = true)
    private Branch branch;
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

}
