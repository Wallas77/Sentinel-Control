package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.ApplicationLogManager;
import com.digivalle.sentinel.models.ApplicationLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ApplicationLogService {
    private final static Logger logger = LoggerFactory.getLogger(ApplicationLogService.class);

    @Autowired
    private ApplicationLogManager applicationLogManager;
    
    
    public ApplicationLog getById(UUID applicationLogId) throws EntityNotExistentException {
        return applicationLogManager.getById(applicationLogId);
    }
    
    public PagedResponse<ApplicationLog> getApplicationLog(ApplicationLog applicationLog,   Paging paging) {
        return applicationLogManager.getApplicationLog(applicationLog, paging);
    }
    
    public List<ApplicationLog> findAll() {
        return applicationLogManager.findAll();
    }
    
    public ApplicationLog createApplicationLog(ApplicationLog applicationLog) throws BusinessLogicException, ExistentEntityException {
        return applicationLogManager.createApplicationLog(applicationLog);
    }
    
    public ApplicationLog updateApplicationLog(UUID applicationLogId,ApplicationLog applicationLog) throws BusinessLogicException, EntityNotExistentException {
        return applicationLogManager.updateApplicationLog(applicationLogId, applicationLog);
    }
    
    public void deleteApplicationLog(UUID applicationLogId) throws EntityNotExistentException {
        applicationLogManager.deleteApplicationLog(applicationLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createApplicationLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createApplicationLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


