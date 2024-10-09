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
import com.digivalle.sentinel.models.ServiceAssignmentLog;
import com.digivalle.sentinel.repositories.ServiceAssignmentLogRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
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
public class ServiceAssignmentLogManager {
    
    @Autowired
    private ServiceAssignmentLogRepository serviceAssignmentLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public ServiceAssignmentLog getById(UUID id) throws EntityNotExistentException {
        Optional<ServiceAssignmentLog> serviceAssignmentLog = serviceAssignmentLogRepository.findById(id);
        if (!serviceAssignmentLog.isEmpty()) {
            return serviceAssignmentLog.get();
        }
        throw new EntityNotExistentException(ServiceAssignmentLog.class,id.toString());
    }
    
    public PagedResponse<ServiceAssignmentLog> getServiceAssignmentLog(ServiceAssignmentLog filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ServiceAssignmentLog> cq = cb.createQuery(ServiceAssignmentLog.class);
        Root<ServiceAssignmentLog> root = cq.from(ServiceAssignmentLog.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<ServiceAssignmentLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<ServiceAssignmentLog> result = query.getResultList();
        
        Page<ServiceAssignmentLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(ServiceAssignmentLog filter, CriteriaBuilder cb, Root<ServiceAssignmentLog> root) {
        List<Predicate> predicates = new ArrayList<>();

        if(filter.getCreationDate()!=null && filter.getCreationDate2()!=null){
            predicates.add(cb.between(root.get("creationDate"), filter.getCreationDate(),filter.getCreationDate2()));
        }
        if(filter.getUpdateDate()!=null && filter.getUpdateDate2()!=null){
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
        if(filter.getRecurrenceInDays()!=null){
            predicates.add(cb.equal(root.get("recurrenceInDays"), filter.getRecurrenceInDays()));
        }
        if(filter.getRecurrencePerWeekDays()!=null){
            predicates.add(cb.like(cb.lower(root.get("recurrencePerWeekDays")), "%" + filter.getRecurrencePerWeekDays().toLowerCase()+ "%"));
        }
        if(filter.getRecurrencePerNumberDays()!=null){
            predicates.add(cb.like(cb.lower(root.get("recurrencePerNumberDays")), "%" + filter.getRecurrencePerNumberDays().toLowerCase()+ "%"));
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
        if(filter.getServiceAssignmentId()!=null){
            predicates.add(cb.equal(root.get("serviceAssignmentId"), filter.getServiceAssignmentId()));
        }
        if(filter.getTransactionId()!=null){
            predicates.add(cb.equal(root.get("transactionId"), filter.getTransactionId()));
        }
        if(filter.getAction()!=null){
            predicates.add(cb.equal(root.get("action"), filter.getAction()));
        }

        return predicates;
    }

    private void applySorting(CriteriaQuery<ServiceAssignmentLog> cq, CriteriaBuilder cb, Root<ServiceAssignmentLog> root, ServiceAssignmentLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, ServiceAssignmentLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ServiceAssignmentLog> countRoot = countQuery.from(ServiceAssignmentLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public ServiceAssignmentLog createServiceAssignmentLog(ServiceAssignmentLog serviceAssignmentLog) throws BusinessLogicException {
        //validateServiceAssignmentLog(serviceAssignmentLog);
        //validateUnique(serviceAssignmentLog);
        return serviceAssignmentLogRepository.save(serviceAssignmentLog);
    }

    public ServiceAssignmentLog updateServiceAssignmentLog(UUID serviceAssignmentLogId, ServiceAssignmentLog serviceAssignmentLog) throws EntityNotExistentException {
        ServiceAssignmentLog persistedServiceAssignmentLog = getById(serviceAssignmentLogId);
        if (persistedServiceAssignmentLog != null) {
            persistedServiceAssignmentLog.setService(serviceAssignmentLog.getService());
            return serviceAssignmentLogRepository.save(persistedServiceAssignmentLog);
        } else {
            throw new EntityNotExistentException(ServiceAssignmentLog.class,serviceAssignmentLogId.toString());
        }
    }

    public void deleteServiceAssignmentLog(UUID serviceAssignmentLogId) throws EntityNotExistentException {
        ServiceAssignmentLog serviceAssignmentLog = getById(serviceAssignmentLogId);
        serviceAssignmentLog.setDeleted(Boolean.TRUE);
        serviceAssignmentLog.setActive(Boolean.FALSE);
        serviceAssignmentLogRepository.save(serviceAssignmentLog);
    }

    public List<ServiceAssignmentLog> findAll(){
        return serviceAssignmentLogRepository.findAll();
    }
    
    
    
}
