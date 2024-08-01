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
import com.digivalle.sentinel.models.ProfileModuleGrantLog;
import com.digivalle.sentinel.repositories.ProfileModuleGrantLogRepository;
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
public class ProfileModuleGrantLogManager {
    
    @Autowired
    private ProfileModuleGrantLogRepository profileModuleGrantLogRepository;
    
    @Autowired
    private EntityManager entityManager;
    
    public ProfileModuleGrantLog getById(UUID id) throws EntityNotExistentException {
        Optional<ProfileModuleGrantLog> profileModuleGrantLog = profileModuleGrantLogRepository.findById(id);
        if (!profileModuleGrantLog.isEmpty()) {
            return profileModuleGrantLog.get();
        }
        throw new EntityNotExistentException(ProfileModuleGrantLog.class,id.toString());
    }
    
    public PagedResponse<ProfileModuleGrantLog> getProfileModuleGrantLog(ProfileModuleGrantLog filter, Paging paging){
        
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<ProfileModuleGrantLog> cq = cb.createQuery(ProfileModuleGrantLog.class);
        Root<ProfileModuleGrantLog> root = cq.from(ProfileModuleGrantLog.class);
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
        
        if(filter.getActive()!=null){
            predicates.add(cb.equal(root.get("active"), filter.getActive()));
        }
        if(filter.getDeleted()!=null){
            predicates.add(cb.equal(root.get("deleted"), filter.getDeleted()));
        }
        if(filter.getUpdateUser()!=null){
            predicates.add(cb.equal(root.get("updateUser"), filter.getUpdateUser()));
        }
        if(filter.getProfileModuleGrantId()!=null){
            predicates.add(cb.equal(root.get("profileModuleGrantId"), filter.getProfileModuleGrantId()));
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
        
        TypedQuery<ProfileModuleGrantLog> query = entityManager.createQuery(cq);
        
        int iTotal = query.getResultList().size();

        
        
        List<ProfileModuleGrantLog> result = query.setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        
        Page<ProfileModuleGrantLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<ProfileModuleGrantLog>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }

    public ProfileModuleGrantLog createProfileModuleGrantLog(ProfileModuleGrantLog profileModuleGrantLog) throws BusinessLogicException {
        validateProfileModuleGrantLog(profileModuleGrantLog);
        return profileModuleGrantLogRepository.save(profileModuleGrantLog);
    }

    private void validateProfileModuleGrantLog(ProfileModuleGrantLog profileModuleGrantLog) throws BusinessLogicException {
        if (profileModuleGrantLog.getProfile()==null) {
            throw new BusinessLogicException("El campo Profile es requerido para el objeto ProfileModuleGrantLog");
        } else if (profileModuleGrantLog.getModule()==null) {
            throw new BusinessLogicException("El campo Module es requerido para el objeto ProfileModuleGrantLog");
        } else if (profileModuleGrantLog.getGrant()==null) {
            throw new BusinessLogicException("El campo Grant es requerido para el objeto ProfileModuleGrantLog");
        } else if (StringUtils.isEmpty(profileModuleGrantLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto ProfileModuleGrantLog");
        } 
    }
    
    

    public ProfileModuleGrantLog updateProfileModuleGrantLog(UUID profileModuleGrantLogId, ProfileModuleGrantLog profileModuleGrantLog) throws EntityNotExistentException, BusinessLogicException {
        validateProfileModuleGrantLog(profileModuleGrantLog);
        ProfileModuleGrantLog persistedProfileModuleGrantLog = getById(profileModuleGrantLogId);
        if (persistedProfileModuleGrantLog != null) {
            if(profileModuleGrantLog.getProfile()!=null){
                persistedProfileModuleGrantLog.setProfile(profileModuleGrantLog.getProfile());
            }
            if(profileModuleGrantLog.getModule()!=null){
                persistedProfileModuleGrantLog.setModule(profileModuleGrantLog.getModule());
            }
            if(profileModuleGrantLog.getGrant()!=null){
                persistedProfileModuleGrantLog.setGrant(profileModuleGrantLog.getGrant());
            }
            if(profileModuleGrantLog.getActive()!=null){
                persistedProfileModuleGrantLog.setActive(profileModuleGrantLog.getActive());
            }
            persistedProfileModuleGrantLog.setUpdateUser(profileModuleGrantLog.getUpdateUser());
            return profileModuleGrantLogRepository.save(persistedProfileModuleGrantLog);
        } else {
            throw new EntityNotExistentException(ProfileModuleGrantLog.class,profileModuleGrantLogId.toString());
        }
    }

    public ProfileModuleGrantLog deleteProfileModuleGrantLog(UUID profileModuleGrantLogId) throws EntityNotExistentException {
        ProfileModuleGrantLog profileModuleGrantLog = getById(profileModuleGrantLogId);
        profileModuleGrantLog.setDeleted(Boolean.TRUE);
        profileModuleGrantLog.setActive(Boolean.FALSE);
        return profileModuleGrantLogRepository.save(profileModuleGrantLog);
    }

    public List<ProfileModuleGrantLog> findAll(){
        return profileModuleGrantLogRepository.findAll();
    }
    
   
}
