package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.ProfileModuleGrantLogManager;
import com.digivalle.sentinel.models.ProfileModuleGrantLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ProfileModuleGrantLogService {
    private final static Logger logger = LoggerFactory.getLogger(ProfileModuleGrantLogService.class);

    @Autowired
    private ProfileModuleGrantLogManager profileLogManager;
    
    
    public ProfileModuleGrantLog getById(UUID profileLogId) throws EntityNotExistentException {
        return profileLogManager.getById(profileLogId);
    }
    
    public PagedResponse<ProfileModuleGrantLog> getProfileModuleGrantLog(ProfileModuleGrantLog profileLog,   Paging paging) {
        return profileLogManager.getProfileModuleGrantLog(profileLog, paging);
    }
    
    public List<ProfileModuleGrantLog> findAll() {
        return profileLogManager.findAll();
    }
    
    public ProfileModuleGrantLog createProfileModuleGrantLog(ProfileModuleGrantLog profileLog) throws BusinessLogicException, ExistentEntityException {
        return profileLogManager.createProfileModuleGrantLog(profileLog);
    }
    
    public ProfileModuleGrantLog updateProfileModuleGrantLog(UUID profileLogId,ProfileModuleGrantLog profileLog) throws BusinessLogicException, EntityNotExistentException {
        return profileLogManager.updateProfileModuleGrantLog(profileLogId, profileLog);
    }
    
    public void deleteProfileModuleGrantLog(UUID profileLogId) throws EntityNotExistentException {
        profileLogManager.deleteProfileModuleGrantLog(profileLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createProfileModuleGrantLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createProfileModuleGrantLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


