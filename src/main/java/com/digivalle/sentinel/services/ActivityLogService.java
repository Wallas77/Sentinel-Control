package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.ActivityLogManager;
import com.digivalle.sentinel.models.ActivityLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ActivityLogService {
    private final static Logger logger = LoggerFactory.getLogger(ActivityLogService.class);

    @Autowired
    private ActivityLogManager activityLogManager;
    
    
    public ActivityLog getById(UUID activityLogId) throws EntityNotExistentException {
        return activityLogManager.getById(activityLogId);
    }
    
    public PagedResponse<ActivityLog> getActivityLog(ActivityLog activityLog,   Paging paging) {
        return activityLogManager.getActivityLog(activityLog, paging);
    }
    
    public List<ActivityLog> findAll() {
        return activityLogManager.findAll();
    }
    
    public ActivityLog createActivityLog(ActivityLog activityLog) throws BusinessLogicException, ExistentEntityException {
        return activityLogManager.createActivityLog(activityLog);
    }
    
    public ActivityLog updateActivityLog(UUID activityLogId,ActivityLog activityLog) throws BusinessLogicException, EntityNotExistentException {
        return activityLogManager.updateActivityLog(activityLogId, activityLog);
    }
    
    public void deleteActivityLog(UUID activityLogId) throws EntityNotExistentException {
        activityLogManager.deleteActivityLog(activityLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createActivityLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createActivityLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


