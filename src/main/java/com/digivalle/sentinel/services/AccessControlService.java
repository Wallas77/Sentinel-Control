package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.AccessControlLogManager;
import com.digivalle.sentinel.managers.AccessControlManager;
import com.digivalle.sentinel.models.AccessControl;
import com.digivalle.sentinel.models.AccessControlLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class AccessControlService {
    private final static Logger logger = LoggerFactory.getLogger(AccessControlService.class);

    @Autowired
    private AccessControlManager accessControlManager;
    
    @Autowired
    private AccessControlLogManager accessControlLogManager;
    
    
    public AccessControl getById(UUID accessControlId) throws EntityNotExistentException {
        return accessControlManager.getById(accessControlId);
    }
    
    public PagedResponse<AccessControl> getAccessControl(AccessControl accessControl,   Paging paging) {
        return accessControlManager.getAccessControl(accessControl, paging);
    }
    
    public List<AccessControl> findAll() {
        return accessControlManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public AccessControl createAccessControl(AccessControl accessControl) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        AccessControl accessControlPersisted = accessControlManager.createAccessControl(accessControl);
        accessControlLogManager.createAccessControlLog(convertLog(accessControlPersisted,null,Definitions.LOG_CREATE));
        return getById(accessControlPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public AccessControl updateAccessControl(UUID accessControlId,AccessControl accessControl) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        AccessControl accessControlPersisted = accessControlManager.updateAccessControl(accessControlId, accessControl);
        accessControlLogManager.createAccessControlLog(convertLog(accessControlPersisted,null,Definitions.LOG_UPDATE));
        return getById(accessControlPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteAccessControl(UUID accessControlId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        AccessControl accessControlPersisted = accessControlManager.deleteAccessControl(accessControlId, updateUser);
        accessControlLogManager.createAccessControlLog(convertLog(accessControlPersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createAccessControls();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createAccessControls() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        
    }
    
    public AccessControlLog convertLog (AccessControl accessControl, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(accessControl);
        AccessControlLog accessControlLog = gson.fromJson(tmp,AccessControlLog.class);
        accessControlLog.setId(null);
        accessControlLog.setUpdateDate(null);
        accessControlLog.setTransactionId(transactionId);
        accessControlLog.setAccessControlId(accessControl.getId());
        accessControlLog.setAction(action);
        accessControlLog.setActiveObject(accessControl.getActive());
        return accessControlLog;
    }
}


