/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.models;

import com.digivalle.sentinel.models.enums.ContactTypeEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Date;
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
@Table(name = "CONTACTS_LOG")

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class ContactLog extends BaseSerializedEntity{
    
    @ManyToOne
    @JoinColumn(name = "CUSTOMER_DIRECTORY_ID", nullable = false)
    private CustomerDirectory customerDirectory;
    @Column(name = "NAME", columnDefinition = "VARCHAR(150)")
    private String name;
    @Column(name = "FIRST_SURNAME", columnDefinition = "VARCHAR(150)")
    private String firstSurname;
    @Column(name = "SECOND_SURNAME", columnDefinition = "VARCHAR(150)")
    private String secondSurname;
    @Column(name = "PHOTO")
    private byte[] photo;
    @Column(name = "BIRTHDAY", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date birthday;
    @Column(name = "NATIONALITY", columnDefinition = "VARCHAR(50)")
    private String nationality;
    @Enumerated(EnumType.STRING)
    @Column(name = "CONTACT_TYPE", columnDefinition = "VARCHAR(50)")
    private ContactTypeEnum contactType;
    
    @Column(name = "MOBILE_PHONE", columnDefinition = "VARCHAR(50)")
    private String mobilePhone;
    @Column(name = "HOME_PHONE", columnDefinition = "VARCHAR(50)")
    private String homePhone;
    @Column(name = "EMAIL", columnDefinition = "VARCHAR(150)")
    private String email;
    
    private UUID contactId;
    private UUID transactionId;
    private String action;
    

}
