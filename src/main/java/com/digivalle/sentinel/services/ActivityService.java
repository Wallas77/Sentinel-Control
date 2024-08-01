package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.ActivityLogManager;
import com.digivalle.sentinel.managers.ActivityManager;
import com.digivalle.sentinel.models.Activity;
import com.digivalle.sentinel.models.ActivityLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class ActivityService {
    private final static Logger logger = LoggerFactory.getLogger(ActivityService.class);

    @Autowired
    private ActivityManager activityManager;
    
    @Autowired
    private ActivityLogManager activityLogManager;
    
    
    public Activity getById(UUID activityId) throws EntityNotExistentException {
        return activityManager.getById(activityId);
    }
    
    public PagedResponse<Activity> getActivity(Activity activity,   Paging paging) {
        return activityManager.getActivity(activity, paging);
    }
    
    public List<Activity> findAll() {
        return activityManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Activity createActivity(Activity activity) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        Activity activityPersisted = activityManager.createActivity(activity);
        activityLogManager.createActivityLog(convertLog(activityPersisted,null,Definitions.LOG_CREATE));
        return getById(activityPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Activity updateActivity(UUID activityId,Activity activity) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        Activity activityPersisted = activityManager.updateActivity(activityId, activity);
        activityLogManager.createActivityLog(convertLog(activityPersisted,null,Definitions.LOG_UPDATE));
        return getById(activityPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteActivity(UUID activityId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        Activity activityPersisted = activityManager.deleteActivity(activityId, updateUser);
        activityLogManager.createActivityLog(convertLog(activityPersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createActivitys();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createActivitys() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        List<Activity> activityes = findAll();
        if(activityes.isEmpty()){
            Activity activity = new Activity();
            activity.setName(Definitions.APPLICATION_SENTINEL);
            activity.setDescription(Definitions.APPLICATION_SENTINEL_DESC);
            activity.setUpdateUser(Definitions.USER_DEFAULT);
            createActivity(activity);
            
                        logger.info("Las Activityes no existen, inicialización ejecutada");
        } else {
            logger.info("Las Activityes ya existen, inicialización no ejecutada");
        }
    }
    
    public ActivityLog convertLog (Activity activity, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(activity);
        ActivityLog activityLog = gson.fromJson(tmp,ActivityLog.class);
        activityLog.setId(null);
        activityLog.setUpdateDate(null);
        activityLog.setTransactionId(transactionId);
        activityLog.setActivityId(activity.getId());
        activityLog.setAction(action);
        return activityLog;
    }
}


