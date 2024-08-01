package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.UserLogManager;
import com.digivalle.sentinel.models.UserLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class UserLogService {
    private final static Logger logger = LoggerFactory.getLogger(UserLogService.class);

    @Autowired
    private UserLogManager userLogManager;
    
    
    public UserLog getById(UUID userLogId) throws EntityNotExistentException {
        return userLogManager.getById(userLogId);
    }
    
    public PagedResponse<UserLog> getUserLog(UserLog userLog,   Paging paging) {
        return userLogManager.getUserLog(userLog, paging);
    }
    
    public List<UserLog> findAll() {
        return userLogManager.findAll();
    }
    
    public UserLog createUserLog(UserLog userLog) throws BusinessLogicException, ExistentEntityException {
        return userLogManager.createUserLog(userLog);
    }
    
    public UserLog updateUserLog(UUID userLogId,UserLog userLog) throws BusinessLogicException, EntityNotExistentException {
        return userLogManager.updateUserLog(userLogId, userLog);
    }
    
    public void deleteUserLog(UUID userLogId) throws EntityNotExistentException {
        userLogManager.deleteUserLog(userLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createUserLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createUserLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


