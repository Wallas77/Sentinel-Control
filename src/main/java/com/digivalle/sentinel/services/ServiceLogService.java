package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.ServiceLogManager;
import com.digivalle.sentinel.models.ServiceLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ServiceLogService {
    private final static Logger logger = LoggerFactory.getLogger(ServiceLogService.class);

    @Autowired
    private ServiceLogManager serviceLogManager;
    
    
    public ServiceLog getById(UUID serviceLogId) throws EntityNotExistentException {
        return serviceLogManager.getById(serviceLogId);
    }
    
    public PagedResponse<ServiceLog> getServiceLog(ServiceLog serviceLog,   Paging paging) {
        return serviceLogManager.getServiceLog(serviceLog, paging);
    }
    
    public List<ServiceLog> findAll() {
        return serviceLogManager.findAll();
    }
    
    public ServiceLog createServiceLog(ServiceLog serviceLog) throws BusinessLogicException, ExistentEntityException {
        return serviceLogManager.createServiceLog(serviceLog);
    }
    
    public ServiceLog updateServiceLog(UUID serviceLogId,ServiceLog serviceLog) throws BusinessLogicException, EntityNotExistentException {
        return serviceLogManager.updateServiceLog(serviceLogId, serviceLog);
    }
    
    public void deleteServiceLog(UUID serviceLogId) throws EntityNotExistentException {
        serviceLogManager.deleteServiceLog(serviceLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createServiceLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createServiceLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


