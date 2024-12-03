package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.ServiceAttendanceLogManager;
import com.digivalle.sentinel.models.ServiceAttendanceLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ServiceAttendanceLogService {
    private final static Logger logger = LoggerFactory.getLogger(ServiceAttendanceLogService.class);

    @Autowired
    private ServiceAttendanceLogManager serviceAttendanceLogManager;
    
    
    public ServiceAttendanceLog getById(UUID serviceAttendanceLogId) throws EntityNotExistentException {
        return serviceAttendanceLogManager.getById(serviceAttendanceLogId);
    }
    
    public PagedResponse<ServiceAttendanceLog> getServiceAttendanceLog(ServiceAttendanceLog serviceAttendanceLog,   Paging paging) {
        return serviceAttendanceLogManager.getServiceAttendanceLog(serviceAttendanceLog, paging);
    }
    
    public List<ServiceAttendanceLog> findAll() {
        return serviceAttendanceLogManager.findAll();
    }
    
    public ServiceAttendanceLog createServiceAttendanceLog(ServiceAttendanceLog serviceAttendanceLog) throws BusinessLogicException, ExistentEntityException {
        return serviceAttendanceLogManager.createServiceAttendanceLog(serviceAttendanceLog);
    }
    
    public ServiceAttendanceLog updateServiceAttendanceLog(UUID serviceAttendanceLogId,ServiceAttendanceLog serviceAttendanceLog) throws BusinessLogicException, EntityNotExistentException {
        return serviceAttendanceLogManager.updateServiceAttendanceLog(serviceAttendanceLogId, serviceAttendanceLog);
    }
    
    public void deleteServiceAttendanceLog(UUID serviceAttendanceLogId) throws EntityNotExistentException {
        serviceAttendanceLogManager.deleteServiceAttendanceLog(serviceAttendanceLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createServiceAttendanceLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createServiceAttendanceLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


