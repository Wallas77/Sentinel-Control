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
import jakarta.persistence.Transient;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 *
 * @author Waldir.Valle
 */
@Entity
@Table(name = "EMPLOYEE_HEALTH_CHECKUPS", indexes = @Index(columnList = "name"))

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class EmployeeHealthCheckup extends BaseSerializedEntity{
    @ManyToOne
    @JoinColumn(name = "EMPLOYEE_ID", nullable = false)
    private Employee employee;
    @Column(name = "NAME")
    private String name;
    @Column(name = "NOTES", columnDefinition = "TEXT")
    private String notes;
    @Column(name = "FILE_NAME")
    private String fileName;
    @Column(name = "FILE")
    private byte[] file;
    @Column(name = "ISSUED_DATE", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date issuedDate;
    @Transient
    private Date issuedDate2;
    @Column(name = "FILE_FORMAT", columnDefinition = "VARCHAR(100)")
    private String fileFormat;
}
