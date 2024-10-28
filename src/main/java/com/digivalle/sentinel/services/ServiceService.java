package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.ServiceLogManager;
import com.digivalle.sentinel.managers.ServiceManager;
import com.digivalle.sentinel.models.Service;
import com.digivalle.sentinel.models.ServiceLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class ServiceService {
    private final static Logger logger = LoggerFactory.getLogger(ServiceService.class);

    @Autowired
    private ServiceManager ServiceManager;
    
    @Autowired
    private ServiceLogManager ServiceLogManager;
    
    
    public Service getById(UUID ServiceId) throws EntityNotExistentException {
        return ServiceManager.getById(ServiceId);
    }
    
    public PagedResponse<Service> getService(Service Service,   Paging paging) {
        return ServiceManager.getService(Service, paging);
    }
    
    public List<Service> findAll() {
        return ServiceManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Service createService(Service Service) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        Service ServicePersisted = ServiceManager.createService(Service);
        ServiceLogManager.createServiceLog(convertLog(ServicePersisted,null,Definitions.LOG_CREATE));
        return getById(ServicePersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Service updateService(UUID ServiceId,Service Service) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        Service ServicePersisted = ServiceManager.updateService(ServiceId, Service);
        ServiceLogManager.createServiceLog(convertLog(ServicePersisted,null,Definitions.LOG_UPDATE));
        return getById(ServicePersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteService(UUID ServiceId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        Service ServicePersisted = ServiceManager.deleteService(ServiceId, updateUser);
        ServiceLogManager.createServiceLog(convertLog(ServicePersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createServices();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createServices() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        
    }
    
    public ServiceLog convertLog (Service service, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(service);
        ServiceLog serviceLog = gson.fromJson(tmp,ServiceLog.class);
        serviceLog.setId(null);
        serviceLog.setUpdateDate(null);
        serviceLog.setTransactionId(transactionId);
        serviceLog.setServiceId(service.getId());
        serviceLog.setAction(action);
        serviceLog.setActiveObject(service.getActive());
        return serviceLog;
    }
}


