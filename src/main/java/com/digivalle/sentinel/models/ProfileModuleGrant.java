/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "PROFILE_MODULE_GRANT")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class ProfileModuleGrant extends BaseEntity{

    
    //@ManyToOne(fetch = FetchType.LAZY)
    @ManyToOne(optional=false) 
    @JoinColumn(name = "MODULE_ID", nullable = false)
    private Module module;
    //@ManyToOne(fetch = FetchType.LAZY)
    @ManyToOne(optional=false) 
    @JoinColumn(name = "GRANT_ID", nullable = false)
    private Grant grant;
    @JsonIgnore
    //@ManyToOne(fetch = FetchType.LAZY)
    @ManyToOne(optional=false) 
    @JoinColumn(name = "PROFILE_ID", nullable = false)
    private Profile profile;
   

}
