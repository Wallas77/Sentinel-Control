package com.digivalle.sentinel.services;

import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.models.Module;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.ApplicationManager;
import com.digivalle.sentinel.managers.ModuleLogManager;
import com.digivalle.sentinel.managers.ModuleManager;
import com.digivalle.sentinel.models.Application;
import com.digivalle.sentinel.models.ModuleLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class ModuleService {
    private final static Logger logger = LoggerFactory.getLogger(ModuleService.class);

    @Autowired
    private ModuleManager moduleManager;
    
    @Autowired
    private ModuleLogManager moduleLogManager;
    
    @Autowired
    private ApplicationManager applicationManager;
    
    
    public Module getById(UUID moduleId) throws EntityNotExistentException {
        return moduleManager.getById(moduleId);
    }
    
    public PagedResponse<Module> getModule(Module module,   Paging paging) {
        return moduleManager.getModule(module, paging);
    }
    
    public List<Module> findAll() {
        return moduleManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Module createModule(Module module) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        Module modulePersisted = moduleManager.createModule(module);
        moduleLogManager.createModuleLog(convertLog(modulePersisted,null,Definitions.LOG_CREATE));
        return getById(modulePersisted.getId());
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Module updateModule(UUID moduleId,Module module) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        Module modulePersisted = moduleManager.updateModule(moduleId, module);
        moduleLogManager.createModuleLog(convertLog(modulePersisted,null,Definitions.LOG_UPDATE));
        return getById(modulePersisted.getId());
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteModule(UUID moduleId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        Module modulePersisted = moduleManager.deleteModule(moduleId);
        modulePersisted.setUpdateUser(updateUser);
        moduleLogManager.createModuleLog(convertLog(modulePersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize()  {
        try{
            createModules();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createModules() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        List<Module> modules = findAll();
        if(modules.isEmpty()){    
            Paging paging = new Paging(0, 1);
            Application application = new Application(Definitions.APPLICATION_SENTINEL, Definitions.APPLICATION_SENTINEL_DESC);
            PagedResponse<Application> paged = applicationManager.getApplication(application, paging);
            if(paged.getTotal()>0){
                Module module = new Module();
                module.setApplication(paged.getElements().get(0));
                module.setName(Definitions.MODULE_SENTINEL_APPLICATIONS);
                module.setUpdateUser(Definitions.USER_DEFAULT);
                createModule(module);
                module = new Module();
                module.setApplication(paged.getElements().get(0));
                module.setName(Definitions.MODULE_SENTINEL_BRANCHES);
                module.setUpdateUser(Definitions.USER_DEFAULT);
                createModule(module);
                module = new Module();
                module.setApplication(paged.getElements().get(0));
                module.setName(Definitions.MODULE_SENTINEL_COUNTRIES);
                module.setUpdateUser(Definitions.USER_DEFAULT);
                createModule(module);
                module = new Module();
                module.setApplication(paged.getElements().get(0));
                module.setName(Definitions.MODULE_SENTINEL_CUSTOMERS);
                module.setUpdateUser(Definitions.USER_DEFAULT);
                createModule(module);
                module = new Module();
                module.setApplication(paged.getElements().get(0));
                module.setName(Definitions.MODULE_SENTINEL_EMPLOYEES);
                module.setUpdateUser(Definitions.USER_DEFAULT);
                createModule(module);
                module = new Module();
                module.setApplication(paged.getElements().get(0));
                module.setName(Definitions.MODULE_SENTINEL_FISCAL_INFO);
                module.setUpdateUser(Definitions.USER_DEFAULT);
                createModule(module);
                module = new Module();
                module.setApplication(paged.getElements().get(0));
                module.setName(Definitions.MODULE_SENTINEL_GRANTS);
                module.setUpdateUser(Definitions.USER_DEFAULT);
                createModule(module);
                module = new Module();
                module.setApplication(paged.getElements().get(0));
                module.setName(Definitions.MODULE_SENTINEL_INCIDENT_TYPES);
                module.setUpdateUser(Definitions.USER_DEFAULT);
                createModule(module);
                module = new Module();
                module.setApplication(paged.getElements().get(0));
                module.setName(Definitions.MODULE_SENTINEL_MODULES);
                module.setUpdateUser(Definitions.USER_DEFAULT);
                createModule(module);
                module = new Module();
                module.setApplication(paged.getElements().get(0));
                module.setName(Definitions.MODULE_SENTINEL_PROFILES);
                module.setUpdateUser(Definitions.USER_DEFAULT);
                createModule(module);
                module = new Module();
                module.setApplication(paged.getElements().get(0));
                module.setName(Definitions.MODULE_SENTINEL_SUPPLIERS);
                module.setUpdateUser(Definitions.USER_DEFAULT);
                createModule(module);
                module = new Module();
                module.setApplication(paged.getElements().get(0));
                module.setName(Definitions.MODULE_SENTINEL_USERS);
                module.setUpdateUser(Definitions.USER_DEFAULT);
                createModule(module);
                
                logger.info("Las Modules no existen, inicialización ejecutada");
            } else {
                throw new BusinessLogicException("No se encontro la aplicación: "+Definitions.APPLICATION_SENTINEL);
            }
        } else {
            logger.info("Las Modules ya existen, inicialización no ejecutada");
        }
    }
    
    public ModuleLog convertLog (Module module, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(module);
        ModuleLog moduleLog = gson.fromJson(tmp,ModuleLog.class);
        moduleLog.setId(null);
        moduleLog.setUpdateDate(null);
        moduleLog.setUpdateUser(module.getUpdateUser());
        moduleLog.setTransactionId(transactionId);
        moduleLog.setModuleId(module.getId());
        moduleLog.setAction(action);
        return moduleLog;
    }
}


