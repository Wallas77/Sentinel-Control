package com.digivalle.sentinel.services;



import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.ActivityLogManager;
import com.digivalle.sentinel.managers.ActivityManager;
import com.digivalle.sentinel.models.Activity;
import com.digivalle.sentinel.models.ActivityLog;
import com.digivalle.sentinel.models.RoleResponsability;
import com.digivalle.sentinel.models.ServiceAttendance;
import com.digivalle.sentinel.models.enums.ActivityStatusEnum;
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
public class ActivityService {
    private final static Logger logger = LoggerFactory.getLogger(ActivityService.class);

    @Autowired
    private ActivityManager activityManager;
    
    @Autowired
    private ActivityLogManager activityLogManager;
    
    @Autowired
    private RoleResponsabilityService roleResponsabilityService;
    
    
    public Activity getById(UUID activityId) throws EntityNotExistentException {
        return activityManager.getById(activityId);
    }
    
    public PagedResponse<Activity> getActivity(Activity activity,   Paging paging) {
        return activityManager.getActivity(activity, paging);
    }
    
    public List<Activity> findAll() {
        return activityManager.findAll();
    }
    
    public List<Activity> findFiltered(UUID employeeId, Date activityDate, Date activityDate2, Boolean active,Boolean deleted, UUID roleResponsabilityId){
        return activityManager.findFiltered(employeeId,activityDate,activityDate2,active,deleted,roleResponsabilityId);
    }
    
