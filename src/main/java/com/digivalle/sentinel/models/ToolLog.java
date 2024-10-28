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
@Table(name = "TOOLS_LOG")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class ToolLog extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "TOOL_TYPE_ID", nullable = false)
    private ToolType toolType;
    @Column(name = "ID_NUMBER" , columnDefinition = "VARCHAR(100)")
    private String idNumber;
    @Column(name = "NAME", columnDefinition = "VARCHAR(100)")
    private String name;
    @Column(name = "DESCRIPTION")
    private String description;
    private UUID toolId;
    private UUID transactionId;
    private String action;
    private Boolean activeObject;

}
