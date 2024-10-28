package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.ToolTypeLogManager;
import com.digivalle.sentinel.managers.ToolTypeManager;
import com.digivalle.sentinel.models.ToolType;
import com.digivalle.sentinel.models.ToolTypeLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class ToolTypeService {
    private final static Logger logger = LoggerFactory.getLogger(ToolTypeService.class);

    @Autowired
    private ToolTypeManager toolTypeManager;
    
    @Autowired
    private ToolTypeLogManager toolTypeLogManager;
    
    
    public ToolType getById(UUID toolTypeId) throws EntityNotExistentException {
        return toolTypeManager.getById(toolTypeId);
    }
    
    public PagedResponse<ToolType> getToolType(ToolType toolType,   Paging paging) {
        return toolTypeManager.getToolType(toolType, paging);
    }
    
    public List<ToolType> findAll() {
        return toolTypeManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public ToolType createToolType(ToolType toolType) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        ToolType toolTypePersisted = toolTypeManager.createToolType(toolType);
        toolTypeLogManager.createToolTypeLog(convertLog(toolTypePersisted,null,Definitions.LOG_CREATE));
        return getById(toolTypePersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public ToolType updateToolType(UUID toolTypeId,ToolType toolType) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        ToolType toolTypePersisted = toolTypeManager.updateToolType(toolTypeId, toolType);
        toolTypeLogManager.createToolTypeLog(convertLog(toolTypePersisted,null,Definitions.LOG_UPDATE));
        return getById(toolTypePersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteToolType(UUID toolTypeId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        ToolType toolTypePersisted = toolTypeManager.deleteToolType(toolTypeId, updateUser);
        toolTypeLogManager.createToolTypeLog(convertLog(toolTypePersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createToolTypes();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createToolTypes() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        
    }
    
    public ToolTypeLog convertLog (ToolType toolType, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(toolType);
        ToolTypeLog toolTypeLog = gson.fromJson(tmp,ToolTypeLog.class);
        toolTypeLog.setId(null);
        toolTypeLog.setUpdateDate(null);
        toolTypeLog.setTransactionId(transactionId);
        toolTypeLog.setToolTypeId(toolType.getId());
        toolTypeLog.setAction(action);
        toolTypeLog.setActiveObject(toolType.getActive());
        return toolTypeLog;
    }
}


