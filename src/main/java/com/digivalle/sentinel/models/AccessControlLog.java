/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.models;

import com.digivalle.sentinel.models.enums.AccessControlTypeEnum;
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
@Table(name = "ACCESS_CONTROLS_LOG", indexes = @Index(columnList = "name,id_number"))
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class AccessControlLog extends BaseEntity{
    @Enumerated(EnumType.STRING)
    @Column(name = "ACCESS_CONTROL_TYPE", columnDefinition = "VARCHAR(30)")
    private AccessControlTypeEnum accessControlType;
    @Column(name = "NAME" , columnDefinition = "VARCHAR(100)")
    private String name;
    @Column(name = "LAST_NAMES")
    private String lastNames;
    @Column(name = "ID_NUMBER")
    private String idNumber;
    @Column(name = "ID_IMAGE")
    private byte[] idImage;
    @Column(name = "PHOTO")
    private byte[] photo;
    @Column(name = "COMPANY")
    private String company;
    @Column(name = "VISIT_REASON")
    private String visitReason;
    @ManyToOne
    @JoinColumn(name = "CUSTOMER_DIRECTORY_ID", nullable = true)
    private CustomerDirectory customerDirectory;
    @ManyToOne
    @JoinColumn(name = "CONTACT_ID", nullable = true)
    private Contact contact;
    @Column(name = "VISIT_PERSON")
    private String visitPerson;
    @ManyToOne
    @JoinColumn(name = "SUPPLIER_ID", nullable = true)
    private Supplier supplier;
    @ManyToOne
    @JoinColumn(name = "EMPLOYEE_ID", nullable = true)
    private Employee employee;
    @Column(name = "ACCESS_DATE", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date accessDate;
    @Transient
    private Date accessDate2;
    @Column(name = "EXIT_DATE", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date exitDate;
    @Transient
    private Date exitDate2;
    @Column(name = "TOTAL_RESIDENCE_TIME")
    private String totalResidenceTime;
    @ManyToOne
    @JoinColumn(name = "VEHICLE_ID", nullable = true)
    private Vehicle vehicle;
    @Column(name = "PARKINGLOT_NUMBER")
    private String parkinglotNumber;
    
    @Column(name = "NOTES", columnDefinition = "TEXT")
    private String notes;
    @Column(name = "SIGNATURE")
    private byte[] signature;
    
    private UUID accessControlId;
    private UUID transactionId;
    private String action;
    private Boolean activeObject;
    

}
