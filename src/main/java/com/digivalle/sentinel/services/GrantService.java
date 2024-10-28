package com.digivalle.sentinel.services;


import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.GrantLogManager;
import com.digivalle.sentinel.managers.GrantManager;
import com.digivalle.sentinel.models.Grant;
import com.digivalle.sentinel.models.GrantLog;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class GrantService {
    private final static Logger logger = LoggerFactory.getLogger(GrantService.class);

    @Autowired
    private GrantManager grantManager;
    
    @Autowired
    private GrantLogManager grantLogManager;
    
    
    public Grant getById(UUID grantId) throws EntityNotExistentException {
        return grantManager.getById(grantId);
    }
    
    public PagedResponse<Grant> getGrant(Grant grant,   Paging paging) {
        return grantManager.getGrant(grant, paging);
    }
    
    public List<Grant> findAll() {
        return grantManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Grant createGrant(Grant grant) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        Grant grantPersisted = grantManager.createGrant(grant);
        grantLogManager.createGrantLog(convertLog(grantPersisted,null,Definitions.LOG_CREATE));
        return getById(grantPersisted.getId());
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Grant updateGrant(UUID grantId,Grant grant) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        Grant grantPersisted = grantManager.updateGrant(grantId, grant);
        grantLogManager.createGrantLog(convertLog(grantPersisted,null,Definitions.LOG_UPDATE));
        return getById(grantPersisted.getId());
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteGrant(UUID grantId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        Grant grantPersisted = grantManager.deleteGrant(grantId);
        grantPersisted.setUpdateUser(updateUser);
        grantLogManager.createGrantLog(convertLog(grantPersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createGrants();
        } catch (BusinessLogicException | ExistentEntityException | EntityNotFoundException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createGrants() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        List<Grant> grantes = findAll();
        if(grantes.isEmpty()){
            Grant grant = new Grant();
            grant.setName(Definitions.GRANT_ACCESS);
            grant.setUpdateUser(Definitions.USER_DEFAULT);
            createGrant(grant);
            grant = new Grant();
            grant.setName(Definitions.GRANT_CREATE);
            grant.setUpdateUser(Definitions.USER_DEFAULT);
            createGrant(grant);
            grant = new Grant();
            grant.setName(Definitions.GRANT_UPDATE);
            grant.setUpdateUser(Definitions.USER_DEFAULT);
            createGrant(grant);
            grant = new Grant();
            grant.setName(Definitions.GRANT_DELETE);
            grant.setUpdateUser(Definitions.USER_DEFAULT);
            createGrant(grant);
            logger.info("Las Grants no existen, inicialización ejecutada");
        } else {
            logger.info("Las Grants ya existen, inicialización no ejecutada");
        }
    }
    
    public GrantLog convertLog (Grant grant, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(grant);
        GrantLog grantLog = gson.fromJson(tmp,GrantLog.class);
        grantLog.setId(null);
        grantLog.setUpdateDate(null);
        grantLog.setUpdateUser(grant.getUpdateUser());
        grantLog.setTransactionId(transactionId);
        grantLog.setGrantId(grant.getId());
        grantLog.setAction(action);
        grantLog.setActiveObject(grant.getActive());
        return grantLog;
    }
}


