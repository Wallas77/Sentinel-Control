package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.RoleResponsabilityLogManager;
import com.digivalle.sentinel.managers.RoleResponsabilityManager;
import com.digivalle.sentinel.models.RoleResponsability;
import com.digivalle.sentinel.models.RoleResponsabilityLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class RoleResponsabilityService {
    private final static Logger logger = LoggerFactory.getLogger(RoleResponsabilityService.class);

    @Autowired
    private RoleResponsabilityManager roleResponsabilityManager;
    
    @Autowired
    private RoleResponsabilityLogManager roleResponsabilityLogManager;
    
    
    public RoleResponsability getById(UUID roleResponsabilityId) throws EntityNotExistentException {
        return roleResponsabilityManager.getById(roleResponsabilityId);
    }
    
    public PagedResponse<RoleResponsability> getRoleResponsability(RoleResponsability roleResponsability,   Paging paging) {
        return roleResponsabilityManager.getRoleResponsability(roleResponsability, paging);
    }
    
    public List<RoleResponsability> findAll() {
        return roleResponsabilityManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public RoleResponsability createRoleResponsability(RoleResponsability roleResponsability) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        RoleResponsability roleResponsabilityPersisted = roleResponsabilityManager.createRoleResponsability(roleResponsability);
        roleResponsabilityLogManager.createRoleResponsabilityLog(convertLog(roleResponsabilityPersisted,null,Definitions.LOG_CREATE));
        return getById(roleResponsabilityPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public RoleResponsability updateRoleResponsability(UUID roleResponsabilityId,RoleResponsability roleResponsability) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        RoleResponsability roleResponsabilityPersisted = roleResponsabilityManager.updateRoleResponsability(roleResponsabilityId, roleResponsability);
        roleResponsabilityLogManager.createRoleResponsabilityLog(convertLog(roleResponsabilityPersisted,null,Definitions.LOG_UPDATE));
        return getById(roleResponsabilityPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteRoleResponsability(UUID roleResponsabilityId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        RoleResponsability roleResponsabilityPersisted = roleResponsabilityManager.deleteRoleResponsability(roleResponsabilityId, updateUser);
        roleResponsabilityLogManager.createRoleResponsabilityLog(convertLog(roleResponsabilityPersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createRoleResponsabilitys();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createRoleResponsabilitys() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        List<RoleResponsability> roleResponsabilityes = findAll();
        if(roleResponsabilityes.isEmpty()){
            RoleResponsability roleResponsability = new RoleResponsability();
            roleResponsability.setName(Definitions.APPLICATION_SENTINEL);
            roleResponsability.setDescription(Definitions.APPLICATION_SENTINEL_DESC);
            roleResponsability.setUpdateUser(Definitions.USER_DEFAULT);
            createRoleResponsability(roleResponsability);
            
                        logger.info("Las RoleResponsabilityes no existen, inicialización ejecutada");
        } else {
            logger.info("Las RoleResponsabilityes ya existen, inicialización no ejecutada");
        }
    }
    
    public RoleResponsabilityLog convertLog (RoleResponsability roleResponsability, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(roleResponsability);
        RoleResponsabilityLog roleResponsabilityLog = gson.fromJson(tmp,RoleResponsabilityLog.class);
        roleResponsabilityLog.setId(null);
        roleResponsabilityLog.setUpdateDate(null);
        roleResponsabilityLog.setTransactionId(transactionId);
        roleResponsabilityLog.setRoleResponsabilityId(roleResponsability.getId());
        roleResponsabilityLog.setAction(action);
        return roleResponsabilityLog;
    }
}


