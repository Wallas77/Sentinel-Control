package com.digivalle.sentinel.services;

import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.ServiceAttendanceLogManager;
import com.digivalle.sentinel.managers.ServiceAttendanceManager;
import com.digivalle.sentinel.models.ServiceAssignment;
import com.digivalle.sentinel.models.ServiceAttendance;
import com.digivalle.sentinel.models.ServiceAttendanceLog;
import com.digivalle.sentinel.models.User;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class ServiceAttendanceService {
    private final static Logger logger = LoggerFactory.getLogger(ServiceAttendanceService.class);

    @Autowired
    private ServiceAttendanceManager serviceAttendanceManager;
    
    @Autowired
    private ServiceAttendanceLogManager serviceAttendanceLogManager;
    
    @Autowired
    private ActivityService activityService;
    
    
    public ServiceAttendance getById(UUID serviceAttendanceId) throws EntityNotExistentException {
        return serviceAttendanceManager.getById(serviceAttendanceId);
    }
    
    public PagedResponse<ServiceAttendance> getServiceAttendance(ServiceAttendance serviceAttendance,   Paging paging) {
        return serviceAttendanceManager.getServiceAttendance(serviceAttendance, paging);
    }
    
    public List<ServiceAttendance> findAll() {
        return serviceAttendanceManager.findAll();
    }
    
   @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public ServiceAttendance createServiceAttendance(ServiceAttendance serviceAttendance) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        ServiceAttendance serviceAttendancePersisted = serviceAttendanceManager.createServiceAttendance(serviceAttendance);
        serviceAttendanceLogManager.createServiceAttendanceLog(convertLog(serviceAttendancePersisted,null,Definitions.LOG_CREATE));
        activityService.createActivitiesFromServiceAttendance(serviceAttendance);
        return getById(serviceAttendancePersisted.getId());
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void createServiceAttendancesFromServiceAssignment(ServiceAssignment serviceAssignment, User user) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException{
        
        Date dateToProcess = serviceAssignment.getStartDate();
        if(dateToProcess==null){
            Calendar cal = Calendar.getInstance();
            cal.setTime(serviceAssignment.getService().getStartContractDate());
            cal.set(Calendar.HOUR_OF_DAY, serviceAssignment.getEntryTime().getHour());
            cal.set(Calendar.MINUTE, serviceAssignment.getEntryTime().getMinute());
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            dateToProcess = cal.getTime();
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateToProcess);
            cal.set(Calendar.HOUR_OF_DAY, serviceAssignment.getEntryTime().getHour());
            cal.set(Calendar.MINUTE, serviceAssignment.getEntryTime().getMinute());
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            dateToProcess = cal.getTime();
        }
        if(dateToProcess.before(new Date())){
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 1);
            cal.set(Calendar.HOUR_OF_DAY, serviceAssignment.getEntryTime().getHour());
            cal.set(Calendar.MINUTE, serviceAssignment.getEntryTime().getMinute());
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            dateToProcess = cal.getTime();
        }
        Date endDateToProcess = serviceAssignment.getEndDate();
        if(endDateToProcess == null){
            endDateToProcess = serviceAssignment.getService().getEndContractDate();
        }
        List<Integer> weekDays = new ArrayList<>();
        if(serviceAssignment.getRecurrencePerWeekDays()!=null && !serviceAssignment.getRecurrencePerWeekDays().trim().isEmpty()){
            String[] split = serviceAssignment.getRecurrencePerWeekDays().trim().split(",");
            for (String split1 : split) {
                switch (split1.trim()) {
                    case "Domingo" -> weekDays.add(Calendar.SUNDAY);
                    case "Lunes" -> weekDays.add(Calendar.MONDAY);
                    case "Martes" -> weekDays.add(Calendar.TUESDAY);
                    case "Miércoles" -> weekDays.add(Calendar.WEDNESDAY);
                    case "Jueves" -> weekDays.add(Calendar.THURSDAY);
                    case "Viernes" -> weekDays.add(Calendar.FRIDAY);
                    case "Sabado" -> weekDays.add(Calendar.SATURDAY);
                }
            }
        }
        List<Integer> numberDays = new ArrayList<>();
        if(serviceAssignment.getRecurrencePerNumberDays()!=null && !serviceAssignment.getRecurrencePerNumberDays().trim().isEmpty()){
            String[] split = serviceAssignment.getRecurrencePerNumberDays().trim().split(",");
            for (String split1 : split) {
                numberDays.add(Integer.valueOf(split1.trim()));
            }
        }
        while(!dateToProcess.after(endDateToProcess)){
            ServiceAttendance serviceAttendance = new ServiceAttendance();
            serviceAttendance.setServiceAssignment(serviceAssignment);
            serviceAttendance.setEmployee(serviceAssignment.getEmployee());
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateToProcess);
            cal.set(Calendar.HOUR_OF_DAY, serviceAssignment.getEntryTime().getHour());
            cal.set(Calendar.MINUTE, serviceAssignment.getEntryTime().getMinute());
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            serviceAttendance.setStartDate(cal.getTime());
            Calendar calStartDate = Calendar.getInstance();
            calStartDate.setTime(cal.getTime());
            //System.out.println("Day of the Week=>"+cal.get(Calendar.DAY_OF_WEEK));
            //cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, serviceAssignment.getHoursPerDay());
            serviceAttendance.setEndDate(cal.getTime());
            serviceAttendance.setHoursPerDay(serviceAssignment.getHoursPerDay());
            serviceAttendance.setRole(serviceAssignment.getRole());
            serviceAttendance.setSalaryParDayAmount(serviceAssignment.getSalaryParDayAmount());
            serviceAttendance.setService(serviceAssignment.getService());
            serviceAttendance.setUpdateUser(user.getEmail());
            if(weekDays.isEmpty() && numberDays.isEmpty()){
                createServiceAttendance(serviceAttendance);
            } else if(!weekDays.isEmpty()){
                if(weekDays.contains(calStartDate.get(Calendar.DAY_OF_WEEK))){
                    if(numberDays.isEmpty()){
                        createServiceAttendance(serviceAttendance);
                    } else if(numberDays.contains(calStartDate.get(Calendar.DAY_OF_MONTH))){
                        createServiceAttendance(serviceAttendance);
                    }
                }
            } else if(!numberDays.isEmpty()){
                if(numberDays.contains(calStartDate.get(Calendar.DAY_OF_MONTH))){
                    createServiceAttendance(serviceAttendance);
                }
            }
                 
            //cal = Calendar.getInstance();
            //cal.setTime(dateToProcess);
            cal.add(Calendar.DATE, serviceAssignment.getRecurrenceInDays());
            dateToProcess = cal.getTime();
        }
       
    
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public ServiceAttendance updateServiceAttendance(UUID serviceAttendanceId,ServiceAttendance serviceAttendance) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        ServiceAttendance serviceAttendancePrevious = getById(serviceAttendanceId);
        activityService.deleteByServiceAttendanceIdAndEmployeeId(serviceAttendanceId, serviceAttendancePrevious.getEmployee().getId());
        ServiceAttendance serviceAttendancePersisted = serviceAttendanceManager.updateServiceAttendance(serviceAttendanceId, serviceAttendance);
        serviceAttendanceLogManager.createServiceAttendanceLog(convertLog(serviceAttendancePersisted,null,Definitions.LOG_UPDATE));
        activityService.createActivitiesFromServiceAttendance(serviceAttendance);
        return getById(serviceAttendancePersisted.getId());
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteServiceAttendance(UUID serviceAttendanceId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        ServiceAttendance serviceAttendancePersisted = serviceAttendanceManager.deleteServiceAttendance(serviceAttendanceId, updateUser);
        serviceAttendanceLogManager.createServiceAttendanceLog(convertLog(serviceAttendancePersisted,null,Definitions.LOG_DELETE));
        activityService.deleteByServiceAttendanceIdAndEmployeeId(serviceAttendanceId,serviceAttendancePersisted.getEmployee().getId());
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteByServiceAssignmentId(UUID serviceAssignmentId){
        activityService.deleteByServiceAssignmentId(serviceAssignmentId);
        serviceAttendanceLogManager.deleteByServiceAssignmentId(serviceAssignmentId);
        serviceAttendanceManager.deleteByServiceAssignmentId(serviceAssignmentId);
        
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteByServiceAssignmentIdAndEmployeeId(UUID serviceAssignmentId, UUID employeeId){
        activityService.deleteByServiceAssignmentIdAndEmployeeId(serviceAssignmentId, employeeId);
        serviceAttendanceLogManager.deleteByServiceAssignmentIdAndEmployeeId(serviceAssignmentId,employeeId);
        serviceAttendanceManager.deleteByServiceAssignmentIdAndEmployeeId(serviceAssignmentId,employeeId);
    }
    
    public Boolean initialize() {
        try{
            createServiceAttendances();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createServiceAttendances() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        
    }
    
    public ServiceAttendanceLog convertLog (ServiceAttendance serviceAttendance, UUID transactionId, String action){
        //Gson gson= new Gson();
        //String tmp = gson.toJson(serviceAttendance);
        //ServiceAttendanceLog serviceAttendanceLog = gson.fromJson(tmp,ServiceAttendanceLog.class);
        ServiceAttendanceLog serviceAttendanceLog = new ServiceAttendanceLog();
        serviceAttendanceLog.setEmployee(serviceAttendance.getEmployee());
        serviceAttendanceLog.setEmployeeReplacement(serviceAttendance.getEmployeeReplacement());
        serviceAttendanceLog.setEndDate(serviceAttendance.getEndDate());
        serviceAttendanceLog.setHoursPerDay(serviceAttendance.getHoursPerDay());
        serviceAttendanceLog.setRealEndDate(serviceAttendance.getRealEndDate());
        serviceAttendanceLog.setRealStartDate(serviceAttendance.getRealStartDate());
        serviceAttendanceLog.setRole(serviceAttendance.getRole());
        serviceAttendanceLog.setSalaryParDayAmount(serviceAttendance.getSalaryParDayAmount());
        serviceAttendanceLog.setService(serviceAttendance.getService());
        serviceAttendanceLog.setServiceAssignment(serviceAttendance.getServiceAssignment());
        serviceAttendanceLog.setStartDate(serviceAttendance.getStartDate());
        
        serviceAttendanceLog.setId(null);
        serviceAttendanceLog.setUpdateDate(null);
        serviceAttendanceLog.setTransactionId(transactionId);
        serviceAttendanceLog.setServiceAttendanceId(serviceAttendance.getId());
        serviceAttendanceLog.setAction(action);
        serviceAttendanceLog.setActiveObject(serviceAttendance.getActive());
        return serviceAttendanceLog;
    }
}


