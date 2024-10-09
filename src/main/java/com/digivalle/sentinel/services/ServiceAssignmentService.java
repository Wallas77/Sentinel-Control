package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.ServiceAssignmentLogManager;
import com.digivalle.sentinel.managers.ServiceAssignmentManager;
import com.digivalle.sentinel.models.ServiceAssignment;
import com.digivalle.sentinel.models.ServiceAssignmentLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class ServiceAssignmentService {
    private final static Logger logger = LoggerFactory.getLogger(ServiceAssignmentService.class);

    @Autowired
    private ServiceAssignmentManager serviceAssignmentManager;
    
    @Autowired
    private ServiceAssignmentLogManager serviceAssignmentLogManager;
    
    
    public ServiceAssignment getById(UUID serviceAssignmentId) throws EntityNotExistentException {
        return serviceAssignmentManager.getById(serviceAssignmentId);
    }
    
    public PagedResponse<ServiceAssignment> getServiceAssignment(ServiceAssignment serviceAssignment,   Paging paging) {
        return serviceAssignmentManager.getServiceAssignment(serviceAssignment, paging);
    }
    
    public List<ServiceAssignment> findAll() {
        return serviceAssignmentManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public ServiceAssignment createServiceAssignment(ServiceAssignment serviceAssignment) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        ServiceAssignment serviceAssignmentPersisted = serviceAssignmentManager.createServiceAssignment(serviceAssignment);
        serviceAssignmentLogManager.createServiceAssignmentLog(convertLog(serviceAssignmentPersisted,null,Definitions.LOG_CREATE));
        return getById(serviceAssignmentPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public ServiceAssignment updateServiceAssignment(UUID serviceAssignmentId,ServiceAssignment serviceAssignment) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        ServiceAssignment serviceAssignmentPersisted = serviceAssignmentManager.updateServiceAssignment(serviceAssignmentId, serviceAssignment);
        serviceAssignmentLogManager.createServiceAssignmentLog(convertLog(serviceAssignmentPersisted,null,Definitions.LOG_UPDATE));
        return getById(serviceAssignmentPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteServiceAssignment(UUID serviceAssignmentId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        ServiceAssignment serviceAssignmentPersisted = serviceAssignmentManager.deleteServiceAssignment(serviceAssignmentId, updateUser);
        serviceAssignmentLogManager.createServiceAssignmentLog(convertLog(serviceAssignmentPersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createServiceAssignments();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createServiceAssignments() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        
    }
    
    public ServiceAssignmentLog convertLog (ServiceAssignment serviceAssignment, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(serviceAssignment);
        ServiceAssignmentLog serviceAssignmentLog = gson.fromJson(tmp,ServiceAssignmentLog.class);
        serviceAssignmentLog.setId(null);
        serviceAssignmentLog.setUpdateDate(null);
        serviceAssignmentLog.setTransactionId(transactionId);
        serviceAssignmentLog.setServiceAssignmentId(serviceAssignment.getId());
        serviceAssignmentLog.setAction(action);
        return serviceAssignmentLog;
    }
}


