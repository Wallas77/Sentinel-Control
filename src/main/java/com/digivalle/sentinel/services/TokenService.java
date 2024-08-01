package com.digivalle.sentinel.services;

import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.ApplicationManager;
import com.digivalle.sentinel.managers.TokenLogManager;
import com.digivalle.sentinel.managers.TokenManager;
import com.digivalle.sentinel.models.Token;
import com.digivalle.sentinel.models.TokenLog;
import com.digivalle.sentinel.models.User;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class TokenService {
    private final static Logger logger = LoggerFactory.getLogger(TokenService.class);

    @Autowired
    private TokenManager tokenManager;
    
    @Autowired
    private TokenLogManager tokenLogManager;
    
    @Autowired
    private ApplicationManager applicationManager;
    
    
    public Token getById(UUID tokenId) throws EntityNotExistentException {
        return tokenManager.getById(tokenId);
    }
    
    public PagedResponse<Token> getToken(Token token,   Paging paging) {
        return tokenManager.getToken(token, paging);
    }
    
    public List<Token> findAll() {
        return tokenManager.findAll();
    }
    
    public Token getByUser(User user){
        return tokenManager.getByUser(user);
    }
    
    public Token getByToken(String token){
        return tokenManager.getByToken(token);
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Token createToken(Token token) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        Token tokenPersisted = tokenManager.createToken(token);
        tokenLogManager.createTokenLog(convertLog(tokenPersisted,null,Definitions.LOG_CREATE));
        return getById(tokenPersisted.getId());
    }
    
    public Token createToken(User user, Integer sessionTime){
        return tokenManager.createToken(user, sessionTime);
    }
    
    
    public void deleteToken(UUID tokenId) throws EntityNotExistentException, BusinessLogicException {
        tokenLogManager.createTokenLog(convertLog(getById(tokenId),null,Definitions.LOG_DELETE));
        tokenManager.deleteToken(tokenId);
    }
    
    public void deleteToken(Token token) throws EntityNotExistentException, BusinessLogicException {
        tokenLogManager.createTokenLog(convertLog(token,null,Definitions.LOG_DELETE));
        tokenManager.deleteToken(token.getId());
    }
    
    public void deleteActiveTokensByUser(User user) throws EntityNotExistentException, BusinessLogicException {
        final List<Token> tokens = tokenManager.findByUser(user);
        for(Token token : tokens){
            token.setUpdateUser(user.getEmail());
            deleteToken(token);
        }
    }
    
    
    public Boolean initialize()  {
        try{
            createTokens();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createTokens() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        
    }
    
    public static TokenLog convertLog (Token token, UUID transactionId, String action){
        Gson gson= new Gson();
        System.out.println("token=>"+token.getId());
        //String tmp = gson.toJson(token);
        //TokenLog tokenLog = gson.fromJson(tmp,TokenLog.class);
        TokenLog tokenLog = new TokenLog();
        tokenLog.setActive(token.getActive());
        tokenLog.setCreationDate(token.getCreationDate());
        tokenLog.setDeleted(token.getDeleted());
        tokenLog.setToken(token.getToken());
        tokenLog.setTokenId(token.getId());
        tokenLog.setId(null);
        tokenLog.setUser(new User());
        tokenLog.getUser().setId(token.getUser().getId());
        tokenLog.setUpdateUser(token.getUpdateUser());
        tokenLog.setValidUntil(token.getValidUntil());
        tokenLog.setUpdateDate(null);
        tokenLog.setTransactionId(transactionId);
        tokenLog.setAction(action);
        tokenLog.setUserIdentifier(token.getUser().getId());
        return tokenLog;
    }

    
}


