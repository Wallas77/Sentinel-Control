package com.digivalle.sentinel.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Date;
import java.util.UUID;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "TOKENS_LOG")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class TokenLog extends BaseEntity{

    @Column(name = "token", length = 255)
    private String token;
    @Column(name = "VALID_UNTIL")
    private Date validUntil;
    @Column(name = "USER_IDENTIFIER")
    private UUID userIdentifier;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private User user;
    
    private UUID tokenId;
    private UUID transactionId;
    private String action;
    private Boolean activeObject;

    
    
   
}
