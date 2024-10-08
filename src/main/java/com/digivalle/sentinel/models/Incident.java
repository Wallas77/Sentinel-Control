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
@Table(name = "INCIDENTS", indexes = @Index(columnList = "employee_id"))
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class Incident extends BaseSerializedEntity{
    @ManyToOne
    @JoinColumn(name = "SERVICE_ID", nullable = false)
    private Service service;
    @ManyToOne
    @JoinColumn(name = "INCIDENT_TYPE_ID", nullable = false)
    private IncidentType incidentType;
    @ManyToOne
    @JoinColumn(name = "EMPLOYEE_ID", nullable = false)
    private Employee employee;
    @Column(name = "NOTES", columnDefinition = "TEXT")
    private String notes;

}
