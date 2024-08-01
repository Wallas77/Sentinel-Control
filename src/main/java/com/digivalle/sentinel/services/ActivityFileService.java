package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.ActivityFileLogManager;
import com.digivalle.sentinel.managers.ActivityFileManager;
import com.digivalle.sentinel.models.ActivityFile;
import com.digivalle.sentinel.models.ActivityFileLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class ActivityFileService {
    private final static Logger logger = LoggerFactory.getLogger(ActivityFileService.class);

    @Autowired
    private ActivityFileManager activityFileManager;
    
    @Autowired
    private ActivityFileLogManager activityFileLogManager;
    
    
    public ActivityFile getById(UUID activityFileId) throws EntityNotExistentException {
        return activityFileManager.getById(activityFileId);
    }
    
    public PagedResponse<ActivityFile> getActivityFile(ActivityFile activityFile,   Paging paging) {
        return activityFileManager.getActivityFile(activityFile, paging);
    }
    
    public List<ActivityFile> findAll() {
        return activityFileManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public ActivityFile createActivityFile(ActivityFile activityFile) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        ActivityFile activityFilePersisted = activityFileManager.createActivityFile(activityFile);
        activityFileLogManager.createActivityFileLog(convertLog(activityFilePersisted,null,Definitions.LOG_CREATE));
        return getById(activityFilePersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public ActivityFile updateActivityFile(UUID activityFileId,ActivityFile activityFile) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        ActivityFile activityFilePersisted = activityFileManager.updateActivityFile(activityFileId, activityFile);
        activityFileLogManager.createActivityFileLog(convertLog(activityFilePersisted,null,Definitions.LOG_UPDATE));
        return getById(activityFilePersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteActivityFile(UUID activityFileId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        ActivityFile activityFilePersisted = activityFileManager.deleteActivityFile(activityFileId, updateUser);
        activityFileLogManager.createActivityFileLog(convertLog(activityFilePersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createActivityFiles();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createActivityFiles() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
       
    }
    
    public ActivityFileLog convertLog (ActivityFile activityFile, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(activityFile);
        ActivityFileLog activityFileLog = gson.fromJson(tmp,ActivityFileLog.class);
        activityFileLog.setId(null);
        activityFileLog.setUpdateDate(null);
        activityFileLog.setTransactionId(transactionId);
        activityFileLog.setActivityFileId(activityFile.getId());
        activityFileLog.setAction(action);
        return activityFileLog;
    }
}


