package com.digivalle.sentinel.containers;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data

public class LoginRequest {
    private String username;
    private String password;
    private Integer sessionTime;

    public LoginRequest(@JsonProperty("username") String username, @JsonProperty("password") String password , @JsonProperty("sessionTime") Integer sessionTime){
        this.username = username;
        this.password = password;
        this.sessionTime = sessionTime;
    }

    
}
