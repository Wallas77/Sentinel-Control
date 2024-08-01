package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.ProfileLogManager;
import com.digivalle.sentinel.models.ProfileLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ProfileLogService {
    private final static Logger logger = LoggerFactory.getLogger(ProfileLogService.class);

    @Autowired
    private ProfileLogManager profileLogManager;
    
    
    public ProfileLog getById(UUID profileLogId) throws EntityNotExistentException {
        return profileLogManager.getById(profileLogId);
    }
    
    public PagedResponse<ProfileLog> getProfileLog(ProfileLog profileLog,   Paging paging) {
        return profileLogManager.getProfileLog(profileLog, paging);
    }
    
    public List<ProfileLog> findAll() {
        return profileLogManager.findAll();
    }
    
    public ProfileLog createProfileLog(ProfileLog profileLog) throws BusinessLogicException, ExistentEntityException {
        return profileLogManager.createProfileLog(profileLog);
    }
    
    public ProfileLog updateProfileLog(UUID profileLogId,ProfileLog profileLog) throws BusinessLogicException, EntityNotExistentException {
        return profileLogManager.updateProfileLog(profileLogId, profileLog);
    }
    
    public void deleteProfileLog(UUID profileLogId) throws EntityNotExistentException {
        profileLogManager.deleteProfileLog(profileLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createProfileLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createProfileLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


