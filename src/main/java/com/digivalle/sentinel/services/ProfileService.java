package com.digivalle.sentinel.services;


import com.google.gson.Gson;
import com.digivalle.sentinel.models.Module;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.ApplicationManager;
import com.digivalle.sentinel.managers.GrantManager;
import com.digivalle.sentinel.managers.ModuleManager;
import com.digivalle.sentinel.managers.ProfileLogManager;
import com.digivalle.sentinel.managers.ProfileManager;
import com.digivalle.sentinel.models.Application;
import com.digivalle.sentinel.models.Grant;
import com.digivalle.sentinel.models.Profile;
import com.digivalle.sentinel.models.ProfileLog;
import com.digivalle.sentinel.models.ProfileModuleGrant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class ProfileService {
    private final static Logger logger = LoggerFactory.getLogger(ProfileService.class);

    @Autowired
    private ProfileManager profileManager;
    
    @Autowired
    private ProfileLogManager profileLogManager;
    
    @Autowired
    private ApplicationManager applicationManager;
    
    @Autowired
    private ProfileModuleGrantService profileModuleGrantService;
    
    @Autowired
    private ModuleManager moduleManager;
    
    @Autowired
    private GrantManager grantManager;
    
    public Profile getById(UUID profileId) throws EntityNotExistentException {
        return profileManager.getById(profileId);
    }
    
    public PagedResponse<Profile> getProfile(Profile profile,   Paging paging) {
        return profileManager.getProfile(profile, paging);
    }
    
    public List<Profile> findAll() {
        return profileManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Profile createProfile(Profile profile) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        
        Profile newProfile = new Profile();
        newProfile.setName(profile.getName());
        newProfile.setUpdateUser(profile.getUpdateUser());
        Profile profilePersisted = profileManager.createProfile(newProfile);
        for(ProfileModuleGrant profileModuleGrant: profile.getProfileModuleGrant()){
            //System.out.println("profilePersisted.getId=>"+profilePersisted.getId());
            newProfile = new Profile();
            newProfile.setId(profilePersisted.getId());
            profileModuleGrant.setProfile(newProfile);
            profileModuleGrant.setUpdateUser(profile.getUpdateUser());
            profileModuleGrantService.createProfileModuleGrant(profileModuleGrant);
        }
        profileLogManager.createProfileLog(convertLog(profilePersisted,null,Definitions.LOG_CREATE));
        return getById(profilePersisted.getId());
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Profile updateProfile(UUID profileId,Profile profile) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        Profile profilePersisted = profileManager.updateProfile(profileId, profile);
        /*for(ProfileModuleGrant profileModuleGrant: profilePersisted.getProfileModuleGrant()){
            //System.out.println("profileModuleGrant.getId()=>"+profileModuleGrant.getId());
            profileModuleGrantService.deleteProfileModuleGrant(profileModuleGrant.getId());
        }*/
        
        profileModuleGrantService.deleteByProfile(profilePersisted);
        for(ProfileModuleGrant profileModuleGrant: profile.getProfileModuleGrant()){
            profileModuleGrant.setProfile(profilePersisted);
            profileModuleGrant.setUpdateUser(profile.getUpdateUser());
            profileModuleGrantService.createProfileModuleGrant(profileModuleGrant);
        }
        //profileLogManager.createProfileLog(convertLog(profilePersisted,null,Definitions.LOG_UPDATE));
        return getById(profilePersisted.getId());
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteProfile(UUID profileId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        Profile profilePersisted = profileManager.deleteProfile(profileId);
        profilePersisted.setUpdateUser(updateUser);
        profileLogManager.createProfileLog(convertLog(profilePersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize()  {
        try{
            createProfiles();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    
    private void createProfiles() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        List<Profile> profiles = findAll();
        if(profiles.isEmpty()){    
            Paging paging = new Paging(0, 10000);
            Gson gson = new Gson();
            
            Profile profile = new Profile();
            profile.setName(Definitions.PROFILE_ADMINISTRATOR_SENTINEL);
            profile.setUpdateUser(Definitions.USER_DEFAULT);
            profile.setProfileModuleGrant(new ArrayList<>());
            
            Application application = new Application(Definitions.APPLICATION_SENTINEL, Definitions.APPLICATION_SENTINEL_DESC);
            Module moduleFilter = new Module();
            moduleFilter.setApplication(application);
            moduleFilter.setDeleted(Boolean.FALSE);
            moduleFilter.setActive(Boolean.TRUE);
            PagedResponse<Module> pagedModules = moduleManager.getModule(moduleFilter, paging);
            if(pagedModules.getTotal()>0){
                Grant grantFilter = new Grant();
                grantFilter.setDeleted(Boolean.FALSE);
                grantFilter.setActive(Boolean.TRUE);
                PagedResponse<Grant> pagedGrants = grantManager.getGrant(grantFilter, paging);
                if(pagedGrants.getTotal()>0){
                    List<Module> modules = pagedModules.getElements();
                    List<Grant> grants = pagedGrants.getElements();
                    for(Module module: modules){
                        for(Grant grant: grants){
                            ProfileModuleGrant profileModuleGrant = new ProfileModuleGrant();
                            //profileModuleGrant.setProfile(profile);
                            profileModuleGrant.setModule(gson.fromJson(gson.toJson(module),Module.class));
                            profileModuleGrant.setGrant(gson.fromJson(gson.toJson(grant),Grant.class));
                            profileModuleGrant.setUpdateUser(Definitions.USER_DEFAULT);
                            profile.getProfileModuleGrant().add(profileModuleGrant);
                        }
                    }
                }
                profile = createProfile(profile);
                
                profile = new Profile();
                profile.setName(Definitions.PROFILE_EMPLOYEE_SENTINEL);
                profile.setUpdateUser(Definitions.USER_DEFAULT);
                profile.setProfileModuleGrant(new ArrayList<>());

                profile = createProfile(profile);
                
                
                logger.info("Las Profiles no existen, inicialización ejecutada");
            } else {
                throw new BusinessLogicException("No se encontro la aplicación: "+Definitions.APPLICATION_SENTINEL);
            }
        } else {
            logger.info("Las Profiles ya existen, inicialización no ejecutada");
        }
    }
    
    public ProfileLog convertLog (Profile profile, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(profile);
        ProfileLog profileLog = gson.fromJson(tmp,ProfileLog.class);
        profileLog.setId(null);
        profileLog.setUpdateDate(null);
        profileLog.setUpdateUser(profile.getUpdateUser());
        profileLog.setTransactionId(transactionId);
        profileLog.setProfileId(profile.getId());
        profileLog.setAction(action);
        profileLog.setActiveObject(profile.getActive());
        return profileLog;
    }
}