    public List<Activity> findFilteredServiceAssignment(UUID serviceAssignmentId, UUID employeeId, Date activityDate, Date activityDate2, Boolean active,Boolean deleted, UUID roleResponsabilityId){
        return activityManager.findFilteredServiceAssignment(serviceAssignmentId,employeeId,activityDate,activityDate2,active,deleted,roleResponsabilityId);
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Activity createActivity(Activity activity) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        Activity activityPersisted = activityManager.createActivity(activity);
        activityLogManager.createActivityLog(convertLog(activityPersisted,null,Definitions.LOG_CREATE));
        return getById(activityPersisted.getId());
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void createActivitiesFromServiceAttendance(ServiceAttendance serviceAttendance) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException{
        
        System.out.println("serviceAttendance=>"+serviceAttendance.getStartDate());
        List<RoleResponsability> roleResponsabilities = roleResponsabilityService.findByRoleAndActiveAndDeleted(serviceAttendance.getRole(),Boolean.TRUE,Boolean.FALSE);
        for(RoleResponsability roleResponsability: roleResponsabilities){
            for(int i=0; i<roleResponsability.getRecurrence(); i++) {    
                Activity activity = new Activity();
                activity.setServiceAssignment(serviceAttendance.getServiceAssignment());
                activity.setServiceAttendance(serviceAttendance);
                activity.setService(serviceAttendance.getService());
                activity.setActivityDate(serviceAttendance.getStartDate());
                if(roleResponsability.getEntryTime()!=null){
                    activity.setExactTime(Boolean.TRUE);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(serviceAttendance.getStartDate());
                    cal.set(Calendar.HOUR_OF_DAY, roleResponsability.getEntryTime().getHour());
                    cal.set(Calendar.MINUTE, roleResponsability.getEntryTime().getMinute());
                    activity.setActivityDate(cal.getTime());
                } else {
                    activity.setExactTime(Boolean.FALSE);
                }
                activity.setActivityStatus(ActivityStatusEnum.PROGRAMADA);
                activity.setDescription(roleResponsability.getDescription());
                activity.setEmployee(serviceAttendance.getEmployee());
                activity.setEmployeeBonus(roleResponsability.getEmployeeBonus());
                activity.setName(roleResponsability.getName());
                activity.setRequiredFiles(roleResponsability.getRequiredFiles());
                activity.setRoleResponsability(roleResponsability);
                activity.setUpdateUser(serviceAttendance.getUpdateUser());

                Integer apex = roleResponsability.getRecurrence() / serviceAttendance.getHoursPerDay();
                if(i>0){
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(activity.getActivityDate());
                    cal.add(Calendar.HOUR, apex);
                    activity.setActivityDate(cal.getTime());
                }
                switch (roleResponsability.getTimePeriod()) {
                    case HORA -> {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(activity.getActivityDate());
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        Date dateStart = calendar.getTime();

                        calendar.setTime(activity.getActivityDate());
                        calendar.set(Calendar.HOUR_OF_DAY, 23);
                        calendar.set(Calendar.MINUTE, 59);
                        calendar.set(Calendar.SECOND, 59);
                        calendar.set(Calendar.MILLISECOND, 999);
                        Date dateEnd = calendar.getTime();
                        if(!activity.getExactTime()){
                            for(int j=0; j<serviceAttendance.getHoursPerDay(); j++){
                                List<Activity> activities = findFilteredServiceAssignment(activity.getServiceAssignment().getId(),activity.getEmployee().getId(),dateStart,dateEnd,Boolean.TRUE, Boolean.FALSE,activity.getRoleResponsability().getId());
                                if(activities.isEmpty()){
                                    createActivity(activity);
                                }
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(activity.getActivityDate());
                                cal.add(Calendar.HOUR, 1);
                                activity.setActivityDate(cal.getTime());
                            }
                        } else {
                            List<Activity> activities = findFilteredServiceAssignment(activity.getServiceAssignment().getId(),activity.getEmployee().getId(),dateStart,dateEnd,Boolean.TRUE, Boolean.FALSE,activity.getRoleResponsability().getId());
                            if(activities.isEmpty()){
                                createActivity(activity);
                            }
                        }
                    }
                    case DIA -> {
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(activity.getActivityDate());
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        Date dateStart = calendar.getTime();

                        calendar.setTime(activity.getActivityDate());
                        calendar.set(Calendar.HOUR_OF_DAY, 23);
                        calendar.set(Calendar.MINUTE, 59);
                        calendar.set(Calendar.SECOND, 59);
                        calendar.set(Calendar.MILLISECOND, 999);
                        Date dateEnd = calendar.getTime();
                        if(!activity.getExactTime()){
                            List<Activity> activities = findFilteredServiceAssignment(activity.getServiceAssignment().getId(),activity.getEmployee().getId(),dateStart,dateEnd,Boolean.TRUE, Boolean.FALSE,activity.getRoleResponsability().getId());
                             if(activities.isEmpty() || (!activities.isEmpty() && activities.size()<roleResponsability.getRecurrence())){
                                calendar = Calendar.getInstance();
                                calendar.setTime(activity.getActivityDate());
                                calendar.set(Calendar.HOUR_OF_DAY, 0);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()==null){
                                    createActivity(activity);
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate()) && calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }                         
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()==null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate())){
                                        createActivity(activity);
                                    }
                                } else if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }
                                }
                            }
                        } else {
                            List<Activity> activities = findFilteredServiceAssignment(activity.getServiceAssignment().getId(),activity.getEmployee().getId(),dateStart,dateEnd,Boolean.TRUE, Boolean.FALSE,activity.getRoleResponsability().getId());
                            if(activities.isEmpty() || (!activities.isEmpty() && activities.size()<roleResponsability.getRecurrence())){
                                calendar = Calendar.getInstance();
                                calendar.setTime(activity.getActivityDate());
                                calendar.set(Calendar.HOUR_OF_DAY, 0);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()==null){
                                    createActivity(activity);
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate()) && calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }                         
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()==null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate())){
                                        createActivity(activity);
                                    }
                                } else if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }
                                }
                            }
                        }
                    }
                    case SEMANA -> {
                        
                        Calendar calendar = Calendar.getInstance();
                        List<Activity> activities = findFilteredServiceAssignment(activity.getServiceAssignment().getId(),activity.getEmployee().getId(),serviceAttendance.getServiceAssignment().getStartDate(),serviceAttendance.getServiceAssignment().getEndDate(),Boolean.TRUE, Boolean.FALSE,activity.getRoleResponsability().getId());
                        if(activities.isEmpty()){
                            calendar.setTime(serviceAttendance.getServiceAssignment().getStartDate());
                        } else {
                            calendar.setTime(activities.get(0).getActivityDate());
                            calendar.add(Calendar.DATE, 7);
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            Date dateStart = calendar.getTime();

                            calendar.setTime(activity.getActivityDate());
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            Date dateEnd = calendar.getTime();
                            if(dateEnd.before(dateStart)){
                                calendar.setTime(activities.get(0).getActivityDate());
                            } else {
                                calendar.setTime(activity.getActivityDate());
                            }
                        }
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        Date dateStart = calendar.getTime();

                        calendar.add(Calendar.DATE, 7);
                        calendar.add(Calendar.DAY_OF_YEAR, -1);
                        calendar.set(Calendar.HOUR_OF_DAY, 23);
                        calendar.set(Calendar.MINUTE, 59);
                        calendar.set(Calendar.SECOND, 59);
                        calendar.set(Calendar.MILLISECOND, 999);
                        Date dateEnd = calendar.getTime();

                        if(!activity.getExactTime()){
                            activities = findFilteredServiceAssignment(activity.getServiceAssignment().getId(),activity.getEmployee().getId(),dateStart,dateEnd,Boolean.TRUE, Boolean.FALSE,activity.getRoleResponsability().getId());
                             if(activities.isEmpty() || (!activities.isEmpty() && activities.size()<roleResponsability.getRecurrence())){
                                calendar = Calendar.getInstance();
                                calendar.setTime(activity.getActivityDate());
                                calendar.set(Calendar.HOUR_OF_DAY, 0);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()==null){
                                    createActivity(activity);
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate()) && calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }                         
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()==null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate())){
                                        createActivity(activity);
                                    }
                                } else if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }
                                }
                            }
                        } else {
                            activities = findFilteredServiceAssignment(activity.getServiceAssignment().getId(),activity.getEmployee().getId(),dateStart,dateEnd,Boolean.TRUE, Boolean.FALSE,activity.getRoleResponsability().getId());
                            if(activities.isEmpty() || (!activities.isEmpty() && activities.size()<roleResponsability.getRecurrence())){
                                calendar = Calendar.getInstance();
                                calendar.setTime(activity.getActivityDate());
                                calendar.set(Calendar.HOUR_OF_DAY, 0);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()==null){
                                    createActivity(activity);
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate()) && calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }                         
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()==null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate())){
                                        createActivity(activity);
                                    }
                                } else if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }
                                }
                            }
                        }
                    }
                    case QUINCENA -> {
                        Calendar calendar = Calendar.getInstance();
                        List<Activity> activities = findFilteredServiceAssignment(activity.getServiceAssignment().getId(),activity.getEmployee().getId(),serviceAttendance.getServiceAssignment().getStartDate(),serviceAttendance.getServiceAssignment().getEndDate(),Boolean.TRUE, Boolean.FALSE,activity.getRoleResponsability().getId());
                        if(activities.isEmpty()){
                            calendar.setTime(serviceAttendance.getServiceAssignment().getStartDate());
                        } else {
                            calendar.setTime(activities.get(0).getActivityDate());
                            calendar.add(Calendar.DATE, 15);
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            Date dateStart = calendar.getTime();

                            calendar.setTime(activity.getActivityDate());
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            Date dateEnd = calendar.getTime();
                            if(dateEnd.before(dateStart)){
                                calendar.setTime(activities.get(0).getActivityDate());
                            } else {
                                calendar.setTime(activity.getActivityDate());
                            }
                        }
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        Date dateStart = calendar.getTime();

                        calendar.add(Calendar.DATE, 15);
                        calendar.add(Calendar.DAY_OF_YEAR, -1);
                        calendar.set(Calendar.HOUR_OF_DAY, 23);
                        calendar.set(Calendar.MINUTE, 59);
                        calendar.set(Calendar.SECOND, 59);
                        calendar.set(Calendar.MILLISECOND, 999);
                        Date dateEnd = calendar.getTime();

                        if(!activity.getExactTime()){
                            activities = findFilteredServiceAssignment(activity.getServiceAssignment().getId(),activity.getEmployee().getId(),dateStart,dateEnd,Boolean.TRUE, Boolean.FALSE,activity.getRoleResponsability().getId());
                             if(activities.isEmpty() || (!activities.isEmpty() && activities.size()<roleResponsability.getRecurrence())){
                                calendar = Calendar.getInstance();
                                calendar.setTime(activity.getActivityDate());
                                calendar.set(Calendar.HOUR_OF_DAY, 0);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()==null){
                                    createActivity(activity);
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate()) && calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }                         
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()==null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate())){
                                        createActivity(activity);
                                    }
                                } else if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }
                                }
                            }
                        } else {
                            activities = findFilteredServiceAssignment(activity.getServiceAssignment().getId(),activity.getEmployee().getId(),dateStart,dateEnd,Boolean.TRUE, Boolean.FALSE,activity.getRoleResponsability().getId());
                            if(activities.isEmpty() || (!activities.isEmpty() && activities.size()<roleResponsability.getRecurrence())){
                                calendar = Calendar.getInstance();
                                calendar.setTime(activity.getActivityDate());
                                calendar.set(Calendar.HOUR_OF_DAY, 0);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()==null){
                                    createActivity(activity);
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate()) && calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }                         
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()==null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate())){
                                        createActivity(activity);
                                    }
                                } else if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }
                                }
                            }
                        }
                    }
                    case MES -> {
                        Calendar calendar = Calendar.getInstance();
                        List<Activity> activities = findFilteredServiceAssignment(activity.getServiceAssignment().getId(),activity.getEmployee().getId(),serviceAttendance.getServiceAssignment().getStartDate(),serviceAttendance.getServiceAssignment().getEndDate(),Boolean.TRUE, Boolean.FALSE,activity.getRoleResponsability().getId());
                        if(activities.isEmpty()){
                            calendar.setTime(serviceAttendance.getServiceAssignment().getStartDate());
                        } else {
                            calendar.setTime(activities.get(0).getActivityDate());
                            calendar.add(Calendar.MONTH, 1);
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            Date dateStart = calendar.getTime();

                            calendar.setTime(activity.getActivityDate());
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            Date dateEnd = calendar.getTime();
                            if(dateEnd.before(dateStart)){
                                calendar.setTime(activities.get(0).getActivityDate());
                            } else {
                                calendar.setTime(activity.getActivityDate());
                            }
                        }
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        Date dateStart = calendar.getTime();

                        calendar.add(Calendar.MONTH, 1);
                        calendar.add(Calendar.DAY_OF_YEAR, -1);
                        calendar.set(Calendar.HOUR_OF_DAY, 23);
                        calendar.set(Calendar.MINUTE, 59);
                        calendar.set(Calendar.SECOND, 59);
                        calendar.set(Calendar.MILLISECOND, 999);
                        Date dateEnd = calendar.getTime();

                        if(!activity.getExactTime()){
                            activities = findFilteredServiceAssignment(activity.getServiceAssignment().getId(),activity.getEmployee().getId(),dateStart,dateEnd,Boolean.TRUE, Boolean.FALSE,activity.getRoleResponsability().getId());
                             if(activities.isEmpty() || (!activities.isEmpty() && activities.size()<roleResponsability.getRecurrence())){
                                calendar = Calendar.getInstance();
                                calendar.setTime(activity.getActivityDate());
                                calendar.set(Calendar.HOUR_OF_DAY, 0);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()==null){
                                    createActivity(activity);
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate()) && calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }                         
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()==null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate())){
                                        createActivity(activity);
                                    }
                                } else if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }
                                }
                            }
                        } else {
                            activities = findFilteredServiceAssignment(activity.getServiceAssignment().getId(),activity.getEmployee().getId(),dateStart,dateEnd,Boolean.TRUE, Boolean.FALSE,activity.getRoleResponsability().getId());
                            if(activities.isEmpty() || (!activities.isEmpty() && activities.size()<roleResponsability.getRecurrence())){
                                calendar = Calendar.getInstance();
                                calendar.setTime(activity.getActivityDate());
                                calendar.set(Calendar.HOUR_OF_DAY, 0);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()==null){
                                    createActivity(activity);
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate()) && calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }                         
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()==null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate())){
                                        createActivity(activity);
                                    }
                                } else if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }
                                }
                            }
                        }
                    }
                    case BIMESTRE -> {
                        Calendar calendar = Calendar.getInstance();
                        List<Activity> activities = findFilteredServiceAssignment(activity.getServiceAssignment().getId(),activity.getEmployee().getId(),serviceAttendance.getServiceAssignment().getStartDate(),serviceAttendance.getServiceAssignment().getEndDate(),Boolean.TRUE, Boolean.FALSE,activity.getRoleResponsability().getId());
                        if(activities.isEmpty()){
                            calendar.setTime(serviceAttendance.getServiceAssignment().getStartDate());
                        } else {
                            calendar.setTime(activities.get(0).getActivityDate());
                            calendar.add(Calendar.MONTH, 2);
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            Date dateStart = calendar.getTime();

                            calendar.setTime(activity.getActivityDate());
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            Date dateEnd = calendar.getTime();
                            if(dateEnd.before(dateStart)){
                                calendar.setTime(activities.get(0).getActivityDate());
                            } else {
                                calendar.setTime(activity.getActivityDate());
                            }
                        }
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        Date dateStart = calendar.getTime();

                        calendar.add(Calendar.MONTH, 2);
                        calendar.add(Calendar.DAY_OF_YEAR, -1);
                        calendar.set(Calendar.HOUR_OF_DAY, 23);
                        calendar.set(Calendar.MINUTE, 59);
                        calendar.set(Calendar.SECOND, 59);
                        calendar.set(Calendar.MILLISECOND, 999);
                        Date dateEnd = calendar.getTime();

                        if(!activity.getExactTime()){
                            activities = findFilteredServiceAssignment(activity.getServiceAssignment().getId(),activity.getEmployee().getId(),dateStart,dateEnd,Boolean.TRUE, Boolean.FALSE,activity.getRoleResponsability().getId());
                             if(activities.isEmpty() || (!activities.isEmpty() && activities.size()<roleResponsability.getRecurrence())){
                                calendar = Calendar.getInstance();
                                calendar.setTime(activity.getActivityDate());
                                calendar.set(Calendar.HOUR_OF_DAY, 0);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()==null){
                                    createActivity(activity);
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate()) && calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }                         
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()==null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate())){
                                        createActivity(activity);
                                    }
                                } else if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }
                                }
                            }
                        } else {
                            activities = findFilteredServiceAssignment(activity.getServiceAssignment().getId(),activity.getEmployee().getId(),dateStart,dateEnd,Boolean.TRUE, Boolean.FALSE,activity.getRoleResponsability().getId());
                            if(activities.isEmpty() || (!activities.isEmpty() && activities.size()<roleResponsability.getRecurrence())){
                                calendar = Calendar.getInstance();
                                calendar.setTime(activity.getActivityDate());
                                calendar.set(Calendar.HOUR_OF_DAY, 0);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()==null){
                                    createActivity(activity);
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate()) && calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }                         
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()==null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate())){
                                        createActivity(activity);
                                    }
                                } else if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }
                                }
                            }
                        }
                    }
                    case TRIMESTRE -> {
                        Calendar calendar = Calendar.getInstance();
                        List<Activity> activities = findFilteredServiceAssignment(activity.getServiceAssignment().getId(),activity.getEmployee().getId(),serviceAttendance.getServiceAssignment().getStartDate(),serviceAttendance.getServiceAssignment().getEndDate(),Boolean.TRUE, Boolean.FALSE,activity.getRoleResponsability().getId());
                        if(activities.isEmpty()){
                            calendar.setTime(serviceAttendance.getServiceAssignment().getStartDate());
                        } else {
                            calendar.setTime(activities.get(0).getActivityDate());
                            calendar.add(Calendar.MONTH, 3);
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            Date dateStart = calendar.getTime();

                            calendar.setTime(activity.getActivityDate());
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            Date dateEnd = calendar.getTime();
                            if(dateEnd.before(dateStart)){
                                calendar.setTime(activities.get(0).getActivityDate());
                            } else {
                                calendar.setTime(activity.getActivityDate());
                            }
                        }
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        Date dateStart = calendar.getTime();

                        calendar.add(Calendar.MONTH, 3);
                        calendar.add(Calendar.DAY_OF_YEAR, -1);
                        calendar.set(Calendar.HOUR_OF_DAY, 23);
                        calendar.set(Calendar.MINUTE, 59);
                        calendar.set(Calendar.SECOND, 59);
                        calendar.set(Calendar.MILLISECOND, 999);
                        Date dateEnd = calendar.getTime();

                        if(!activity.getExactTime()){
                            activities = findFilteredServiceAssignment(activity.getServiceAssignment().getId(),activity.getEmployee().getId(),dateStart,dateEnd,Boolean.TRUE, Boolean.FALSE,activity.getRoleResponsability().getId());
                             if(activities.isEmpty() || (!activities.isEmpty() && activities.size()<roleResponsability.getRecurrence())){
                                calendar = Calendar.getInstance();
                                calendar.setTime(activity.getActivityDate());
                                calendar.set(Calendar.HOUR_OF_DAY, 0);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()==null){
                                    createActivity(activity);
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate()) && calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }                         
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()==null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate())){
                                        createActivity(activity);
                                    }
                                } else if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }
                                }
                            }
                        } else {
                            activities = findFilteredServiceAssignment(activity.getServiceAssignment().getId(),activity.getEmployee().getId(),dateStart,dateEnd,Boolean.TRUE, Boolean.FALSE,activity.getRoleResponsability().getId());
                            if(activities.isEmpty() || (!activities.isEmpty() && activities.size()<roleResponsability.getRecurrence())){
                                calendar = Calendar.getInstance();
                                calendar.setTime(activity.getActivityDate());
                                calendar.set(Calendar.HOUR_OF_DAY, 0);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()==null){
                                    createActivity(activity);
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate()) && calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }                         
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()==null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate())){
                                        createActivity(activity);
                                    }
                                } else if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }
                                }
                            }
                        }
                    }
                    case SEMESTRE -> {
                        Calendar calendar = Calendar.getInstance();
                        List<Activity> activities = findFilteredServiceAssignment(activity.getServiceAssignment().getId(),activity.getEmployee().getId(),serviceAttendance.getServiceAssignment().getStartDate(),serviceAttendance.getServiceAssignment().getEndDate(),Boolean.TRUE, Boolean.FALSE,activity.getRoleResponsability().getId());
                        if(activities.isEmpty()){
                            calendar.setTime(serviceAttendance.getServiceAssignment().getStartDate());
                        } else {
                            calendar.setTime(activities.get(0).getActivityDate());
                            calendar.add(Calendar.MONTH, 6);
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            Date dateStart = calendar.getTime();

                            calendar.setTime(activity.getActivityDate());
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            Date dateEnd = calendar.getTime();
                            if(dateEnd.before(dateStart)){
                                calendar.setTime(activities.get(0).getActivityDate());
                            } else {
                                calendar.setTime(activity.getActivityDate());
                            }
                        }
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        Date dateStart = calendar.getTime();

                        calendar.add(Calendar.MONTH, 6);
                        calendar.add(Calendar.DAY_OF_YEAR, -1);
                        calendar.set(Calendar.HOUR_OF_DAY, 23);
                        calendar.set(Calendar.MINUTE, 59);
                        calendar.set(Calendar.SECOND, 59);
                        calendar.set(Calendar.MILLISECOND, 999);
                        Date dateEnd = calendar.getTime();

                        if(!activity.getExactTime()){
                            activities = findFilteredServiceAssignment(activity.getServiceAssignment().getId(),activity.getEmployee().getId(),dateStart,dateEnd,Boolean.TRUE, Boolean.FALSE,activity.getRoleResponsability().getId());
                             if(activities.isEmpty() || (!activities.isEmpty() && activities.size()<roleResponsability.getRecurrence())){
                                calendar = Calendar.getInstance();
                                calendar.setTime(activity.getActivityDate());
                                calendar.set(Calendar.HOUR_OF_DAY, 0);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()==null){
                                    createActivity(activity);
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate()) && calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }                         
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()==null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate())){
                                        createActivity(activity);
                                    }
                                } else if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }
                                }
                            }
                        } else {
                            activities = findFilteredServiceAssignment(activity.getServiceAssignment().getId(),activity.getEmployee().getId(),dateStart,dateEnd,Boolean.TRUE, Boolean.FALSE,activity.getRoleResponsability().getId());
                            if(activities.isEmpty() || (!activities.isEmpty() && activities.size()<roleResponsability.getRecurrence())){
                                calendar = Calendar.getInstance();
                                calendar.setTime(activity.getActivityDate());
                                calendar.set(Calendar.HOUR_OF_DAY, 0);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()==null){
                                    createActivity(activity);
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate()) && calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }                         
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()==null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate())){
                                        createActivity(activity);
                                    }
                                } else if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }
                                }
                            }
                        }
                    }
                    
                    case ANUAL -> {
                        Calendar calendar = Calendar.getInstance();
                        List<Activity> activities = findFilteredServiceAssignment(activity.getServiceAssignment().getId(),activity.getEmployee().getId(),serviceAttendance.getServiceAssignment().getStartDate(),serviceAttendance.getServiceAssignment().getEndDate(),Boolean.TRUE, Boolean.FALSE,activity.getRoleResponsability().getId());
                        if(activities.isEmpty()){
                            calendar.setTime(serviceAttendance.getServiceAssignment().getStartDate());
                        } else {
                            calendar.setTime(activities.get(0).getActivityDate());
                            calendar.add(Calendar.MONTH, 12);
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            Date dateStart = calendar.getTime();

                            calendar.setTime(activity.getActivityDate());
                            calendar.set(Calendar.HOUR_OF_DAY, 0);
                            calendar.set(Calendar.MINUTE, 0);
                            calendar.set(Calendar.SECOND, 0);
                            calendar.set(Calendar.MILLISECOND, 0);
                            Date dateEnd = calendar.getTime();
                            if(dateEnd.before(dateStart)){
                                calendar.setTime(activities.get(0).getActivityDate());
                            } else {
                                calendar.setTime(activity.getActivityDate());
                            }
                        }
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        Date dateStart = calendar.getTime();

                        calendar.add(Calendar.MONTH, 12);
                        calendar.add(Calendar.DAY_OF_YEAR, -1);
                        calendar.set(Calendar.HOUR_OF_DAY, 23);
                        calendar.set(Calendar.MINUTE, 59);
                        calendar.set(Calendar.SECOND, 59);
                        calendar.set(Calendar.MILLISECOND, 999);
                        Date dateEnd = calendar.getTime();

                        if(!activity.getExactTime()){
                            activities = findFilteredServiceAssignment(activity.getServiceAssignment().getId(),activity.getEmployee().getId(),dateStart,dateEnd,Boolean.TRUE, Boolean.FALSE,activity.getRoleResponsability().getId());
                             if(activities.isEmpty() || (!activities.isEmpty() && activities.size()<roleResponsability.getRecurrence())){
                                calendar = Calendar.getInstance();
                                calendar.setTime(activity.getActivityDate());
                                calendar.set(Calendar.HOUR_OF_DAY, 0);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()==null){
                                    createActivity(activity);
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate()) && calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }                         
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()==null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate())){
                                        createActivity(activity);
                                    }
                                } else if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }
                                }
                            }
                        } else {
                            activities = findFilteredServiceAssignment(activity.getServiceAssignment().getId(),activity.getEmployee().getId(),dateStart,dateEnd,Boolean.TRUE, Boolean.FALSE,activity.getRoleResponsability().getId());
                            if(activities.isEmpty() || (!activities.isEmpty() && activities.size()<roleResponsability.getRecurrence())){
                                calendar = Calendar.getInstance();
                                calendar.setTime(activity.getActivityDate());
                                calendar.set(Calendar.HOUR_OF_DAY, 0);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()==null){
                                    createActivity(activity);
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate()) && calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }                         
                                } else if(roleResponsability.getStartDate()!=null && roleResponsability.getEndDate()==null){
                                    if(calendar.getTime().after(roleResponsability.getStartDate())){
                                        createActivity(activity);
                                    }
                                } else if(roleResponsability.getStartDate()==null && roleResponsability.getEndDate()!=null){
                                    if(calendar.getTime().before(roleResponsability.getEndDate())){
                                        createActivity(activity);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    
    
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Activity updateActivity(UUID activityId,Activity activity) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        Activity activityPersisted = activityManager.updateActivity(activityId, activity);
        activityLogManager.createActivityLog(convertLog(activityPersisted,null,Definitions.LOG_UPDATE));
        return getById(activityPersisted.getId());
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteActivity(UUID activityId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        Activity activityPersisted = activityManager.deleteActivity(activityId, updateUser);
        activityLogManager.createActivityLog(convertLog(activityPersisted,null,Definitions.LOG_DELETE));
    }  
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteByRoleResponsabilityIdAndEmployeeId(UUID roleResponsabilityId, UUID employeeId){
        activityLogManager.deleteByRoleResponsabilityIdAndEmployeeId(roleResponsabilityId, employeeId);
        activityManager.deleteByRoleResponsabilityIdAndEmployeeId(roleResponsabilityId, employeeId);
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteByServiceAttendanceIdAndEmployeeId(UUID serviceAttendanceId, UUID employeeId){
        activityLogManager.deleteByServiceAttendanceIdAndEmployeeId(serviceAttendanceId, employeeId);
        activityManager.deleteByServiceAttendanceIdAndEmployeeId(serviceAttendanceId, employeeId);
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteByServiceAssignmentId(UUID serviceAssignmentId){
        activityLogManager.deleteByServiceAssignmentId(serviceAssignmentId);
        activityManager.deleteByServiceAssignmentId(serviceAssignmentId);
    }
    
    public void deleteByServiceAssignmentIdAndEmployeeId(UUID serviceAssignmentId, UUID employeeId){
        activityLogManager.deleteByServiceAssignmentIdAndEmployeeId(serviceAssignmentId,employeeId);
        activityManager.deleteByServiceAssignmentIdAndEmployeeId(serviceAssignmentId,employeeId);
    }
    
    public Boolean initialize() {
        try{
            createActivitys();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createActivitys() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        List<Activity> activityes = findAll();
        if(activityes.isEmpty()){
            Activity activity = new Activity();
            activity.setName(Definitions.APPLICATION_SENTINEL);
            activity.setDescription(Definitions.APPLICATION_SENTINEL_DESC);
            activity.setUpdateUser(Definitions.USER_DEFAULT);
            createActivity(activity);
            
                        logger.info("Las Activityes no existen, inicialización ejecutada");
        } else {
            logger.info("Las Activityes ya existen, inicialización no ejecutada");
        }
    }
    
    public ActivityLog convertLog (Activity activity, UUID transactionId, String action){
        /*Gson gson= new Gson();
        String tmp = gson.toJson(activity);
        ActivityLog activityLog = gson.fromJson(tmp,ActivityLog.class);*/
        ActivityLog activityLog = new ActivityLog();
        activityLog.setActivityDate(activity.getActivityDate());
        activityLog.setActivityStatus(activity.getActivityStatus());
        activityLog.setCanceledDate(activity.getCanceledDate());
        activityLog.setDescription(activity.getDescription());
        activityLog.setEmployee(activity.getEmployee());
        activityLog.setEmployeeBonus(activity.getEmployeeBonus());
        activityLog.setEndDate(activity.getEndDate());
        activityLog.setExactTime(activity.getExactTime());
        activityLog.setName(activity.getName());
        activityLog.setRequiredFiles(activity.getRequiredFiles());
        activityLog.setRoleResponsability(activity.getRoleResponsability());
        activityLog.setService(activity.getService());
        activityLog.setServiceAssignment(activity.getServiceAssignment());
        activityLog.setServiceAttendance(activity.getServiceAttendance());
        activityLog.setStartDate(activity.getStartDate());
        
        activityLog.setId(null);
        activityLog.setUpdateDate(null);
        activityLog.setTransactionId(transactionId);
        activityLog.setActivityId(activity.getId());
        activityLog.setAction(action);
        activityLog.setActiveObject(activity.getActive());
        return activityLog;
    }
}


