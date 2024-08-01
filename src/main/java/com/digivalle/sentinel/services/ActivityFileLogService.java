package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.ActivityFileLogManager;
import com.digivalle.sentinel.models.ActivityFileLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ActivityFileLogService {
    private final static Logger logger = LoggerFactory.getLogger(ActivityFileLogService.class);

    @Autowired
    private ActivityFileLogManager activityFileLogManager;
    
    
    public ActivityFileLog getById(UUID activityFileLogId) throws EntityNotExistentException {
        return activityFileLogManager.getById(activityFileLogId);
    }
    
    public PagedResponse<ActivityFileLog> getActivityFileLog(ActivityFileLog activityFileLog,   Paging paging) {
        return activityFileLogManager.getActivityFileLog(activityFileLog, paging);
    }
    
    public List<ActivityFileLog> findAll() {
        return activityFileLogManager.findAll();
    }
    
    public ActivityFileLog createActivityFileLog(ActivityFileLog activityFileLog) throws BusinessLogicException, ExistentEntityException {
        return activityFileLogManager.createActivityFileLog(activityFileLog);
    }
    
    public ActivityFileLog updateActivityFileLog(UUID activityFileLogId,ActivityFileLog activityFileLog) throws BusinessLogicException, EntityNotExistentException {
        return activityFileLogManager.updateActivityFileLog(activityFileLogId, activityFileLog);
    }
    
    public void deleteActivityFileLog(UUID activityFileLogId) throws EntityNotExistentException {
        activityFileLogManager.deleteActivityFileLog(activityFileLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createActivityFileLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createActivityFileLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


