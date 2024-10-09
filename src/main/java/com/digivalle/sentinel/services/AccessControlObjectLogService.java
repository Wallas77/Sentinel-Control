package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.AccessControlObjectLogManager;
import com.digivalle.sentinel.models.AccessControlObjectLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AccessControlObjectLogService {
    private final static Logger logger = LoggerFactory.getLogger(AccessControlObjectLogService.class);

    @Autowired
    private AccessControlObjectLogManager accessControlObjectLogManager;
    
    
    public AccessControlObjectLog getById(UUID accessControlObjectLogId) throws EntityNotExistentException {
        return accessControlObjectLogManager.getById(accessControlObjectLogId);
    }
    
    public PagedResponse<AccessControlObjectLog> getAccessControlObjectLog(AccessControlObjectLog accessControlObjectLog,   Paging paging) {
        return accessControlObjectLogManager.getAccessControlObjectLog(accessControlObjectLog, paging);
    }
    
    public List<AccessControlObjectLog> findAll() {
        return accessControlObjectLogManager.findAll();
    }
    
    public AccessControlObjectLog createAccessControlObjectLog(AccessControlObjectLog accessControlObjectLog) throws BusinessLogicException, ExistentEntityException {
        return accessControlObjectLogManager.createAccessControlObjectLog(accessControlObjectLog);
    }
    
    public AccessControlObjectLog updateAccessControlObjectLog(UUID accessControlObjectLogId,AccessControlObjectLog accessControlObjectLog) throws BusinessLogicException, EntityNotExistentException {
        return accessControlObjectLogManager.updateAccessControlObjectLog(accessControlObjectLogId, accessControlObjectLog);
    }
    
    public void deleteAccessControlObjectLog(UUID accessControlObjectLogId) throws EntityNotExistentException {
        accessControlObjectLogManager.deleteAccessControlObjectLog(accessControlObjectLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createAccessControlObjectLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createAccessControlObjectLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


