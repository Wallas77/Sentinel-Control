package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.EmployeeHealthCheckupLogManager;
import com.digivalle.sentinel.models.EmployeeHealthCheckupLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class EmployeeHealthCheckupLogService {
    private final static Logger logger = LoggerFactory.getLogger(EmployeeHealthCheckupLogService.class);

    @Autowired
    private EmployeeHealthCheckupLogManager employeeHealthCheckupLogManager;
    
    
    public EmployeeHealthCheckupLog getById(UUID employeeHealthCheckupLogId) throws EntityNotExistentException {
        return employeeHealthCheckupLogManager.getById(employeeHealthCheckupLogId);
    }
    
    public PagedResponse<EmployeeHealthCheckupLog> getEmployeeHealthCheckupLog(EmployeeHealthCheckupLog employeeHealthCheckupLog,   Paging paging) {
        return employeeHealthCheckupLogManager.getEmployeeHealthCheckupLog(employeeHealthCheckupLog, paging);
    }
    
    public List<EmployeeHealthCheckupLog> findAll() {
        return employeeHealthCheckupLogManager.findAll();
    }
    
    public EmployeeHealthCheckupLog createEmployeeHealthCheckupLog(EmployeeHealthCheckupLog employeeHealthCheckupLog) throws BusinessLogicException, ExistentEntityException {
        return employeeHealthCheckupLogManager.createEmployeeHealthCheckupLog(employeeHealthCheckupLog);
    }
    
    public EmployeeHealthCheckupLog updateEmployeeHealthCheckupLog(UUID employeeHealthCheckupLogId,EmployeeHealthCheckupLog employeeHealthCheckupLog) throws BusinessLogicException, EntityNotExistentException {
        return employeeHealthCheckupLogManager.updateEmployeeHealthCheckupLog(employeeHealthCheckupLogId, employeeHealthCheckupLog);
    }
    
    public void deleteEmployeeHealthCheckupLog(UUID employeeHealthCheckupLogId) throws EntityNotExistentException {
        employeeHealthCheckupLogManager.deleteEmployeeHealthCheckupLog(employeeHealthCheckupLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createEmployeeHealthCheckupLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createEmployeeHealthCheckupLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


