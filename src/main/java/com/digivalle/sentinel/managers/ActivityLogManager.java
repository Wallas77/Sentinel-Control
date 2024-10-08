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
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.models.ActivityLog;
import com.digivalle.sentinel.repositories.ActivityLogRepository;
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
import org.apache.commons.lang3.StringUtils;
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
public class ActivityLogManager {
    
    @Autowired
    private ActivityLogRepository activityLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public ActivityLog getById(UUID id) throws EntityNotExistentException {
        Optional<ActivityLog> activityLog = activityLogRepository.findById(id);
        if (!activityLog.isEmpty()) {
            return activityLog.get();
        }
        throw new EntityNotExistentException(ActivityLog.class,id.toString());
    }
    
    public PagedResponse<ActivityLog> getActivityLog(ActivityLog filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ActivityLog> cq = cb.createQuery(ActivityLog.class);
        Root<ActivityLog> root = cq.from(ActivityLog.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<ActivityLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<ActivityLog> result = query.getResultList();
        
        Page<ActivityLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(ActivityLog filter, CriteriaBuilder cb, Root<ActivityLog> root) {
        List<Predicate> predicates = new ArrayList<>();

        if(filter.getCreationDate()!=null && filter.getCreationDate2()!=null){
            predicates.add(cb.between(root.get("creationDate"), filter.getCreationDate(),filter.getCreationDate2()));
        }
        if(filter.getUpdateDate()!=null && filter.getUpdateDate2()!=null){
            predicates.add(cb.between(root.get("updateDate"), filter.getUpdateDate(),filter.getUpdateDate2()));
        }
       
        if(filter.getName()!=null){
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase()+ "%"));
        }
        if(filter.getDescription()!=null){
            predicates.add(cb.like(cb.lower(root.get("description")), "%" + filter.getDescription().toLowerCase()+ "%"));
        }
        if(filter.getActivityDate()!=null && filter.getActivityDate2()!=null){
            predicates.add(cb.between(root.get("activityDate"), filter.getActivityDate(),filter.getActivityDate2()));
        }
        if(filter.getStartDate()!=null && filter.getStartDate2()!=null){
            predicates.add(cb.between(root.get("startDate"), filter.getStartDate(),filter.getStartDate2()));
        }
        if(filter.getEndDate()!=null && filter.getEndDate2()!=null){
            predicates.add(cb.between(root.get("endDate"), filter.getEndDate(),filter.getEndDate2()));
        }
        if(filter.getCanceledDate()!=null && filter.getCanceledDate2()!=null){
            predicates.add(cb.between(root.get("canceledDate"), filter.getCanceledDate(),filter.getCanceledDate2()));
        }
        if(filter.getRequiredFiles()!=null){
            predicates.add(cb.equal(root.get("requiredFiles"), filter.getRequiredFiles()));
        }
        if(filter.getRoleResponsability()!=null){
            if(filter.getRoleResponsability().getId()!=null){
                predicates.add(cb.equal(root.get("roleResponsability").get("id"), filter.getRoleResponsability().getId()));
            }
            if(filter.getRoleResponsability().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("roleResponsability").get("name")), "%" + filter.getRoleResponsability().getName().toLowerCase()+ "%"));
            }
        }
        if(filter.getEmployee()!=null){
            if(filter.getEmployee().getId()!=null){
                predicates.add(cb.equal(root.get("employee").get("id"), filter.getEmployee().getId()));
            }
            if(filter.getEmployee().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("employee").get("name")), "%" + filter.getEmployee().getName().toLowerCase()+ "%"));
            }
        }
        if(filter.getEmployeeBonus()!=null){
            predicates.add(cb.equal(root.get("employeeBonus"), filter.getEmployeeBonus()));
        }
        if(filter.getService()!=null){
            if(filter.getService().getId()!=null){
                predicates.add(cb.equal(root.get("service").get("id"), filter.getService().getId()));
            }
            if(filter.getService().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("service").get("name")), "%" + filter.getService().getName().toLowerCase()+ "%"));
            }
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
        if(filter.getActivityId()!=null){
            predicates.add(cb.equal(root.get("activityId"), filter.getActivityId()));
        }
        if(filter.getTransactionId()!=null){
            predicates.add(cb.equal(root.get("transactionId"), filter.getTransactionId()));
        }
        if(filter.getAction()!=null){
            predicates.add(cb.equal(root.get("action"), filter.getAction()));
        }

        return predicates;
    }

    private void applySorting(CriteriaQuery<ActivityLog> cq, CriteriaBuilder cb, Root<ActivityLog> root, ActivityLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, ActivityLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ActivityLog> countRoot = countQuery.from(ActivityLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    

    public ActivityLog createActivityLog(ActivityLog activityLog) throws BusinessLogicException {
        //validateActivityLog(activityLog);
        //validateUnique(activityLog);
        return activityLogRepository.save(activityLog);
    }

    private void validateActivityLog(ActivityLog activityLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(activityLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto ActivityLog");
        } else if (StringUtils.isEmpty(activityLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto ActivityLog");
        } 
    }
    
    private void validateUnique(ActivityLog activityLog) throws ExistentEntityException {
        List<ActivityLog> activityLoges = activityLogRepository.findByName(activityLog.getName());
        if (activityLoges!=null && !activityLoges.isEmpty()) {
            throw new ExistentEntityException(ActivityLog.class,"name="+activityLog.getName());
        } 
    }

    public ActivityLog updateActivityLog(UUID activityLogId, ActivityLog activityLog) throws EntityNotExistentException {
        ActivityLog persistedActivityLog = getById(activityLogId);
        if (persistedActivityLog != null) {
            persistedActivityLog.setName(activityLog.getName());
            return activityLogRepository.save(persistedActivityLog);
        } else {
            throw new EntityNotExistentException(ActivityLog.class,activityLogId.toString());
        }
    }

    public void deleteActivityLog(UUID activityLogId) throws EntityNotExistentException {
        ActivityLog activityLog = getById(activityLogId);
        activityLog.setDeleted(Boolean.TRUE);
        activityLog.setActive(Boolean.FALSE);
        activityLogRepository.save(activityLog);
    }

    public List<ActivityLog> findAll(){
        return activityLogRepository.findAll();
    }
    
    public ActivityLog getByName(String name){
        return activityLogRepository.getByName(name);
    }
    
    public List<ActivityLog> findByNameIgnoreCaseContaining(String name){
        return activityLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<ActivityLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return activityLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}
