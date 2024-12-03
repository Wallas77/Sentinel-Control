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
import com.digivalle.sentinel.models.ActivityFileLog;
import com.digivalle.sentinel.repositories.ActivityFileLogRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
import java.util.Calendar;

/**
 *
 * @author Waldir.Valle
 */
@Component
public class ActivityFileLogManager {
    
    @Autowired
    private ActivityFileLogRepository activityFileLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public ActivityFileLog getById(UUID id) throws EntityNotExistentException {
        Optional<ActivityFileLog> activityFileLog = activityFileLogRepository.findById(id);
        if (!activityFileLog.isEmpty()) {
            return activityFileLog.get();
        }
        throw new EntityNotExistentException(ActivityFileLog.class,id.toString());
    }
    
    public PagedResponse<ActivityFileLog> getActivityFileLog(ActivityFileLog filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ActivityFileLog> cq = cb.createQuery(ActivityFileLog.class);
        Root<ActivityFileLog> root = cq.from(ActivityFileLog.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<ActivityFileLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<ActivityFileLog> result = query.getResultList();
        
        Page<ActivityFileLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(ActivityFileLog filter, CriteriaBuilder cb, Root<ActivityFileLog> root) {
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
        if(filter.getName()!=null){
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase()+ "%"));
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
        if(filter.getActivityFileId()!=null){
            predicates.add(cb.equal(root.get("activityFileId"), filter.getActivityFileId()));
        }
        if(filter.getTransactionId()!=null){
            predicates.add(cb.equal(root.get("transactionId"), filter.getTransactionId()));
        }
        if(filter.getAction()!=null){
            predicates.add(cb.equal(root.get("action"), filter.getAction()));
        }

        return predicates;
    }

    private void applySorting(CriteriaQuery<ActivityFileLog> cq, CriteriaBuilder cb, Root<ActivityFileLog> root, ActivityFileLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, ActivityFileLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ActivityFileLog> countRoot = countQuery.from(ActivityFileLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    

    public ActivityFileLog createActivityFileLog(ActivityFileLog activityFileLog) throws BusinessLogicException {
        //validateActivityFileLog(activityFileLog);
        //validateUnique(activityFileLog);
        return activityFileLogRepository.save(activityFileLog);
    }

    private void validateActivityFileLog(ActivityFileLog activityFileLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(activityFileLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto ActivityFileLog");
        } else if (StringUtils.isEmpty(activityFileLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto ActivityFileLog");
        } 
    }
    
    private void validateUnique(ActivityFileLog activityFileLog) throws ExistentEntityException {
        List<ActivityFileLog> activityFileLoges = activityFileLogRepository.findByName(activityFileLog.getName());
        if (activityFileLoges!=null && !activityFileLoges.isEmpty()) {
            throw new ExistentEntityException(ActivityFileLog.class,"name="+activityFileLog.getName());
        } 
    }

    public ActivityFileLog updateActivityFileLog(UUID activityFileLogId, ActivityFileLog activityFileLog) throws EntityNotExistentException {
        ActivityFileLog persistedActivityFileLog = getById(activityFileLogId);
        if (persistedActivityFileLog != null) {
            persistedActivityFileLog.setName(activityFileLog.getName());
            return activityFileLogRepository.save(persistedActivityFileLog);
        } else {
            throw new EntityNotExistentException(ActivityFileLog.class,activityFileLogId.toString());
        }
    }

    public void deleteActivityFileLog(UUID activityFileLogId) throws EntityNotExistentException {
        ActivityFileLog activityFileLog = getById(activityFileLogId);
        activityFileLog.setDeleted(Boolean.TRUE);
        activityFileLog.setActive(Boolean.FALSE);
        activityFileLogRepository.save(activityFileLog);
    }

    public List<ActivityFileLog> findAll(){
        return activityFileLogRepository.findAll();
    }
    
    public ActivityFileLog getByName(String name){
        return activityFileLogRepository.getByName(name);
    }
    
    public List<ActivityFileLog> findByNameIgnoreCaseContaining(String name){
        return activityFileLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<ActivityFileLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return activityFileLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}
