package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.RoleLogManager;
import com.digivalle.sentinel.managers.RoleManager;
import com.digivalle.sentinel.models.Role;
import com.digivalle.sentinel.models.RoleLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class RoleService {
    private final static Logger logger = LoggerFactory.getLogger(RoleService.class);

    @Autowired
    private RoleManager roleManager;
    
    @Autowired
    private RoleLogManager roleLogManager;
    
    
    public Role getById(UUID roleId) throws EntityNotExistentException {
        return roleManager.getById(roleId);
    }
    
    public PagedResponse<Role> getRole(Role role,   Paging paging) {
        return roleManager.getRole(role, paging);
    }
    
    public List<Role> findAll() {
        return roleManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Role createRole(Role role) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        Role rolePersisted = roleManager.createRole(role);
        roleLogManager.createRoleLog(convertLog(rolePersisted,null,Definitions.LOG_CREATE));
        return getById(rolePersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Role updateRole(UUID roleId,Role role) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        Role rolePersisted = roleManager.updateRole(roleId, role);
        roleLogManager.createRoleLog(convertLog(rolePersisted,null,Definitions.LOG_UPDATE));
        return getById(rolePersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteRole(UUID roleId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        Role rolePersisted = roleManager.deleteRole(roleId, updateUser);
        roleLogManager.createRoleLog(convertLog(rolePersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createRoles();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createRoles() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        List<Role> rolees = findAll();
        if(rolees.isEmpty()){
            Role role = new Role();
            role.setName(Definitions.APPLICATION_SENTINEL);
            role.setDescription(Definitions.APPLICATION_SENTINEL_DESC);
            role.setUpdateUser(Definitions.USER_DEFAULT);
            createRole(role);
            
                        logger.info("Las Rolees no existen, inicialización ejecutada");
        } else {
            logger.info("Las Rolees ya existen, inicialización no ejecutada");
        }
    }
    
    public RoleLog convertLog (Role role, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(role);
        RoleLog roleLog = gson.fromJson(tmp,RoleLog.class);
        roleLog.setId(null);
        roleLog.setUpdateDate(null);
        roleLog.setTransactionId(transactionId);
        roleLog.setRoleId(role.getId());
        roleLog.setAction(action);
        roleLog.setActiveObject(role.getActive());
        return roleLog;
    }
}


