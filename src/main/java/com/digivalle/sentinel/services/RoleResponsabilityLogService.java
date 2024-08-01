package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.RoleResponsabilityLogManager;
import com.digivalle.sentinel.models.RoleResponsabilityLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class RoleResponsabilityLogService {
    private final static Logger logger = LoggerFactory.getLogger(RoleResponsabilityLogService.class);

    @Autowired
    private RoleResponsabilityLogManager roleResponsabilityLogManager;
    
    
    public RoleResponsabilityLog getById(UUID roleResponsabilityLogId) throws EntityNotExistentException {
        return roleResponsabilityLogManager.getById(roleResponsabilityLogId);
    }
    
    public PagedResponse<RoleResponsabilityLog> getRoleResponsabilityLog(RoleResponsabilityLog roleResponsabilityLog,   Paging paging) {
        return roleResponsabilityLogManager.getRoleResponsabilityLog(roleResponsabilityLog, paging);
    }
    
    public List<RoleResponsabilityLog> findAll() {
        return roleResponsabilityLogManager.findAll();
    }
    
    public RoleResponsabilityLog createRoleResponsabilityLog(RoleResponsabilityLog roleResponsabilityLog) throws BusinessLogicException, ExistentEntityException {
        return roleResponsabilityLogManager.createRoleResponsabilityLog(roleResponsabilityLog);
    }
    
    public RoleResponsabilityLog updateRoleResponsabilityLog(UUID roleResponsabilityLogId,RoleResponsabilityLog roleResponsabilityLog) throws BusinessLogicException, EntityNotExistentException {
        return roleResponsabilityLogManager.updateRoleResponsabilityLog(roleResponsabilityLogId, roleResponsabilityLog);
    }
    
    public void deleteRoleResponsabilityLog(UUID roleResponsabilityLogId) throws EntityNotExistentException {
        roleResponsabilityLogManager.deleteRoleResponsabilityLog(roleResponsabilityLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createRoleResponsabilityLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createRoleResponsabilityLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


