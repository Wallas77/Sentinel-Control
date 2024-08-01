package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.RoleLogManager;
import com.digivalle.sentinel.models.RoleLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class RoleLogService {
    private final static Logger logger = LoggerFactory.getLogger(RoleLogService.class);

    @Autowired
    private RoleLogManager roleLogManager;
    
    
    public RoleLog getById(UUID roleLogId) throws EntityNotExistentException {
        return roleLogManager.getById(roleLogId);
    }
    
    public PagedResponse<RoleLog> getRoleLog(RoleLog roleLog,   Paging paging) {
        return roleLogManager.getRoleLog(roleLog, paging);
    }
    
    public List<RoleLog> findAll() {
        return roleLogManager.findAll();
    }
    
    public RoleLog createRoleLog(RoleLog roleLog) throws BusinessLogicException, ExistentEntityException {
        return roleLogManager.createRoleLog(roleLog);
    }
    
    public RoleLog updateRoleLog(UUID roleLogId,RoleLog roleLog) throws BusinessLogicException, EntityNotExistentException {
        return roleLogManager.updateRoleLog(roleLogId, roleLog);
    }
    
    public void deleteRoleLog(UUID roleLogId) throws EntityNotExistentException {
        roleLogManager.deleteRoleLog(roleLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createRoleLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createRoleLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


