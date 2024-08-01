package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.TokenLogManager;
import com.digivalle.sentinel.models.TokenLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class TokenLogService {
    private final static Logger logger = LoggerFactory.getLogger(TokenLogService.class);

    @Autowired
    private TokenLogManager tokenLogManager;
    
    
    public TokenLog getById(UUID tokenLogId) throws EntityNotExistentException {
        return tokenLogManager.getById(tokenLogId);
    }
    
    public PagedResponse<TokenLog> getTokenLog(TokenLog tokenLog,   Paging paging) {
        return tokenLogManager.getTokenLog(tokenLog, paging);
    }
    
    public List<TokenLog> findAll() {
        return tokenLogManager.findAll();
    }
    
    public TokenLog createTokenLog(TokenLog tokenLog) throws BusinessLogicException, ExistentEntityException {
        return tokenLogManager.createTokenLog(tokenLog);
    }
    
    public void deleteTokenLog(UUID tokenLogId) throws EntityNotExistentException {
        tokenLogManager.deleteTokenLog(tokenLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createTokenLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createTokenLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


