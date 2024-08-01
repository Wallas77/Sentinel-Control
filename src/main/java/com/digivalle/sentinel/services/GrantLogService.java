package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.GrantLogManager;
import com.digivalle.sentinel.models.GrantLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class GrantLogService {
    private final static Logger logger = LoggerFactory.getLogger(GrantLogService.class);

    @Autowired
    private GrantLogManager grantLogManager;
    
    
    public GrantLog getById(UUID grantLogId) throws EntityNotExistentException {
        return grantLogManager.getById(grantLogId);
    }
    
    public PagedResponse<GrantLog> getGrantLog(GrantLog grantLog,   Paging paging) {
        return grantLogManager.getGrantLog(grantLog, paging);
    }
    
    public List<GrantLog> findAll() {
        return grantLogManager.findAll();
    }
    
    public GrantLog createGrantLog(GrantLog grantLog) throws BusinessLogicException, ExistentEntityException {
        return grantLogManager.createGrantLog(grantLog);
    }
    
    public GrantLog updateGrantLog(UUID grantLogId,GrantLog grantLog) throws BusinessLogicException, EntityNotExistentException {
        return grantLogManager.updateGrantLog(grantLogId, grantLog);
    }
    
    public void deleteGrantLog(UUID grantLogId) throws EntityNotExistentException {
        grantLogManager.deleteGrantLog(grantLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createGrantLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createGrantLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


