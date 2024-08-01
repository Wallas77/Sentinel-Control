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
@Table(name = "ACTIVITY_FILES_LOG")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class ActivityFileLog extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "ACTIVITY_ID", nullable = false)
    private Activity activity;
    @Column(name = "NAME")
    private String name;
    @Column(name = "FILE")
    private byte[] file;

    private UUID activityFileId;
    private UUID transactionId;
    private String action;

}
