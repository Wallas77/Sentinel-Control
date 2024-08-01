package com.digivalle.sentinel.models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.security.crypto.bcrypt.BCrypt;

@Entity
//@Table(name = "USERS", indexes = @Index(columnList = "name, email"))
@Table(name = "USERS")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class User extends BaseSerializedEntity{

    @ManyToOne
    @JoinColumn(name = "PROFILE_ID", nullable = false)
    private Profile profile;
    @Column(name = "NAME")
    private String name;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "PASSWORD")
    public String password;
   
    
    public User(String username, String password){
        this.email = username;
        if(password!=null){
            this.password = BCrypt.hashpw(password,BCrypt.gensalt());
        } else {
            this.password = password;
        }
    }

    
    public void setPassword(String password) {
        //System.out.println("password=>"+password);
        if(password!=null){
            this.password = BCrypt.hashpw(password,BCrypt.gensalt());
        } else {
            this.password = password;
        }
        //this.password = password;
    }
 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        return name != null && name.equals(((User) o).name);
    }
 
    @Override
    public int hashCode() {
        return 31;
    }

   
   
}
