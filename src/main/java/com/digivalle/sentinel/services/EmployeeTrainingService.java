package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.EmployeeTrainingLogManager;
import com.digivalle.sentinel.managers.EmployeeTrainingManager;
import com.digivalle.sentinel.models.EmployeeTraining;
import com.digivalle.sentinel.models.EmployeeTrainingLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class EmployeeTrainingService {
    private final static Logger logger = LoggerFactory.getLogger(EmployeeTrainingService.class);

    @Autowired
    private EmployeeTrainingManager employeeTrainingManager;
    
    @Autowired
    private EmployeeTrainingLogManager employeeTrainingLogManager;
    
    
    public EmployeeTraining getById(UUID employeeTrainingId) throws EntityNotExistentException {
        return employeeTrainingManager.getById(employeeTrainingId);
    }
    
    public PagedResponse<EmployeeTraining> getEmployeeTraining(EmployeeTraining employeeTraining,   Paging paging) {
        return employeeTrainingManager.getEmployeeTraining(employeeTraining, paging);
    }
    
    public List<EmployeeTraining> findAll() {
        return employeeTrainingManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public EmployeeTraining createEmployeeTraining(EmployeeTraining employeeTraining) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        EmployeeTraining employeeTrainingPersisted = employeeTrainingManager.createEmployeeTraining(employeeTraining);
        employeeTrainingLogManager.createEmployeeTrainingLog(convertLog(employeeTrainingPersisted,null,Definitions.LOG_CREATE));
        return getById(employeeTrainingPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public EmployeeTraining updateEmployeeTraining(UUID employeeTrainingId,EmployeeTraining employeeTraining) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        EmployeeTraining employeeTrainingPersisted = employeeTrainingManager.updateEmployeeTraining(employeeTrainingId, employeeTraining);
        employeeTrainingLogManager.createEmployeeTrainingLog(convertLog(employeeTrainingPersisted,null,Definitions.LOG_UPDATE));
        return getById(employeeTrainingPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteEmployeeTraining(UUID employeeTrainingId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        EmployeeTraining employeeTrainingPersisted = employeeTrainingManager.deleteEmployeeTraining(employeeTrainingId, updateUser);
        employeeTrainingLogManager.createEmployeeTrainingLog(convertLog(employeeTrainingPersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createEmployeeTrainings();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createEmployeeTrainings() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        List<EmployeeTraining> employeeTraininges = findAll();
        if(employeeTraininges.isEmpty()){
            EmployeeTraining employeeTraining = new EmployeeTraining();
            employeeTraining.setName(Definitions.APPLICATION_SENTINEL);
            employeeTraining.setDescription(Definitions.APPLICATION_SENTINEL_DESC);
            employeeTraining.setUpdateUser(Definitions.USER_DEFAULT);
            createEmployeeTraining(employeeTraining);
            
                        logger.info("Las EmployeeTraininges no existen, inicialización ejecutada");
        } else {
            logger.info("Las EmployeeTraininges ya existen, inicialización no ejecutada");
        }
    }
    
    public EmployeeTrainingLog convertLog (EmployeeTraining employeeTraining, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(employeeTraining);
        EmployeeTrainingLog employeeTrainingLog = gson.fromJson(tmp,EmployeeTrainingLog.class);
        employeeTrainingLog.setId(null);
        employeeTrainingLog.setUpdateDate(null);
        employeeTrainingLog.setTransactionId(transactionId);
        employeeTrainingLog.setEmployeeTrainingId(employeeTraining.getId());
        employeeTrainingLog.setAction(action);
        employeeTrainingLog.setActiveObject(employeeTraining.getActive());
        return employeeTrainingLog;
    }
}


