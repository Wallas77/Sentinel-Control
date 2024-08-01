package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.ModuleLogManager;
import com.digivalle.sentinel.models.ModuleLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ModuleLogService {
    private final static Logger logger = LoggerFactory.getLogger(ModuleLogService.class);

    @Autowired
    private ModuleLogManager moduleLogManager;
    
    
    public ModuleLog getById(UUID moduleLogId) throws EntityNotExistentException {
        return moduleLogManager.getById(moduleLogId);
    }
    
    public PagedResponse<ModuleLog> getModuleLog(ModuleLog moduleLog,   Paging paging) {
        return moduleLogManager.getModuleLog(moduleLog, paging);
    }
    
    public List<ModuleLog> findAll() {
        return moduleLogManager.findAll();
    }
    
    public ModuleLog createModuleLog(ModuleLog moduleLog) throws BusinessLogicException, ExistentEntityException {
        return moduleLogManager.createModuleLog(moduleLog);
    }
    
    public ModuleLog updateModuleLog(UUID moduleLogId,ModuleLog moduleLog) throws BusinessLogicException, EntityNotExistentException {
        return moduleLogManager.updateModuleLog(moduleLogId, moduleLog);
    }
    
    public void deleteModuleLog(UUID moduleLogId) throws EntityNotExistentException {
        moduleLogManager.deleteModuleLog(moduleLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createModuleLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createModuleLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


