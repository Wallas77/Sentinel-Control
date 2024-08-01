package com.digivalle.sentinel.models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCrypt;

@Entity
@Table(name = "USERS_LOG")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserLog extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "PROFILE_ID", nullable = false)
    private Profile profile;
    @Column(name = "NAME")
    private String name;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "COMMISSION")
    public Double commission;
    
    private UUID userId;
    private UUID transactionId;
    private String action;
   
   
    
    public UserLog(String username, String password){
        this.email = username;
        this.password = password;
    }

    
    public void setPassword(String password) {
        this.password = BCrypt.hashpw(password,BCrypt.gensalt());
        //this.password = password;
    }
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserLog)) return false;
        return name != null && name.equals(((UserLog) o).name);
    }
 
    @Override
    public int hashCode() {
        return 31;
    }

   
   
}
