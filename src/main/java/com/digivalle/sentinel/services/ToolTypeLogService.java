package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.ToolTypeLogManager;
import com.digivalle.sentinel.models.ToolTypeLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ToolTypeLogService {
    private final static Logger logger = LoggerFactory.getLogger(ToolTypeLogService.class);

    @Autowired
    private ToolTypeLogManager toolTypeLogManager;
    
    
    public ToolTypeLog getById(UUID toolTypeLogId) throws EntityNotExistentException {
        return toolTypeLogManager.getById(toolTypeLogId);
    }
    
    public PagedResponse<ToolTypeLog> getToolTypeLog(ToolTypeLog toolTypeLog,   Paging paging) {
        return toolTypeLogManager.getToolTypeLog(toolTypeLog, paging);
    }
    
    public List<ToolTypeLog> findAll() {
        return toolTypeLogManager.findAll();
    }
    
    public ToolTypeLog createToolTypeLog(ToolTypeLog toolTypeLog) throws BusinessLogicException, ExistentEntityException {
        return toolTypeLogManager.createToolTypeLog(toolTypeLog);
    }
    
    public ToolTypeLog updateToolTypeLog(UUID toolTypeLogId,ToolTypeLog toolTypeLog) throws BusinessLogicException, EntityNotExistentException {
        return toolTypeLogManager.updateToolTypeLog(toolTypeLogId, toolTypeLog);
    }
    
    public void deleteToolTypeLog(UUID toolTypeLogId) throws EntityNotExistentException {
        toolTypeLogManager.deleteToolTypeLog(toolTypeLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createToolTypeLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createToolTypeLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


