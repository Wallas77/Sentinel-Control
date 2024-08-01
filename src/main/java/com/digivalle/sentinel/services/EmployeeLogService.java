package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.EmployeeLogManager;
import com.digivalle.sentinel.models.EmployeeLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class EmployeeLogService {
    private final static Logger logger = LoggerFactory.getLogger(EmployeeLogService.class);

    @Autowired
    private EmployeeLogManager employeeLogManager;
    
    
    public EmployeeLog getById(UUID employeeLogId) throws EntityNotExistentException {
        return employeeLogManager.getById(employeeLogId);
    }
    
    public PagedResponse<EmployeeLog> getEmployeeLog(EmployeeLog employeeLog,   Paging paging) {
        return employeeLogManager.getEmployeeLog(employeeLog, paging);
    }
    
    public List<EmployeeLog> findAll() {
        return employeeLogManager.findAll();
    }
    
    public EmployeeLog createEmployeeLog(EmployeeLog employeeLog) throws BusinessLogicException, ExistentEntityException {
        return employeeLogManager.createEmployeeLog(employeeLog);
    }
    
    public EmployeeLog updateEmployeeLog(UUID employeeLogId,EmployeeLog employeeLog) throws BusinessLogicException, EntityNotExistentException {
        return employeeLogManager.updateEmployeeLog(employeeLogId, employeeLog);
    }
    
    public void deleteEmployeeLog(UUID employeeLogId) throws EntityNotExistentException {
        employeeLogManager.deleteEmployeeLog(employeeLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createEmployeeLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createEmployeeLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


