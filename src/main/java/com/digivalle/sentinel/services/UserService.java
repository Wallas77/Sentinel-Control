package com.digivalle.sentinel.services;

import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.ProfileManager;
import com.digivalle.sentinel.managers.UserLogManager;
import com.digivalle.sentinel.managers.UserManager;
import com.digivalle.sentinel.models.Profile;
import com.digivalle.sentinel.models.User;
import com.digivalle.sentinel.models.UserLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class UserService {
    private final static Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserManager userManager;
    
    @Autowired
    private UserLogManager userLogManager;
    
    @Autowired
    private ProfileManager profileManager;
    
    public User getById(UUID userId) throws EntityNotExistentException {
        return userManager.getById(userId);
    }
    
    public PagedResponse<User> getUser(User user,   Paging paging) {
        return userManager.getUser(user, paging);
    }
    
    public List<User> findAll() {
        return userManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public User createUser(User user) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        
        User userPersisted = userManager.createUser(user);
        userLogManager.createUserLog(convertLog(userPersisted,null,Definitions.LOG_CREATE));
        return getById(userPersisted.getId());
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public User updateUser(UUID userId,User user) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        User userPersisted = userManager.updateUser(userId, user);
        userLogManager.createUserLog(convertLog(userPersisted,null,Definitions.LOG_UPDATE));
        return getById(userPersisted.getId());
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteUser(UUID userId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        User userPersisted = userManager.deleteUser(userId);
        userPersisted.setUpdateUser(updateUser);
        userLogManager.createUserLog(convertLog(userPersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize()  {
        try{
            createUsers();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    public List<User> findByEmailIgnoreCaseContainingAndDeletedAndActive(String name,Boolean deleted, Boolean active){
        return userManager.findByEmailIgnoreCaseContainingAndDeletedAndActive(name,deleted,active);
    }
    
    private void createUsers() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        List<User> users = findAll();
        if(users.isEmpty()){    
            Paging paging = new Paging(0, 10000);
            Gson gson = new Gson();
            User user = new User();
            user.setName(Definitions.USER_ADMINISTRATOR_SENTINEL_NAME);
            user.setEmail(Definitions.USER_ADMINISTRATOR_SENTINEL_EMAIL);
            user.setPassword(Definitions.USER_ADMINISTRATOR_SENTINEL_PASSWORD);
            user.setUpdateUser(Definitions.USER_DEFAULT);
            Profile profileFilter = new Profile();
            profileFilter.setName(Definitions.PROFILE_ADMINISTRATOR_SENTINEL);
            profileFilter.setDeleted(Boolean.FALSE);
            profileFilter.setActive(Boolean.TRUE);
            PagedResponse<Profile> pagedProfiles = profileManager.getProfile(profileFilter, paging);
            UUID adminId =null; 
            if(pagedProfiles.getTotal()>0){
                System.out.println("pagedProfiles.getElements().get(0)=>"+pagedProfiles.getElements().get(0).getId());
                adminId=pagedProfiles.getElements().get(0).getId();
                Profile profile = new Profile();
                profile.setId(pagedProfiles.getElements().get(0).getId());
                user.setProfile(profile);
                user = createUser(user);
                
                logger.info("Los Users no existen, inicialización ejecutada");
            } else {
                throw new BusinessLogicException("No se encontro la aplicación: "+Definitions.APPLICATION_SENTINEL);
            }
        } else {
            logger.info("Los Users ya existen, inicialización no ejecutada");
        }
    }
    
    public UserLog convertLog (User user, UUID transactionId, String action){
        Gson gson= new Gson();
        if(user.getProfile()!=null){
            Profile profile = new Profile();
            profile.setId(user.getProfile().getId());
            user.setProfile(profile);
        }
        String tmp = gson.toJson(user);
        
        UserLog userLog = gson.fromJson(tmp,UserLog.class);
        userLog.setId(null);
        userLog.setUpdateDate(null);
        userLog.setUpdateUser(user.getUpdateUser());
        userLog.setTransactionId(transactionId);
        userLog.setUserId(user.getId());
        userLog.setAction(action);
        return userLog;
    }
}


