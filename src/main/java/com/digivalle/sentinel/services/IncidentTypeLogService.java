package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.IncidentTypeLogManager;
import com.digivalle.sentinel.models.IncidentTypeLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class IncidentTypeLogService {
    private final static Logger logger = LoggerFactory.getLogger(IncidentTypeLogService.class);

    @Autowired
    private IncidentTypeLogManager incidentTypeLogManager;
    
    
    public IncidentTypeLog getById(UUID incidentTypeLogId) throws EntityNotExistentException {
        return incidentTypeLogManager.getById(incidentTypeLogId);
    }
    
    public PagedResponse<IncidentTypeLog> getIncidentTypeLog(IncidentTypeLog incidentTypeLog,   Paging paging) {
        return incidentTypeLogManager.getIncidentTypeLog(incidentTypeLog, paging);
    }
    
    public List<IncidentTypeLog> findAll() {
        return incidentTypeLogManager.findAll();
    }
    
    public IncidentTypeLog createIncidentTypeLog(IncidentTypeLog incidentTypeLog) throws BusinessLogicException, ExistentEntityException {
        return incidentTypeLogManager.createIncidentTypeLog(incidentTypeLog);
    }
    
    public IncidentTypeLog updateIncidentTypeLog(UUID incidentTypeLogId,IncidentTypeLog incidentTypeLog) throws BusinessLogicException, EntityNotExistentException {
        return incidentTypeLogManager.updateIncidentTypeLog(incidentTypeLogId, incidentTypeLog);
    }
    
    public void deleteIncidentTypeLog(UUID incidentTypeLogId) throws EntityNotExistentException {
        incidentTypeLogManager.deleteIncidentTypeLog(incidentTypeLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createIncidentTypeLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createIncidentTypeLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


