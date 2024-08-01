package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.EmployeeTrainingLogManager;
import com.digivalle.sentinel.models.EmployeeTrainingLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class EmployeeTrainingLogService {
    private final static Logger logger = LoggerFactory.getLogger(EmployeeTrainingLogService.class);

    @Autowired
    private EmployeeTrainingLogManager employeeTrainingLogManager;
    
    
    public EmployeeTrainingLog getById(UUID employeeTrainingLogId) throws EntityNotExistentException {
        return employeeTrainingLogManager.getById(employeeTrainingLogId);
    }
    
    public PagedResponse<EmployeeTrainingLog> getEmployeeTrainingLog(EmployeeTrainingLog employeeTrainingLog,   Paging paging) {
        return employeeTrainingLogManager.getEmployeeTrainingLog(employeeTrainingLog, paging);
    }
    
    public List<EmployeeTrainingLog> findAll() {
        return employeeTrainingLogManager.findAll();
    }
    
    public EmployeeTrainingLog createEmployeeTrainingLog(EmployeeTrainingLog employeeTrainingLog) throws BusinessLogicException, ExistentEntityException {
        return employeeTrainingLogManager.createEmployeeTrainingLog(employeeTrainingLog);
    }
    
    public EmployeeTrainingLog updateEmployeeTrainingLog(UUID employeeTrainingLogId,EmployeeTrainingLog employeeTrainingLog) throws BusinessLogicException, EntityNotExistentException {
        return employeeTrainingLogManager.updateEmployeeTrainingLog(employeeTrainingLogId, employeeTrainingLog);
    }
    
    public void deleteEmployeeTrainingLog(UUID employeeTrainingLogId) throws EntityNotExistentException {
        employeeTrainingLogManager.deleteEmployeeTrainingLog(employeeTrainingLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createEmployeeTrainingLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createEmployeeTrainingLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


