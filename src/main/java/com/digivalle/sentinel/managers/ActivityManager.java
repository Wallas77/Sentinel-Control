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
import com.digivalle.sentinel.models.Activity;
import com.digivalle.sentinel.repositories.ActivityRepository;
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
public class ActivityManager {
    
    @Autowired
    private ActivityRepository activityRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public Activity getById(UUID id) throws EntityNotExistentException {
        Optional<Activity> activity = activityRepository.findById(id);
        if (!activity.isEmpty()) {
            return activity.get();
        }
        throw new EntityNotExistentException(Activity.class,id.toString());
    }
    
    public PagedResponse<Activity> getActivity(Activity filter, Paging paging){
        
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<Activity> cq = cb.createQuery(Activity.class);
        Root<Activity> root = cq.from(Activity.class);
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
        if(filter.getSerial()!=null){
            predicates.add(cb.equal(root.get("serial"), filter.getSerial()));
        }
        if(filter.getName()!=null){
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase()+ "%"));
        }
        if(filter.getDescription()!=null){
            predicates.add(cb.like(cb.lower(root.get("description")), "%" + filter.getDescription().toLowerCase()+ "%"));
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
        
        cq.select(root);
        if(predicates.size()>0){
            cq.where(predicates.toArray(new Predicate[0]));
        }
        
        TypedQuery<Activity> query = entityManager.createQuery(cq);
        
        int iTotal = query.getResultList().size();

        
        
        List<Activity> result = query.setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        
        Page<Activity> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<Activity>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }

    public Activity createActivity(Activity activity) throws BusinessLogicException, ExistentEntityException {
        validateActivity(activity);
        validateUnique(activity);
        return activityRepository.save(activity);
    }

    private void validateActivity(Activity activity) throws BusinessLogicException {
        if (StringUtils.isEmpty(activity.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto Activity");
        } else if (StringUtils.isEmpty(activity.getDescription())) {
            throw new BusinessLogicException("El campo Description es requerido para el objeto Activity");
        } else if (StringUtils.isEmpty(activity.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Activity");
        } 
    }
    
    private void validateUnique(Activity activity) throws ExistentEntityException {
        List<Activity> activityes = activityRepository.findByName(activity.getName());
        if (activityes!=null && !activityes.isEmpty()) {
            throw new ExistentEntityException(Activity.class,"name="+activity.getName());
        } 
    }

    public Activity updateActivity(UUID activityId, Activity activity) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(activity.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Activity");
        } 
    
        Activity persistedActivity = getById(activityId);
        if (persistedActivity != null) {
            if(activity.getName()!=null){
                persistedActivity.setName(activity.getName());
            }
            if(activity.getDescription()!=null){
                persistedActivity.setDescription(activity.getDescription());
            }
            if(activity.getActive()!=null){
                persistedActivity.setActive(activity.getActive());
            }
            persistedActivity.setUpdateUser(activity.getUpdateUser());
            return activityRepository.save(persistedActivity);
        } else {
            throw new EntityNotExistentException(Activity.class,activityId.toString());
        }
    }

    public Activity deleteActivity(UUID activityId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto Activity");
        } 
        Activity activity = getById(activityId);
        activity.setDeleted(Boolean.TRUE);
        activity.setActive(Boolean.FALSE);
        return activityRepository.save(activity);
    }

    public List<Activity> findAll(){
        return activityRepository.findAll();
    }
    
    public Activity getByName(String name){
        return activityRepository.getByName(name);
    }
    
    public List<Activity> findByNameIgnoreCaseContaining(String name){
        return activityRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<Activity> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return activityRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public Activity getBySerial(Integer serial) {
        return activityRepository.getBySerial(serial);
    }
}