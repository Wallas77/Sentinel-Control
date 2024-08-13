/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.models;

import com.digivalle.sentinel.models.enums.BloodTypeEnum;
import com.digivalle.sentinel.models.enums.BodyComplexionEnum;
import com.digivalle.sentinel.models.enums.EyeColorEnum;
import com.digivalle.sentinel.models.enums.HairColorEnum;
import com.digivalle.sentinel.models.enums.SkinColorEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
@Table(name = "EMPLOYEES_LOG")

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class EmployeeLog extends BaseEntity{
    @Column(name = "CODE", columnDefinition = "VARCHAR(50)")
    private String code;
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
    @Column(name = "HEIGHT")
    private Double height;
    @Transient
    private Double height2;
    @Column(name = "WEIGHT")
    private Double weight;
    @Transient
    private Double weight2;
    @Enumerated(EnumType.STRING)
    @Column(name = "HAIR_COLOR", columnDefinition = "VARCHAR(50)")
    private HairColorEnum hairColor;
    @Enumerated(EnumType.STRING)
    @Column(name = "SKIN_COLOR", columnDefinition = "VARCHAR(50)")
    private SkinColorEnum skinColor;
    @Enumerated(EnumType.STRING)
    @Column(name = "EYES_COLOR", columnDefinition = "VARCHAR(50)")
    private EyeColorEnum eyesColor;
    @Enumerated(EnumType.STRING)
    @Column(name = "BLOOD_TYPE", columnDefinition = "VARCHAR(20)")
    private BloodTypeEnum bloodType;
    @Enumerated(EnumType.STRING)
    @Column(name = "BODY_COMPLEXION", columnDefinition = "VARCHAR(50)")
    private BodyComplexionEnum bodyComplexion;
    @ManyToOne
    @JoinColumn(name = "COUNTRY_ID", nullable = true)
    private Country country;
    @Column(name = "STREET")
    private String street;
    @Column(name = "EXTERNAL_NUMBER", columnDefinition = "VARCHAR(45)")
    private String externalNumber;
    @Column(name = "INTERNAL_NUMBER", columnDefinition = "VARCHAR(45)")
    private String internalNumber;
    @Column(name = "COLONY", columnDefinition = "VARCHAR(100)")
    private String colony;
    @Column(name = "SUBURB", columnDefinition = "VARCHAR(100)")
    private String suburb;
    @Column(name = "CITY", columnDefinition = "VARCHAR(100)")
    private String city;
    @Column(name = "STATE", columnDefinition = "VARCHAR(100)")
    private String state;
    @Column(name = "ZIP_CODE", columnDefinition = "VARCHAR(10)")
    private String zipCode;
    @Column(name = "MOBILE_PHONE", columnDefinition = "VARCHAR(50)")
    private String mobilePhone;
    @Column(name = "HOME_PHONE", columnDefinition = "VARCHAR(50)")
    private String homePhone;
    @Column(name = "EMAIL", columnDefinition = "VARCHAR(150)")
    private String email;
    @Column(name = "SALARY_AMOUNT")
    private Double salaryAmount;
    @Transient
    private Double salaryAmount2;
    @Column(name = "EMERGENCY_CONTACT_NAME")
    private String emergencyContactName;
    @Column(name = "EMERGENCY_CONTACT_PHONE")
    private String emergencyContactPhone;
    @Column(name = "START_CONTRACT_DATE", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date startContractDate;
    @Transient
    private Date startContractDate2;
    @Column(name = "END_CONTRACT_DATE", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date endContractDate;
    @Transient
    private Date endContractDate2;
    
    private UUID employeeId;
    private UUID transactionId;
    private String action;
    

}
