package com.digivalle.sentinel.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Date;
import java.util.UUID;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "TOKENS")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class Token extends BaseEntity{

    @Column(name = "token", length = 255)
    private String token;
    @Column(name = "VALID_UNTIL")
    private Date validUntil;
    @Column(name = "USER_IDENTIFIER")
    private UUID userIdentifier;
    @Transient
    private String userProfile;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private User user;
   
}
