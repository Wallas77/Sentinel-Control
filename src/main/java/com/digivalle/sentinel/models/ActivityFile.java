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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


/**
 *
 * @author Waldir.Valle
 */
@Entity
@Table(name = "ACTIVITY_FILES")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class ActivityFile extends BaseSerializedEntity{
    @ManyToOne
    @JoinColumn(name = "ACTIVITY_ID", nullable = false)
    private Activity activity;
    @Column(name = "NAME")
    private String name;
    @Column(name = "FILE")
    private byte[] file;
    @Column(name = "FILE_FORMAT", columnDefinition = "VARCHAR(100)")
    private String fileFormat;
}
