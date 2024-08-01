package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.IncidentTypeLogManager;
import com.digivalle.sentinel.managers.IncidentTypeManager;
import com.digivalle.sentinel.models.IncidentType;
import com.digivalle.sentinel.models.IncidentTypeLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class IncidentTypeService {
    private final static Logger logger = LoggerFactory.getLogger(IncidentTypeService.class);

    @Autowired
    private IncidentTypeManager incidentTypeManager;
    
    @Autowired
    private IncidentTypeLogManager incidentTypeLogManager;
    
    
    public IncidentType getById(UUID incidentTypeId) throws EntityNotExistentException {
        return incidentTypeManager.getById(incidentTypeId);
    }
    
    public PagedResponse<IncidentType> getIncidentType(IncidentType incidentType,   Paging paging) {
        return incidentTypeManager.getIncidentType(incidentType, paging);
    }
    
    public List<IncidentType> findAll() {
        return incidentTypeManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public IncidentType createIncidentType(IncidentType incidentType) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        IncidentType incidentTypePersisted = incidentTypeManager.createIncidentType(incidentType);
        incidentTypeLogManager.createIncidentTypeLog(convertLog(incidentTypePersisted,null,Definitions.LOG_CREATE));
        return getById(incidentTypePersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public IncidentType updateIncidentType(UUID incidentTypeId,IncidentType incidentType) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        IncidentType incidentTypePersisted = incidentTypeManager.updateIncidentType(incidentTypeId, incidentType);
        incidentTypeLogManager.createIncidentTypeLog(convertLog(incidentTypePersisted,null,Definitions.LOG_UPDATE));
        return getById(incidentTypePersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteIncidentType(UUID incidentTypeId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        IncidentType incidentTypePersisted = incidentTypeManager.deleteIncidentType(incidentTypeId, updateUser);
        incidentTypeLogManager.createIncidentTypeLog(convertLog(incidentTypePersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createIncidentTypes();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createIncidentTypes() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        List<IncidentType> incidentTypees = findAll();
        if(incidentTypees.isEmpty()){
            IncidentType incidentType = new IncidentType();
            incidentType.setName(Definitions.APPLICATION_SENTINEL);
            incidentType.setDescription(Definitions.APPLICATION_SENTINEL_DESC);
            incidentType.setUpdateUser(Definitions.USER_DEFAULT);
            createIncidentType(incidentType);
            
                        logger.info("Las IncidentTypees no existen, inicialización ejecutada");
        } else {
            logger.info("Las IncidentTypees ya existen, inicialización no ejecutada");
        }
    }
    
    public IncidentTypeLog convertLog (IncidentType incidentType, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(incidentType);
        IncidentTypeLog incidentTypeLog = gson.fromJson(tmp,IncidentTypeLog.class);
        incidentTypeLog.setId(null);
        incidentTypeLog.setUpdateDate(null);
        incidentTypeLog.setTransactionId(transactionId);
        incidentTypeLog.setIncidentTypeId(incidentType.getId());
        incidentTypeLog.setAction(action);
        return incidentTypeLog;
    }
}


