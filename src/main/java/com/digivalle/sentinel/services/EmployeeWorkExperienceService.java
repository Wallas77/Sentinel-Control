package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.EmployeeWorkExperienceLogManager;
import com.digivalle.sentinel.managers.EmployeeWorkExperienceManager;
import com.digivalle.sentinel.models.EmployeeWorkExperience;
import com.digivalle.sentinel.models.EmployeeWorkExperienceLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class EmployeeWorkExperienceService {
    private final static Logger logger = LoggerFactory.getLogger(EmployeeWorkExperienceService.class);

    @Autowired
    private EmployeeWorkExperienceManager employeeWorkExperienceManager;
    
    @Autowired
    private EmployeeWorkExperienceLogManager employeeWorkExperienceLogManager;
    
    
    public EmployeeWorkExperience getById(UUID employeeWorkExperienceId) throws EntityNotExistentException {
        return employeeWorkExperienceManager.getById(employeeWorkExperienceId);
    }
    
    public PagedResponse<EmployeeWorkExperience> getEmployeeWorkExperience(EmployeeWorkExperience employeeWorkExperience,   Paging paging) {
        return employeeWorkExperienceManager.getEmployeeWorkExperience(employeeWorkExperience, paging);
    }
    
    public List<EmployeeWorkExperience> findAll() {
        return employeeWorkExperienceManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public EmployeeWorkExperience createEmployeeWorkExperience(EmployeeWorkExperience employeeWorkExperience) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        EmployeeWorkExperience employeeWorkExperiencePersisted = employeeWorkExperienceManager.createEmployeeWorkExperience(employeeWorkExperience);
        employeeWorkExperienceLogManager.createEmployeeWorkExperienceLog(convertLog(employeeWorkExperiencePersisted,null,Definitions.LOG_CREATE));
        return getById(employeeWorkExperiencePersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public EmployeeWorkExperience updateEmployeeWorkExperience(UUID employeeWorkExperienceId,EmployeeWorkExperience employeeWorkExperience) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        EmployeeWorkExperience employeeWorkExperiencePersisted = employeeWorkExperienceManager.updateEmployeeWorkExperience(employeeWorkExperienceId, employeeWorkExperience);
        employeeWorkExperienceLogManager.createEmployeeWorkExperienceLog(convertLog(employeeWorkExperiencePersisted,null,Definitions.LOG_UPDATE));
        return getById(employeeWorkExperiencePersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteEmployeeWorkExperience(UUID employeeWorkExperienceId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        EmployeeWorkExperience employeeWorkExperiencePersisted = employeeWorkExperienceManager.deleteEmployeeWorkExperience(employeeWorkExperienceId, updateUser);
        employeeWorkExperienceLogManager.createEmployeeWorkExperienceLog(convertLog(employeeWorkExperiencePersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createEmployeeWorkExperiences();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createEmployeeWorkExperiences() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        
    }
    
    public EmployeeWorkExperienceLog convertLog (EmployeeWorkExperience employeeWorkExperience, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(employeeWorkExperience);
        EmployeeWorkExperienceLog employeeWorkExperienceLog = gson.fromJson(tmp,EmployeeWorkExperienceLog.class);
        employeeWorkExperienceLog.setId(null);
        employeeWorkExperienceLog.setUpdateDate(null);
        employeeWorkExperienceLog.setTransactionId(transactionId);
        employeeWorkExperienceLog.setEmployeeWorkExperienceId(employeeWorkExperience.getId());
        employeeWorkExperienceLog.setAction(action);
        return employeeWorkExperienceLog;
    }
}


