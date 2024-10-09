package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.AccessControlLogManager;
import com.digivalle.sentinel.models.AccessControlLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class AccessControlLogService {
    private final static Logger logger = LoggerFactory.getLogger(AccessControlLogService.class);

    @Autowired
    private AccessControlLogManager accessControlLogManager;
    
    
    public AccessControlLog getById(UUID accessControlLogId) throws EntityNotExistentException {
        return accessControlLogManager.getById(accessControlLogId);
    }
    
    public PagedResponse<AccessControlLog> getAccessControlLog(AccessControlLog accessControlLog,   Paging paging) {
        return accessControlLogManager.getAccessControlLog(accessControlLog, paging);
    }
    
    public List<AccessControlLog> findAll() {
        return accessControlLogManager.findAll();
    }
    
    public AccessControlLog createAccessControlLog(AccessControlLog accessControlLog) throws BusinessLogicException, ExistentEntityException {
        return accessControlLogManager.createAccessControlLog(accessControlLog);
    }
    
    public AccessControlLog updateAccessControlLog(UUID accessControlLogId,AccessControlLog accessControlLog) throws BusinessLogicException, EntityNotExistentException {
        return accessControlLogManager.updateAccessControlLog(accessControlLogId, accessControlLog);
    }
    
    public void deleteAccessControlLog(UUID accessControlLogId) throws EntityNotExistentException {
        accessControlLogManager.deleteAccessControlLog(accessControlLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createAccessControlLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createAccessControlLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


