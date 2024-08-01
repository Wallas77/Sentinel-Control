package com.digivalle.sentinel.services;

import com.google.gson.Gson;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.ApplicationManager;
import com.digivalle.sentinel.managers.ProfileModuleGrantLogManager;
import com.digivalle.sentinel.managers.ProfileModuleGrantManager;
import com.digivalle.sentinel.models.Profile;
import com.digivalle.sentinel.models.ProfileModuleGrant;
import com.digivalle.sentinel.models.ProfileModuleGrantLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class ProfileModuleGrantService {
    private final static Logger logger = LoggerFactory.getLogger(ProfileModuleGrantService.class);

    @Autowired
    private ProfileModuleGrantManager profileModuleGrantManager;
    
    @Autowired
    private ProfileModuleGrantLogManager profileModuleGrantLogManager;
    
    @Autowired
    private ApplicationManager applicationManager;
    
    
    public ProfileModuleGrant getById(UUID profileModuleGrantId) throws EntityNotExistentException {
        return profileModuleGrantManager.getById(profileModuleGrantId);
    }
    
    public PagedResponse<ProfileModuleGrant> getProfileModuleGrant(ProfileModuleGrant profileModuleGrant,   Paging paging) {
        return profileModuleGrantManager.getProfileModuleGrant(profileModuleGrant, paging);
    }
    
    public List<ProfileModuleGrant> findAll() {
        return profileModuleGrantManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public ProfileModuleGrant createProfileModuleGrant(ProfileModuleGrant profileModuleGrant) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        ProfileModuleGrant profileModuleGrantPersisted = profileModuleGrantManager.createProfileModuleGrant(profileModuleGrant);
        //profileModuleGrantLogManager.createProfileModuleGrantLog(convertLog(profileModuleGrantPersisted,null,Definitions.LOG_CREATE));
        return getById(profileModuleGrantPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public ProfileModuleGrant updateProfileModuleGrant(UUID profileModuleGrantId,ProfileModuleGrant profileModuleGrant) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        ProfileModuleGrant profileModuleGrantPersisted = profileModuleGrantManager.updateProfileModuleGrant(profileModuleGrantId, profileModuleGrant);
        //profileModuleGrantLogManager.createProfileModuleGrantLog(convertLog(profileModuleGrantPersisted,null,Definitions.LOG_UPDATE));
        return getById(profileModuleGrantPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteProfileModuleGrant(UUID profileModuleGrantId) throws EntityNotExistentException, BusinessLogicException {
        
        //ProfileModuleGrant profileModuleGrantPersisted = getById(profileModuleGrantId);
        //profileModuleGrantLogManager.createProfileModuleGrantLog(convertLog(profileModuleGrantPersisted,null,Definitions.LOG_DELETE));
        profileModuleGrantManager.deleteProfileModuleGrant(profileModuleGrantId);
        
    }  
    
    public void deleteByProfile(Profile profile){
        profileModuleGrantManager.deleteByProfile(profile);
    }
    
    public Boolean initialize()  {
        try{
            createProfileModuleGrants();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createProfileModuleGrants() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        List<ProfileModuleGrant> profileModuleGrants = findAll();
        if(profileModuleGrants.isEmpty()){    
            
        } else {
            logger.info("Las ProfileModuleGrants ya existen, inicialización no ejecutada");
        }
    }
    
    public ProfileModuleGrantLog convertLog (ProfileModuleGrant profileModuleGrant, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(profileModuleGrant);
        ProfileModuleGrantLog profileModuleGrantLog = gson.fromJson(tmp,ProfileModuleGrantLog.class);
        profileModuleGrantLog.setId(null);
        profileModuleGrantLog.setUpdateDate(null);
        profileModuleGrantLog.setTransactionId(transactionId);
        profileModuleGrantLog.setProfileModuleGrantId(profileModuleGrant.getId());
        profileModuleGrantLog.setAction(action);
        return profileModuleGrantLog;
    }
    
    public List<ProfileModuleGrant> findByProfileAndModule_NameAndGrant_Name(Profile profile, String moduleName, String grantName){
        return profileModuleGrantManager.findByProfileAndModule_NameAndGrant_Name(profile, moduleName, grantName);
    }
    
    public List<ProfileModuleGrant> findByProfileAndModule_Application_Name(Profile profile, String applicationName){
        return profileModuleGrantManager.findByProfileAndModule_Application_Name(profile, applicationName);
    }
}


