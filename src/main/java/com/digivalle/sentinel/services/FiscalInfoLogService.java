package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.FiscalInfoLogManager;
import com.digivalle.sentinel.models.FiscalInfoLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class FiscalInfoLogService {
    private final static Logger logger = LoggerFactory.getLogger(FiscalInfoLogService.class);

    @Autowired
    private FiscalInfoLogManager fiscalInfoLogManager;
    
    
    public FiscalInfoLog getById(UUID fiscalInfoLogId) throws EntityNotExistentException {
        return fiscalInfoLogManager.getById(fiscalInfoLogId);
    }
    
    public PagedResponse<FiscalInfoLog> getFiscalInfoLog(FiscalInfoLog fiscalInfoLog,   Paging paging) {
        return fiscalInfoLogManager.getFiscalInfoLog(fiscalInfoLog, paging);
    }
    
    public List<FiscalInfoLog> findAll() {
        return fiscalInfoLogManager.findAll();
    }
    
    public FiscalInfoLog createFiscalInfoLog(FiscalInfoLog fiscalInfoLog) throws BusinessLogicException, ExistentEntityException {
        return fiscalInfoLogManager.createFiscalInfoLog(fiscalInfoLog);
    }
    
    public FiscalInfoLog updateFiscalInfoLog(UUID fiscalInfoLogId,FiscalInfoLog fiscalInfoLog) throws BusinessLogicException, EntityNotExistentException {
        return fiscalInfoLogManager.updateFiscalInfoLog(fiscalInfoLogId, fiscalInfoLog);
    }
    
    public void deleteFiscalInfoLog(UUID fiscalInfoLogId) throws EntityNotExistentException {
        fiscalInfoLogManager.deleteFiscalInfoLog(fiscalInfoLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createFiscalInfoLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createFiscalInfoLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


