package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.ApplicationLogManager;
import com.digivalle.sentinel.managers.ApplicationManager;
import com.digivalle.sentinel.models.Application;
import com.digivalle.sentinel.models.ApplicationLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class ApplicationService {
    private final static Logger logger = LoggerFactory.getLogger(ApplicationService.class);

    @Autowired
    private ApplicationManager applicationManager;
    
    @Autowired
    private ApplicationLogManager applicationLogManager;
    
    
    public Application getById(UUID applicationId) throws EntityNotExistentException {
        return applicationManager.getById(applicationId);
    }
    
    public PagedResponse<Application> getApplication(Application application,   Paging paging) {
        return applicationManager.getApplication(application, paging);
    }
    
    public List<Application> findAll() {
        return applicationManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Application createApplication(Application application) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        Application applicationPersisted = applicationManager.createApplication(application);
        applicationLogManager.createApplicationLog(convertLog(applicationPersisted,null,Definitions.LOG_CREATE));
        return getById(applicationPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Application updateApplication(UUID applicationId,Application application) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        Application applicationPersisted = applicationManager.updateApplication(applicationId, application);
        applicationLogManager.createApplicationLog(convertLog(applicationPersisted,null,Definitions.LOG_UPDATE));
        return getById(applicationPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteApplication(UUID applicationId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        Application applicationPersisted = applicationManager.deleteApplication(applicationId, updateUser);
        applicationLogManager.createApplicationLog(convertLog(applicationPersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createApplications();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createApplications() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        List<Application> applicationes = findAll();
        if(applicationes.isEmpty()){
            Application application = new Application();
            application.setName(Definitions.APPLICATION_SENTINEL);
            application.setDescription(Definitions.APPLICATION_SENTINEL_DESC);
            application.setUpdateUser(Definitions.USER_DEFAULT);
            createApplication(application);
            
                        logger.info("Las Applicationes no existen, inicialización ejecutada");
        } else {
            logger.info("Las Applicationes ya existen, inicialización no ejecutada");
        }
    }
    
    public ApplicationLog convertLog (Application application, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(application);
        ApplicationLog applicationLog = gson.fromJson(tmp,ApplicationLog.class);
        applicationLog.setId(null);
        applicationLog.setUpdateDate(null);
        applicationLog.setTransactionId(transactionId);
        applicationLog.setApplicationId(application.getId());
        applicationLog.setAction(action);
        return applicationLog;
    }
}


