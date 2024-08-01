package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.EmployeeWorkExperienceLogManager;
import com.digivalle.sentinel.models.EmployeeWorkExperienceLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class EmployeeWorkExperienceLogService {
    private final static Logger logger = LoggerFactory.getLogger(EmployeeWorkExperienceLogService.class);

    @Autowired
    private EmployeeWorkExperienceLogManager employeeWorkExperienceLogManager;
    
    
    public EmployeeWorkExperienceLog getById(UUID employeeWorkExperienceLogId) throws EntityNotExistentException {
        return employeeWorkExperienceLogManager.getById(employeeWorkExperienceLogId);
    }
    
    public PagedResponse<EmployeeWorkExperienceLog> getEmployeeWorkExperienceLog(EmployeeWorkExperienceLog employeeWorkExperienceLog,   Paging paging) {
        return employeeWorkExperienceLogManager.getEmployeeWorkExperienceLog(employeeWorkExperienceLog, paging);
    }
    
    public List<EmployeeWorkExperienceLog> findAll() {
        return employeeWorkExperienceLogManager.findAll();
    }
    
    public EmployeeWorkExperienceLog createEmployeeWorkExperienceLog(EmployeeWorkExperienceLog employeeWorkExperienceLog) throws BusinessLogicException, ExistentEntityException {
        return employeeWorkExperienceLogManager.createEmployeeWorkExperienceLog(employeeWorkExperienceLog);
    }
    
    public EmployeeWorkExperienceLog updateEmployeeWorkExperienceLog(UUID employeeWorkExperienceLogId,EmployeeWorkExperienceLog employeeWorkExperienceLog) throws BusinessLogicException, EntityNotExistentException {
        return employeeWorkExperienceLogManager.updateEmployeeWorkExperienceLog(employeeWorkExperienceLogId, employeeWorkExperienceLog);
    }
    
    public void deleteEmployeeWorkExperienceLog(UUID employeeWorkExperienceLogId) throws EntityNotExistentException {
        employeeWorkExperienceLogManager.deleteEmployeeWorkExperienceLog(employeeWorkExperienceLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createEmployeeWorkExperienceLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createEmployeeWorkExperienceLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


