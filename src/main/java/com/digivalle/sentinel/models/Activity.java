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
@Table(name = "ACTIVITIES", indexes = @Index(columnList = "name"))
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class Activity extends BaseSerializedEntity{
    @Column(name = "NAME" , columnDefinition = "VARCHAR(100)")
    private String name;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "ACTIVITY_DATE", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date activityDate;
    @Column(name = "START_DATE", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date startDate;
    @Column(name = "END_DATE", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date endDate;
    @Column(name = "CANCELED_DATE", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date canceledDate;
    @Column(name = "REQUIRED_FILES")
    private Integer requiredFiles;
    @ManyToOne
    @JoinColumn(name = "ROLE_RESPONSABILITY_ID", nullable = false)
    private RoleResponsability roleResponsability;
    @ManyToOne
    @JoinColumn(name = "EMPLOYEE_ID", nullable = false)
    private Employee employee;
    

}
