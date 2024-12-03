/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.managers;




import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.models.ServiceAttendanceLog;
import com.digivalle.sentinel.repositories.ServiceAttendanceLogRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 *
 * @author Waldir.Valle
 */
@Component
public class ServiceAttendanceLogManager {
    
    @Autowired
    private ServiceAttendanceLogRepository serviceAttendanceLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public ServiceAttendanceLog getById(UUID id) throws EntityNotExistentException {
        Optional<ServiceAttendanceLog> serviceAttendanceLog = serviceAttendanceLogRepository.findById(id);
        if (!serviceAttendanceLog.isEmpty()) {
            return serviceAttendanceLog.get();
        }
        throw new EntityNotExistentException(ServiceAttendanceLog.class,id.toString());
    }
    
    public PagedResponse<ServiceAttendanceLog> getServiceAttendanceLog(ServiceAttendanceLog filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ServiceAttendanceLog> cq = cb.createQuery(ServiceAttendanceLog.class);
        Root<ServiceAttendanceLog> root = cq.from(ServiceAttendanceLog.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<ServiceAttendanceLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<ServiceAttendanceLog> result = query.getResultList();
        
        Page<ServiceAttendanceLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(ServiceAttendanceLog filter, CriteriaBuilder cb, Root<ServiceAttendanceLog> root) {
        List<Predicate> predicates = new ArrayList<>();

        if(filter.getCreationDate()!=null && filter.getCreationDate2()!=null){
            Calendar cal = Calendar.getInstance();
            cal.setTime(filter.getCreationDate());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            filter.setCreationDate(cal.getTime());
            
            cal = Calendar.getInstance();
            cal.setTime(filter.getCreationDate2());
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            filter.setCreationDate2(cal.getTime());
            predicates.add(cb.between(root.get("creationDate"), filter.getCreationDate(),filter.getCreationDate2()));
        }
        if(filter.getUpdateDate()!=null && filter.getUpdateDate2()!=null){
            Calendar cal = Calendar.getInstance();
            cal.setTime(filter.getUpdateDate());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            filter.setUpdateDate(cal.getTime());
            
            cal = Calendar.getInstance();
            cal.setTime(filter.getUpdateDate2());
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            filter.setUpdateDate2(cal.getTime());
            predicates.add(cb.between(root.get("updateDate"), filter.getUpdateDate(),filter.getUpdateDate2()));
        }
        
        if(filter.getEmployee()!=null){
            if(filter.getEmployee().getId()!=null){
                predicates.add(cb.equal(root.get("employee").get("id"), filter.getEmployee().getId()));
            }
            if(filter.getEmployee().getSerial()!=null){
                predicates.add(cb.equal(root.get("employee").get("serial"), filter.getEmployee().getSerial()));
            }
            if(filter.getEmployee().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("employee").get("name")), "%" + filter.getEmployee().getName().toLowerCase()+ "%"));
            }
        }
        if(filter.getRole()!=null){
            if(filter.getRole().getId()!=null){
                predicates.add(cb.equal(root.get("role").get("id"), filter.getRole().getId()));
            }
            if(filter.getRole().getSerial()!=null){
                predicates.add(cb.equal(root.get("role").get("serial"), filter.getRole().getSerial()));
            }
            if(filter.getRole().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("role").get("name")), "%" + filter.getRole().getName().toLowerCase()+ "%"));
            }
        }
        if(filter.getService()!=null){
            if(filter.getService().getId()!=null){
                predicates.add(cb.equal(root.get("service").get("id"), filter.getService().getId()));
            }
            if(filter.getService().getSerial()!=null){
                predicates.add(cb.equal(root.get("service").get("serial"), filter.getService().getSerial()));
            }
            if(filter.getService().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("service").get("name")), "%" + filter.getService().getName().toLowerCase()+ "%"));
            }
        }
        if(filter.getSalaryParDayAmount()!=null && filter.getSalaryParDayAmount2()!=null){
            predicates.add(cb.between(root.get("salaryParDayAmount"), filter.getSalaryParDayAmount(),filter.getSalaryParDayAmount2()));
        }
        if(filter.getHoursPerDay()!=null){
            predicates.add(cb.equal(root.get("hoursPerDay"), filter.getHoursPerDay()));
        }
        if(filter.getStartDate()!=null && filter.getStartDate2()!=null){
            predicates.add(cb.between(root.get("startDate"), filter.getStartDate(),filter.getStartDate2()));
        }
        if(filter.getEndDate()!=null && filter.getEndDate2()!=null){
            predicates.add(cb.between(root.get("endDate"), filter.getEndDate(),filter.getEndDate2()));
        }
        if(filter.getRealStartDate()!=null && filter.getRealStartDate2()!=null){
            predicates.add(cb.between(root.get("realStartDate"), filter.getRealStartDate(),filter.getRealStartDate2()));
        }
        if(filter.getRealEndDate()!=null && filter.getRealEndDate2()!=null){
            predicates.add(cb.between(root.get("realEndDate"), filter.getRealEndDate(),filter.getRealEndDate2()));
        }
        if(filter.getActive()!=null){
            predicates.add(cb.equal(root.get("active"), filter.getActive()));
        }
        if(filter.getDeleted()!=null){
            predicates.add(cb.equal(root.get("deleted"), filter.getDeleted()));
        }
        if(filter.getUpdateUser()!=null){
            predicates.add(cb.equal(root.get("updateUser"), filter.getUpdateUser()));
        }
        if(filter.getServiceAttendanceId()!=null){
            predicates.add(cb.equal(root.get("serviceAttendanceId"), filter.getServiceAttendanceId()));
        }
        if(filter.getTransactionId()!=null){
            predicates.add(cb.equal(root.get("transactionId"), filter.getTransactionId()));
        }
        if(filter.getAction()!=null){
            predicates.add(cb.equal(root.get("action"), filter.getAction()));
        }
        if (filter.getEmployee().getUser() != null) {
                if (filter.getEmployee().getUser().getId() != null) {
                    predicates.add(cb.equal(root.get("employee").get("user").get("id"), filter.getEmployee().getUser().getId()));
                }
                if (filter.getEmployee().getUser().getEmail() != null) {
                    predicates.add(cb.like(cb.lower(root.get("employee").get("user").get("email")), "%" + filter.getEmployee().getUser().getEmail().toLowerCase() + "%"));
                }
            }

        return predicates;
    }

    private void applySorting(CriteriaQuery<ServiceAttendanceLog> cq, CriteriaBuilder cb, Root<ServiceAttendanceLog> root, ServiceAttendanceLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, ServiceAttendanceLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ServiceAttendanceLog> countRoot = countQuery.from(ServiceAttendanceLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public ServiceAttendanceLog createServiceAttendanceLog(ServiceAttendanceLog serviceAttendanceLog) throws BusinessLogicException {
        //validateServiceAttendanceLog(serviceAttendanceLog);
        //validateUnique(serviceAttendanceLog);
        return serviceAttendanceLogRepository.save(serviceAttendanceLog);
    }

    public ServiceAttendanceLog updateServiceAttendanceLog(UUID serviceAttendanceLogId, ServiceAttendanceLog serviceAttendanceLog) throws EntityNotExistentException {
        ServiceAttendanceLog persistedServiceAttendanceLog = getById(serviceAttendanceLogId);
        if (persistedServiceAttendanceLog != null) {
            persistedServiceAttendanceLog.setService(serviceAttendanceLog.getService());
            return serviceAttendanceLogRepository.save(persistedServiceAttendanceLog);
        } else {
            throw new EntityNotExistentException(ServiceAttendanceLog.class,serviceAttendanceLogId.toString());
        }
    }

    public void deleteServiceAttendanceLog(UUID serviceAttendanceLogId) throws EntityNotExistentException {
        ServiceAttendanceLog serviceAttendanceLog = getById(serviceAttendanceLogId);
        serviceAttendanceLog.setDeleted(Boolean.TRUE);
        serviceAttendanceLog.setActive(Boolean.FALSE);
        serviceAttendanceLogRepository.save(serviceAttendanceLog);
    }
    
    public void deleteByServiceAssignmentId(UUID serviceAssignmentId){
        serviceAttendanceLogRepository.deleteByServiceAssignmentId(serviceAssignmentId);
    }
    
    public void deleteByServiceAssignmentIdAndEmployeeId(UUID serviceAssignmentId, UUID employeeId){
        serviceAttendanceLogRepository.deleteByServiceAssignmentIdAndEmployeeId(serviceAssignmentId, employeeId);
    }

    public List<ServiceAttendanceLog> findAll(){
        return serviceAttendanceLogRepository.findAll();
    }
    
    
    
}
