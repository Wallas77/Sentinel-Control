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
import com.digivalle.sentinel.models.ActivityFile;
import com.digivalle.sentinel.repositories.ActivityFileRepository;
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
public class ActivityFileManager {
    
    @Autowired
    private ActivityFileRepository activityFileRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public ActivityFile getById(UUID id) throws EntityNotExistentException {
        Optional<ActivityFile> activityFile = activityFileRepository.findById(id);
        if (!activityFile.isEmpty()) {
            return activityFile.get();
        }
        throw new EntityNotExistentException(ActivityFile.class,id.toString());
    }
    
    public PagedResponse<ActivityFile> getActivityFile(ActivityFile filter, Paging paging){
        
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<ActivityFile> cq = cb.createQuery(ActivityFile.class);
        Root<ActivityFile> root = cq.from(ActivityFile.class);
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
        
        TypedQuery<ActivityFile> query = entityManager.createQuery(cq);
        
        int iTotal = query.getResultList().size();

        
        
        List<ActivityFile> result = query.setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        
        Page<ActivityFile> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<ActivityFile>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }

    public ActivityFile createActivityFile(ActivityFile activityFile) throws BusinessLogicException, ExistentEntityException {
        validateActivityFile(activityFile);
        validateUnique(activityFile);
        return activityFileRepository.save(activityFile);
    }

    private void validateActivityFile(ActivityFile activityFile) throws BusinessLogicException {
        if (StringUtils.isEmpty(activityFile.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto ActivityFile");
        } else if (StringUtils.isEmpty(activityFile.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto ActivityFile");
        } 
    }
    
    private void validateUnique(ActivityFile activityFile) throws ExistentEntityException {
        List<ActivityFile> activityFilees = activityFileRepository.findByName(activityFile.getName());
        if (activityFilees!=null && !activityFilees.isEmpty()) {
            throw new ExistentEntityException(ActivityFile.class,"name="+activityFile.getName());
        } 
    }

    public ActivityFile updateActivityFile(UUID activityFileId, ActivityFile activityFile) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(activityFile.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto ActivityFile");
        } 
    
        ActivityFile persistedActivityFile = getById(activityFileId);
        if (persistedActivityFile != null) {
            if(activityFile.getName()!=null){
                persistedActivityFile.setName(activityFile.getName());
            }
            
            if(activityFile.getActive()!=null){
                persistedActivityFile.setActive(activityFile.getActive());
            }
            persistedActivityFile.setUpdateUser(activityFile.getUpdateUser());
            return activityFileRepository.save(persistedActivityFile);
        } else {
            throw new EntityNotExistentException(ActivityFile.class,activityFileId.toString());
        }
    }

    public ActivityFile deleteActivityFile(UUID activityFileId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto ActivityFile");
        } 
        ActivityFile activityFile = getById(activityFileId);
        activityFile.setDeleted(Boolean.TRUE);
        activityFile.setActive(Boolean.FALSE);
        return activityFileRepository.save(activityFile);
    }

    public List<ActivityFile> findAll(){
        return activityFileRepository.findAll();
    }
    
    public ActivityFile getByName(String name){
        return activityFileRepository.getByName(name);
    }
    
    public List<ActivityFile> findByNameIgnoreCaseContaining(String name){
        return activityFileRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<ActivityFile> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return activityFileRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public ActivityFile getBySerial(Integer serial) {
        return activityFileRepository.getBySerial(serial);
    }
}
