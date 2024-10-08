/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author Waldir.Valle
 */
@Entity
@Table(name = "FISCAL_INFOS", indexes = @Index(columnList = "code,name"))

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class FiscalInfo extends BaseSerializedEntity{
    
    @Column(name = "CODE")
    private String code;
    @Column(name = "NAME")
    private String name;
    @ManyToOne
    @JoinColumn(name = "COUNTRY_ID", nullable = false)
    private Country country;
    @Column(name = "STREET")
    private String street;
    @Column(name = "EXTERNAL_NUMBER", columnDefinition = "VARCHAR(45)")
    private String externalNumber;
    @Column(name = "INTERNAL_NUMBER", columnDefinition = "VARCHAR(45)")
    private String internalNumber;
    @Column(name = "COLONY", columnDefinition = "VARCHAR(100)")
    private String colony;
    @Column(name = "SUBURB", columnDefinition = "VARCHAR(100)")
    private String suburb;
    @Column(name = "CITY", columnDefinition = "VARCHAR(100)")
    private String city;
    @Column(name = "STATE", columnDefinition = "VARCHAR(100)")
    private String state;
    @Column(name = "ZIP_CODE", columnDefinition = "VARCHAR(10)")
    private String zipCode;
    
}
