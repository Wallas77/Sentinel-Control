package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.TokenLog;
import com.digivalle.sentinel.models.User;
import java.util.Date;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public interface TokenLogRepository extends JpaRepository<TokenLog, UUID>{
    TokenLog getByToken(String token);
    List<TokenLog> findByUser(User user);
    List<TokenLog> getByValidUntilBefore(Date validUntil);
}
