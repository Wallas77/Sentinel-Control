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
@Table(name = "ACCESS_CONTROL_OBJECTS_LOG", indexes = @Index(columnList = "serial_number,name"))
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class AccessControlObjectLog extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "ACCESS_CONTROL_ID", nullable = false)
    private AccessControl accessControl;
    @Column(name = "SERIAL_NUMBER" , columnDefinition = "VARCHAR(100)")
    private String serialNumber;
    @Column(name = "NAME" , columnDefinition = "VARCHAR(100)")
    private String name;
    @Column(name = "DESCRIPTION")
    private String description;

    private UUID accessControlObjectId;
    private UUID transactionId;
    private String action;
    private Boolean activeObject;
}
