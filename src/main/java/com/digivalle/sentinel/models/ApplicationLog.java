/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "APPLICATIONS_LOG")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class ApplicationLog extends BaseEntity{
    @Column(name = "NAME", columnDefinition = "VARCHAR(100)")
    private String name;
    @Column(name = "DESCRIPTION")
    private String description;
    private UUID applicationId;
    private UUID transactionId;
    private String action;

}