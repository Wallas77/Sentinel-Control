package com.digivalle.sentinel.models;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import java.io.Serializable;
import java.util.Date;
import java.util.UUID;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@MappedSuperclass
@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class BaseSerializedEntity implements Serializable{

    @Id
    @GeneratedValue
    @Column(name = "ID", unique = true)
    private UUID id;
    @Column(name = "CREATION_DATE", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date creationDate;
    @Column(name = "UPDATE_DATE", columnDefinition= "TIMESTAMP WITH TIME ZONE")
    private Date updateDate;
    @Column(name = "ACTIVE")
    private Boolean active;
    @Column(name = "DELETED")
    private Boolean deleted;
    @Column(insertable = false, updatable = false, columnDefinition="serial")
    private Integer serial;
    private String updateUser;
    @Transient
    private Date creationDate2;
    @Transient
    private Date updateDate2;

    
    @PrePersist
    public void onCreate(){
        //this.setId(UUID.randomUUID());
        this.creationDate = new Date();
        this.deleted = false;
        this.active = true;
    }

    @PreUpdate
    public void onUpdate(){
        this.updateDate = new Date();
    }

}
