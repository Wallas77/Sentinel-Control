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
        //cq.orderBy(cb.asc(root.get("id")));

        List<Predicate> predicates = new ArrayList<Predicate>();
        cq.orderBy(cb.desc(root.get("creationDate")));
        if(filter.getCreationDate()!=null && filter.getCreationDate2()!=null){
            predicates.add(cb.between(root.get("creationDate"), filter.getCreationDate(),filter.getCreationDate2()));
        }
        if(filter.getUpdateDate()!=null && filter.getUpdateDate2()!=null){
            predicates.add(cb.between(root.get("updateDate"), filter.getUpdateDate(),filter.getUpdateDate2()));
            cq.orderBy(cb.desc(root.get("updateDate")));
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
        if(filter.getActivityId()!=null){
            predicates.add(cb.equal(root.get("activityId"), filter.getActivityId()));
        }
        if(filter.getTransactionId()!=null){
            predicates.add(cb.equal(root.get("transactionId"), filter.getTransactionId()));
        }
        if(filter.getAction()!=null){
            predicates.add(cb.equal(root.get("action"), filter.getAction()));
        }
        
        cq.select(root);
        if(predicates.size()>0){
            cq.where(predicates.toArray(new Predicate[0]));
        }
        
        TypedQuery<ActivityLog> query = entityManager.createQuery(cq);
        
        int iTotal = query.getResultList().size();

        
        
        List<ActivityLog> result = query.setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        
        Page<ActivityLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<ActivityLog>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
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
