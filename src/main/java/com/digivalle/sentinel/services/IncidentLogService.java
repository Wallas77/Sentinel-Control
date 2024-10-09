package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.IncidentLogManager;
import com.digivalle.sentinel.models.IncidentLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class IncidentLogService {
    private final static Logger logger = LoggerFactory.getLogger(IncidentLogService.class);

    @Autowired
    private IncidentLogManager incidentLogManager;
    
    
    public IncidentLog getById(UUID incidentLogId) throws EntityNotExistentException {
        return incidentLogManager.getById(incidentLogId);
    }
    
    public PagedResponse<IncidentLog> getIncidentLog(IncidentLog incidentLog,   Paging paging) {
        return incidentLogManager.getIncidentLog(incidentLog, paging);
    }
    
    public List<IncidentLog> findAll() {
        return incidentLogManager.findAll();
    }
    
    public IncidentLog createIncidentLog(IncidentLog incidentLog) throws BusinessLogicException, ExistentEntityException {
        return incidentLogManager.createIncidentLog(incidentLog);
    }
    
    public IncidentLog updateIncidentLog(UUID incidentLogId,IncidentLog incidentLog) throws BusinessLogicException, EntityNotExistentException {
        return incidentLogManager.updateIncidentLog(incidentLogId, incidentLog);
    }
    
    public void deleteIncidentLog(UUID incidentLogId) throws EntityNotExistentException {
        incidentLogManager.deleteIncidentLog(incidentLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createIncidentLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createIncidentLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


