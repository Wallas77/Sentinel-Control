package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.EmployeeHealthCheckupLogManager;
import com.digivalle.sentinel.managers.EmployeeHealthCheckupManager;
import com.digivalle.sentinel.models.EmployeeHealthCheckup;
import com.digivalle.sentinel.models.EmployeeHealthCheckupLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class EmployeeHealthCheckupService {
    private final static Logger logger = LoggerFactory.getLogger(EmployeeHealthCheckupService.class);

    @Autowired
    private EmployeeHealthCheckupManager employeeTrainingManager;
    
    @Autowired
    private EmployeeHealthCheckupLogManager employeeTrainingLogManager;
    
    
    public EmployeeHealthCheckup getById(UUID employeeTrainingId) throws EntityNotExistentException {
        return employeeTrainingManager.getById(employeeTrainingId);
    }
    
    public PagedResponse<EmployeeHealthCheckup> getEmployeeHealthCheckup(EmployeeHealthCheckup employeeTraining,   Paging paging) {
        return employeeTrainingManager.getEmployeeHealthCheckup(employeeTraining, paging);
    }
    
    public List<EmployeeHealthCheckup> findAll() {
        return employeeTrainingManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public EmployeeHealthCheckup createEmployeeHealthCheckup(EmployeeHealthCheckup employeeTraining) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        EmployeeHealthCheckup employeeTrainingPersisted = employeeTrainingManager.createEmployeeHealthCheckup(employeeTraining);
        employeeTrainingLogManager.createEmployeeHealthCheckupLog(convertLog(employeeTrainingPersisted,null,Definitions.LOG_CREATE));
        return getById(employeeTrainingPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public EmployeeHealthCheckup updateEmployeeHealthCheckup(UUID employeeTrainingId,EmployeeHealthCheckup employeeTraining) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        EmployeeHealthCheckup employeeTrainingPersisted = employeeTrainingManager.updateEmployeeHealthCheckup(employeeTrainingId, employeeTraining);
        employeeTrainingLogManager.createEmployeeHealthCheckupLog(convertLog(employeeTrainingPersisted,null,Definitions.LOG_UPDATE));
        return getById(employeeTrainingPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteEmployeeHealthCheckup(UUID employeeTrainingId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        EmployeeHealthCheckup employeeTrainingPersisted = employeeTrainingManager.deleteEmployeeHealthCheckup(employeeTrainingId, updateUser);
        employeeTrainingLogManager.createEmployeeHealthCheckupLog(convertLog(employeeTrainingPersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createEmployeeHealthCheckups();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createEmployeeHealthCheckups() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        
    }
    
    public EmployeeHealthCheckupLog convertLog (EmployeeHealthCheckup employeeTraining, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(employeeTraining);
        EmployeeHealthCheckupLog employeeTrainingLog = gson.fromJson(tmp,EmployeeHealthCheckupLog.class);
        employeeTrainingLog.setId(null);
        employeeTrainingLog.setUpdateDate(null);
        employeeTrainingLog.setTransactionId(transactionId);
        employeeTrainingLog.setEmployeeHealthCheckupId(employeeTraining.getId());
        employeeTrainingLog.setAction(action);
        return employeeTrainingLog;
    }
}


