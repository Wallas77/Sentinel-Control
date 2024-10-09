package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.ToolLogManager;
import com.digivalle.sentinel.models.ToolLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ToolLogService {
    private final static Logger logger = LoggerFactory.getLogger(ToolLogService.class);

    @Autowired
    private ToolLogManager toolLogManager;
    
    
    public ToolLog getById(UUID toolLogId) throws EntityNotExistentException {
        return toolLogManager.getById(toolLogId);
    }
    
    public PagedResponse<ToolLog> getToolLog(ToolLog toolLog,   Paging paging) {
        return toolLogManager.getToolLog(toolLog, paging);
    }
    
    public List<ToolLog> findAll() {
        return toolLogManager.findAll();
    }
    
    public ToolLog createToolLog(ToolLog toolLog) throws BusinessLogicException, ExistentEntityException {
        return toolLogManager.createToolLog(toolLog);
    }
    
    public ToolLog updateToolLog(UUID toolLogId,ToolLog toolLog) throws BusinessLogicException, EntityNotExistentException {
        return toolLogManager.updateToolLog(toolLogId, toolLog);
    }
    
    public void deleteToolLog(UUID toolLogId) throws EntityNotExistentException {
        toolLogManager.deleteToolLog(toolLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createToolLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createToolLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


