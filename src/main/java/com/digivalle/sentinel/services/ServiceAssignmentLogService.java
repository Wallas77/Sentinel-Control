package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.ServiceAssignmentLogManager;
import com.digivalle.sentinel.models.ServiceAssignmentLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ServiceAssignmentLogService {
    private final static Logger logger = LoggerFactory.getLogger(ServiceAssignmentLogService.class);

    @Autowired
    private ServiceAssignmentLogManager serviceAssignmentLogManager;
    
    
    public ServiceAssignmentLog getById(UUID serviceAssignmentLogId) throws EntityNotExistentException {
        return serviceAssignmentLogManager.getById(serviceAssignmentLogId);
    }
    
    public PagedResponse<ServiceAssignmentLog> getServiceAssignmentLog(ServiceAssignmentLog serviceAssignmentLog,   Paging paging) {
        return serviceAssignmentLogManager.getServiceAssignmentLog(serviceAssignmentLog, paging);
    }
    
    public List<ServiceAssignmentLog> findAll() {
        return serviceAssignmentLogManager.findAll();
    }
    
    public ServiceAssignmentLog createServiceAssignmentLog(ServiceAssignmentLog serviceAssignmentLog) throws BusinessLogicException, ExistentEntityException {
        return serviceAssignmentLogManager.createServiceAssignmentLog(serviceAssignmentLog);
    }
    
    public ServiceAssignmentLog updateServiceAssignmentLog(UUID serviceAssignmentLogId,ServiceAssignmentLog serviceAssignmentLog) throws BusinessLogicException, EntityNotExistentException {
        return serviceAssignmentLogManager.updateServiceAssignmentLog(serviceAssignmentLogId, serviceAssignmentLog);
    }
    
    public void deleteServiceAssignmentLog(UUID serviceAssignmentLogId) throws EntityNotExistentException {
        serviceAssignmentLogManager.deleteServiceAssignmentLog(serviceAssignmentLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createServiceAssignmentLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createServiceAssignmentLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


