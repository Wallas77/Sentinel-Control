/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.services;

import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.models.ProfileModuleGrant;
import com.digivalle.sentinel.models.Token;
import com.digivalle.sentinel.models.User;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

/**
 *
 * @author Waldir.Valle
 */

@Component
public class SecurityService {
    
    private final static Logger logger = LoggerFactory.getLogger(SecurityService.class);
    
    @Autowired
    ApplicationService applicationService;
    
    @Autowired
    GrantService grantService;
    
    @Autowired
    ModuleService moduleService;
    
    @Autowired
    ProfileService profileService;
    
    @Autowired
    UserService userService;
    
    @Autowired
    TokenService tokenService;
    
    @Autowired
    TokenLogService tokenLogService;
    
    @Autowired
    ProfileModuleGrantService profileModuleGrantService;
    
    @Autowired
    CountryService countryService;
    
    
    
     public String initialize () throws EntityNotFoundException, ExistentEntityException{
        String response = "Application: "+applicationService.initialize()+"\n";
        response = "Grant: "+grantService.initialize()+"\n";
        response += "Module: "+moduleService.initialize()+"\n";
        response += "Profile: "+profileService.initialize()+"\n";
        response += "User: "+userService.initialize()+"\n";
        response += "Country: "+countryService.initialize()+"\n";
        
        
        return response;
    }
     
     public Token login(String name, String password, Integer sessionTime) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        final List<User> users = userService.findByEmailIgnoreCaseContainingAndDeletedAndActive(name, false, true);
        if(!users.isEmpty()){
            if(users.get(0)!=null && password!=null && checkPass(password,users.get(0).getPassword())){
                Token persistedToken = tokenService.getByUser(users.get(0));
                if(persistedToken==null){
                    tokenService.deleteActiveTokensByUser(users.get(0));
                    Token token = tokenService.createToken(users.get(0), sessionTime);
                    tokenLogService.createTokenLog(TokenService.convertLog(token, null, "CREATE"));
                    token.setUserIdentifier(users.get(0).getId());
                    token.setUser(users.get(0));
                    token.setUserProfile(users.get(0).getProfile().getName());
                    return token;
                } else {
                    persistedToken.setUserProfile(users.get(0).getProfile().getName());
                    return persistedToken;
                }
            }
        }
        logger.info("Intento invalido de logueo [%s/%s]", name, password);
        throw new BusinessLogicException("Intento invalido de logueo");
    }
     
     private boolean checkPass(String plainPassword, String hashedPassword) {
	if (BCrypt.checkpw(plainPassword, hashedPassword)){
            logger.debug("El password coincide.");
            return true;
        }else{
            logger.debug("El password no coincide.");
            return false;
        }
    }
     
     public boolean getGrantAndModule(String token, String moduleName, String grantName) throws EntityNotExistentException {
        Token tokenPersisted = tokenService.getByToken(token);
        if(tokenPersisted==null){
            throw new EntityNotExistentException(Token.class,token);
        }
        User userPersisted = userService.getById(tokenPersisted.getUser().getId());
        List<ProfileModuleGrant> profileModuleGrants = profileModuleGrantService.findByProfileAndModule_NameAndGrant_Name(userPersisted.getProfile(), moduleName, grantName);
        if(profileModuleGrants==null || profileModuleGrants.isEmpty()){
            return false;
        } else {
            for(ProfileModuleGrant profileModuleGrant: profileModuleGrants){
                //System.out.println("Profile=>"+profileModuleGrant.getProfile().getId()+" Module=>"+profileModuleGrant.getModule().getId()+" Grant=>"+profileModuleGrant.getGrant().getId());
            }
        }
        return true;
    }
     
     public User getUserByToken(String token) throws EntityNotExistentException {
        Token tokenPersisted = tokenService.getByToken(token);
        if(tokenPersisted==null){
            throw new EntityNotExistentException(Token.class,token);
        }
        //User userPersisted = userService.getById(tokenPersisted.getUser().getId());
        //tokenPersisted.getUser().setPassword(null);
        User user = new User();
        user.setActive(tokenPersisted.getUser().getActive());
        user.setCreationDate(tokenPersisted.getUser().getCreationDate());
        user.setDeleted(tokenPersisted.getUser().getDeleted());
        user.setEmail(tokenPersisted.getUser().getEmail());
        user.setId(tokenPersisted.getUser().getId());
        user.setName(tokenPersisted.getUser().getName());
        user.setProfile(tokenPersisted.getUser().getProfile());
        user.setSerial(tokenPersisted.getUser().getSerial());
        user.setUpdateDate(tokenPersisted.getUser().getUpdateDate());
        user.setUpdateUser(tokenPersisted.getUser().getUpdateUser());
        return user;
    }
     
    public  List<ProfileModuleGrant> getGrantsByApplication(String token, String applicationName) throws EntityNotExistentException {
        Token tokenPersisted = tokenService.getByToken(token);
        if(tokenPersisted==null){
            throw new EntityNotExistentException(Token.class,token);
        }
        return profileModuleGrantService.findByProfileAndModule_Application_Name(tokenPersisted.getUser().getProfile(), applicationName);
        //return tokenPersisted.getUser().getProfile().getProfileModuleGrant();
    }
     
}
