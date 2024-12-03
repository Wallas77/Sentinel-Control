package com.digivalle.sentinel.services;



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
import com.digivalle.sentinel.models.User;
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
    
    @Autowired
    private ServiceAttendanceService serviceAttendanceService;
    
    public ServiceAssignment getById(UUID serviceAssignmentId) throws EntityNotExistentException {
        return serviceAssignmentManager.getById(serviceAssignmentId);
    }
    
    public PagedResponse<ServiceAssignment> getServiceAssignment(ServiceAssignment serviceAssignment,   Paging paging) {
        return serviceAssignmentManager.getServiceAssignment(serviceAssignment, paging);
    }
    
    public List<ServiceAssignment> getByService_IdAndActiveAndDeleted(UUID serviceId, Boolean active, Boolean deleted){
        return serviceAssignmentManager.getByService_IdAndActiveAndDeleted(serviceId, active, deleted);
    }
    
    public List<ServiceAssignment> findAll() {
        return serviceAssignmentManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public ServiceAssignment createServiceAssignment(ServiceAssignment serviceAssignment, User user) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        ServiceAssignment serviceAssignmentPersisted = serviceAssignmentManager.createServiceAssignment(serviceAssignment);
        serviceAssignmentLogManager.createServiceAssignmentLog(convertLog(serviceAssignmentPersisted,null,Definitions.LOG_CREATE));
        if(serviceAssignment.getEmployee()!=null && serviceAssignment.getEmployee().getId()!=null){
            serviceAttendanceService.createServiceAttendancesFromServiceAssignment(serviceAssignment, user);
        }
        return getById(serviceAssignmentPersisted.getId());
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public ServiceAssignment updateServiceAssignment(UUID serviceAssignmentId,ServiceAssignment serviceAssignment, User user) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        ServiceAssignment serviceAssignmentPrevious = serviceAssignmentManager.getById(serviceAssignmentId);
        if(serviceAssignmentPrevious.getEmployee()!=null && serviceAssignmentPrevious.getEmployee().getId()!=null){
            //activityService.deleteByServiceAssignmentId(serviceAssignmentId);
            serviceAttendanceService.deleteByServiceAssignmentId(serviceAssignmentId);
        }
        ServiceAssignment serviceAssignmentPersisted = serviceAssignmentManager.updateServiceAssignment(serviceAssignmentId, serviceAssignment);
        serviceAssignmentLogManager.createServiceAssignmentLog(convertLog(serviceAssignmentPersisted,null,Definitions.LOG_UPDATE));
        
        if(serviceAssignment.getEmployee()!=null && serviceAssignment.getEmployee().getId()!=null){
            serviceAttendanceService.createServiceAttendancesFromServiceAssignment(serviceAssignment, user);
        }
        return getById(serviceAssignmentPersisted.getId());
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteServiceAssignment(UUID serviceAssignmentId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        ServiceAssignment serviceAssignmentPersisted = serviceAssignmentManager.deleteServiceAssignment(serviceAssignmentId, updateUser);
        serviceAttendanceService.deleteByServiceAssignmentId(serviceAssignmentId);
        serviceAssignmentLogManager.createServiceAssignmentLog(convertLog(serviceAssignmentPersisted,null,Definitions.LOG_DELETE));
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteServiceAssignmentByServiceId(UUID serviceId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        
        List<ServiceAssignment> serviceAssignments = getByService_IdAndActiveAndDeleted(serviceId, Boolean.TRUE, Boolean.FALSE);
        for(ServiceAssignment serviceAssignment: serviceAssignments){
            ServiceAssignment serviceAssignmentPersisted = serviceAssignmentManager.deleteServiceAssignment(serviceAssignment.getId(), updateUser);
            serviceAttendanceService.deleteByServiceAssignmentId(serviceAssignment.getId());
            serviceAssignmentLogManager.createServiceAssignmentLog(convertLog(serviceAssignmentPersisted,null,Definitions.LOG_DELETE));   
        }
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
        //Gson gson= new Gson();
        //String tmp = gson.toJson(serviceAssignment);
        //ServiceAssignmentLog serviceAssignmentLog = gson.fromJson(tmp,ServiceAssignmentLog.class);
        ServiceAssignmentLog serviceAssignmentLog =new ServiceAssignmentLog();
        serviceAssignmentLog.setEmployee(serviceAssignment.getEmployee());
        serviceAssignmentLog.setEndDate(serviceAssignment.getEndDate());
        serviceAssignmentLog.setEntryTime(serviceAssignment.getEntryTime());
        serviceAssignmentLog.setHoursPerDay(serviceAssignment.getHoursPerDay());
        serviceAssignmentLog.setRecurrenceInDays(serviceAssignment.getRecurrenceInDays());
        serviceAssignmentLog.setRecurrencePerNumberDays(serviceAssignment.getRecurrencePerNumberDays());
        serviceAssignmentLog.setRecurrencePerWeekDays(serviceAssignment.getRecurrencePerWeekDays());
        serviceAssignmentLog.setRole(serviceAssignment.getRole());
        serviceAssignmentLog.setSalaryParDayAmount(serviceAssignment.getSalaryParDayAmount());
        serviceAssignmentLog.setService(serviceAssignment.getService());
        serviceAssignmentLog.setStartDate(serviceAssignment.getStartDate());
        
        serviceAssignmentLog.setId(null);
        serviceAssignmentLog.setUpdateDate(null);
        serviceAssignmentLog.setTransactionId(transactionId);
        serviceAssignmentLog.setServiceAssignmentId(serviceAssignment.getId());
        serviceAssignmentLog.setAction(action);
        serviceAssignmentLog.setActiveObject(serviceAssignment.getActive());
        return serviceAssignmentLog;
    }
}


