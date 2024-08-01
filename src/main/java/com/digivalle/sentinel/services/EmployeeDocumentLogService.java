package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.EmployeeDocumentLogManager;
import com.digivalle.sentinel.models.EmployeeDocumentLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class EmployeeDocumentLogService {
    private final static Logger logger = LoggerFactory.getLogger(EmployeeDocumentLogService.class);

    @Autowired
    private EmployeeDocumentLogManager employeeDocumentLogManager;
    
    
    public EmployeeDocumentLog getById(UUID employeeDocumentLogId) throws EntityNotExistentException {
        return employeeDocumentLogManager.getById(employeeDocumentLogId);
    }
    
    public PagedResponse<EmployeeDocumentLog> getEmployeeDocumentLog(EmployeeDocumentLog employeeDocumentLog,   Paging paging) {
        return employeeDocumentLogManager.getEmployeeDocumentLog(employeeDocumentLog, paging);
    }
    
    public List<EmployeeDocumentLog> findAll() {
        return employeeDocumentLogManager.findAll();
    }
    
    public EmployeeDocumentLog createEmployeeDocumentLog(EmployeeDocumentLog employeeDocumentLog) throws BusinessLogicException, ExistentEntityException {
        return employeeDocumentLogManager.createEmployeeDocumentLog(employeeDocumentLog);
    }
    
    public EmployeeDocumentLog updateEmployeeDocumentLog(UUID employeeDocumentLogId,EmployeeDocumentLog employeeDocumentLog) throws BusinessLogicException, EntityNotExistentException {
        return employeeDocumentLogManager.updateEmployeeDocumentLog(employeeDocumentLogId, employeeDocumentLog);
    }
    
    public void deleteEmployeeDocumentLog(UUID employeeDocumentLogId) throws EntityNotExistentException {
        employeeDocumentLogManager.deleteEmployeeDocumentLog(employeeDocumentLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createEmployeeDocumentLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createEmployeeDocumentLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


