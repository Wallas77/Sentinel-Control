package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.AccessControlObjectLogManager;
import com.digivalle.sentinel.managers.AccessControlObjectManager;
import com.digivalle.sentinel.models.AccessControlObject;
import com.digivalle.sentinel.models.AccessControlObjectLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class AccessControlObjectService {
    private final static Logger logger = LoggerFactory.getLogger(AccessControlObjectService.class);

    @Autowired
    private AccessControlObjectManager accessControlObjectManager;
    
    @Autowired
    private AccessControlObjectLogManager accessControlObjectLogManager;
    
    
    public AccessControlObject getById(UUID accessControlObjectId) throws EntityNotExistentException {
        return accessControlObjectManager.getById(accessControlObjectId);
    }
    
    public PagedResponse<AccessControlObject> getAccessControlObject(AccessControlObject accessControlObject,   Paging paging) {
        return accessControlObjectManager.getAccessControlObject(accessControlObject, paging);
    }
    
    public List<AccessControlObject> findAll() {
        return accessControlObjectManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public AccessControlObject createAccessControlObject(AccessControlObject accessControlObject) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        AccessControlObject accessControlObjectPersisted = accessControlObjectManager.createAccessControlObject(accessControlObject);
        accessControlObjectLogManager.createAccessControlObjectLog(convertLog(accessControlObjectPersisted,null,Definitions.LOG_CREATE));
        return getById(accessControlObjectPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public AccessControlObject updateAccessControlObject(UUID accessControlObjectId,AccessControlObject accessControlObject) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        AccessControlObject accessControlObjectPersisted = accessControlObjectManager.updateAccessControlObject(accessControlObjectId, accessControlObject);
        accessControlObjectLogManager.createAccessControlObjectLog(convertLog(accessControlObjectPersisted,null,Definitions.LOG_UPDATE));
        return getById(accessControlObjectPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteAccessControlObject(UUID accessControlObjectId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        AccessControlObject accessControlObjectPersisted = accessControlObjectManager.deleteAccessControlObject(accessControlObjectId, updateUser);
        accessControlObjectLogManager.createAccessControlObjectLog(convertLog(accessControlObjectPersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createAccessControlObjects();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createAccessControlObjects() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        List<AccessControlObject> accessControlObjectes = findAll();
        if(accessControlObjectes.isEmpty()){
            AccessControlObject accessControlObject = new AccessControlObject();
            accessControlObject.setName(Definitions.APPLICATION_SENTINEL);
            accessControlObject.setDescription(Definitions.APPLICATION_SENTINEL_DESC);
            accessControlObject.setUpdateUser(Definitions.USER_DEFAULT);
            createAccessControlObject(accessControlObject);
            
                        logger.info("Las AccessControlObjectes no existen, inicialización ejecutada");
        } else {
            logger.info("Las AccessControlObjectes ya existen, inicialización no ejecutada");
        }
    }
    
    public AccessControlObjectLog convertLog (AccessControlObject accessControlObject, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(accessControlObject);
        AccessControlObjectLog accessControlObjectLog = gson.fromJson(tmp,AccessControlObjectLog.class);
        accessControlObjectLog.setId(null);
        accessControlObjectLog.setUpdateDate(null);
        accessControlObjectLog.setTransactionId(transactionId);
        accessControlObjectLog.setAccessControlObjectId(accessControlObject.getId());
        accessControlObjectLog.setAction(action);
        accessControlObjectLog.setActiveObject(accessControlObject.getActive());
        return accessControlObjectLog;
    }
}


