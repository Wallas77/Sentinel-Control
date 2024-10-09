package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.ToolLogManager;
import com.digivalle.sentinel.managers.ToolManager;
import com.digivalle.sentinel.models.Tool;
import com.digivalle.sentinel.models.ToolLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class ToolService {
    private final static Logger logger = LoggerFactory.getLogger(ToolService.class);

    @Autowired
    private ToolManager toolManager;
    
    @Autowired
    private ToolLogManager toolLogManager;
    
    
    public Tool getById(UUID toolId) throws EntityNotExistentException {
        return toolManager.getById(toolId);
    }
    
    public PagedResponse<Tool> getTool(Tool tool,   Paging paging) {
        return toolManager.getTool(tool, paging);
    }
    
    public List<Tool> findAll() {
        return toolManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Tool createTool(Tool tool) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        Tool toolPersisted = toolManager.createTool(tool);
        toolLogManager.createToolLog(convertLog(toolPersisted,null,Definitions.LOG_CREATE));
        return getById(toolPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Tool updateTool(UUID toolId,Tool tool) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        Tool toolPersisted = toolManager.updateTool(toolId, tool);
        toolLogManager.createToolLog(convertLog(toolPersisted,null,Definitions.LOG_UPDATE));
        return getById(toolPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteTool(UUID toolId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        Tool toolPersisted = toolManager.deleteTool(toolId, updateUser);
        toolLogManager.createToolLog(convertLog(toolPersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createTools();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createTools() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        List<Tool> tooles = findAll();
        if(tooles.isEmpty()){
            Tool tool = new Tool();
            tool.setName(Definitions.APPLICATION_SENTINEL);
            tool.setDescription(Definitions.APPLICATION_SENTINEL_DESC);
            tool.setUpdateUser(Definitions.USER_DEFAULT);
            createTool(tool);
            
                        logger.info("Las Tooles no existen, inicialización ejecutada");
        } else {
            logger.info("Las Tooles ya existen, inicialización no ejecutada");
        }
    }
    
    public ToolLog convertLog (Tool tool, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(tool);
        ToolLog toolLog = gson.fromJson(tmp,ToolLog.class);
        toolLog.setId(null);
        toolLog.setUpdateDate(null);
        toolLog.setTransactionId(transactionId);
        toolLog.setToolId(tool.getId());
        toolLog.setAction(action);
        return toolLog;
    }
}


