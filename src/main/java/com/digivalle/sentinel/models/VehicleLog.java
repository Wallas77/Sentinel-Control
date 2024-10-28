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
@Table(name = "VEHICLES_LOG", indexes = @Index(columnList = "plates,sub_brand"))
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class VehicleLog extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "VEHICLE_BRAND_ID", nullable = true)
    private VehicleBrand vehicleBrand;
    @Column(name = "PLATES")
    private String plates;
    @Column(name = "SUB_BRAND")
    private String subBrand;
    @Column(name = "COLOR")
    private String color;
    @Column(name = "DESCRIPTION")
    private String description;
    
    private UUID vehicleId;
    private UUID transactionId;
    private String action;
    private Boolean activeObject;
}
