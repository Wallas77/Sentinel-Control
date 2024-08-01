package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.Token;
import com.digivalle.sentinel.models.User;
import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public interface TokenRepository extends JpaRepository<Token, UUID>{
    
    List<Token> findByUser(User user);
    List<Token> getByValidUntilBefore(Date validUntil);

    
    public Token getByToken(String token);
}
