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
import com.digivalle.sentinel.models.Profile;
import com.digivalle.sentinel.models.ProfileModuleGrant;
import com.digivalle.sentinel.repositories.ProfileModuleGrantRepository;
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
public class ProfileModuleGrantManager {
    
    @Autowired
    private ProfileModuleGrantRepository profileModuleGrantRepository;
    
    @Autowired
    private EntityManager entityManager;
    
    public ProfileModuleGrant getById(UUID id) throws EntityNotExistentException {
        Optional<ProfileModuleGrant> profileModuleGrant = profileModuleGrantRepository.findById(id);
        if (!profileModuleGrant.isEmpty()) {
            return profileModuleGrant.get();
        }
        throw new EntityNotExistentException(ProfileModuleGrant.class,id.toString());
    }
    
    public PagedResponse<ProfileModuleGrant> getProfileModuleGrant(ProfileModuleGrant filter, Paging paging){
        
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<ProfileModuleGrant> cq = cb.createQuery(ProfileModuleGrant.class);
        Root<ProfileModuleGrant> root = cq.from(ProfileModuleGrant.class);
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
        
        cq.select(root);
        if(predicates.size()>0){
            cq.where(predicates.toArray(new Predicate[0]));
        }
        
        TypedQuery<ProfileModuleGrant> query = entityManager.createQuery(cq);
        
        int iTotal = query.getResultList().size();

        
        
        List<ProfileModuleGrant> result = query.setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        
        Page<ProfileModuleGrant> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<ProfileModuleGrant>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }

    public ProfileModuleGrant createProfileModuleGrant(ProfileModuleGrant profileModuleGrant) throws BusinessLogicException {
        validateProfileModuleGrant(profileModuleGrant);
        return profileModuleGrantRepository.save(profileModuleGrant);
    }

    private void validateProfileModuleGrant(ProfileModuleGrant profileModuleGrant) throws BusinessLogicException {
        if (profileModuleGrant.getProfile()==null) {
            throw new BusinessLogicException("El campo Profile es requerido para el objeto ProfileModuleGrant");
        } else if (profileModuleGrant.getModule()==null) {
            throw new BusinessLogicException("El campo Module es requerido para el objeto ProfileModuleGrant");
        } else if (profileModuleGrant.getGrant()==null) {
            throw new BusinessLogicException("El campo Grant es requerido para el objeto ProfileModuleGrant");
        } else if (StringUtils.isEmpty(profileModuleGrant.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto ProfileModuleGrant");
        } 
    }
    
    

    public ProfileModuleGrant updateProfileModuleGrant(UUID profileModuleGrantId, ProfileModuleGrant profileModuleGrant) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(profileModuleGrant.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto ProfileModuleGrant");
        } 
        ProfileModuleGrant persistedProfileModuleGrant = getById(profileModuleGrantId);
        if (persistedProfileModuleGrant != null) {
            if(profileModuleGrant.getProfile()!=null){
                persistedProfileModuleGrant.setProfile(profileModuleGrant.getProfile());
            }
            if(profileModuleGrant.getModule()!=null){
                persistedProfileModuleGrant.setModule(profileModuleGrant.getModule());
            }
            if(profileModuleGrant.getGrant()!=null){
                persistedProfileModuleGrant.setGrant(profileModuleGrant.getGrant());
            }
            if(profileModuleGrant.getActive()!=null){
                persistedProfileModuleGrant.setActive(profileModuleGrant.getActive());
            }
            persistedProfileModuleGrant.setUpdateUser(profileModuleGrant.getUpdateUser());
            return profileModuleGrantRepository.save(persistedProfileModuleGrant);
        } else {
            throw new EntityNotExistentException(ProfileModuleGrant.class,profileModuleGrantId.toString());
        }
    }

    public void deleteProfileModuleGrant(UUID profileModuleGrantId) throws EntityNotExistentException {
        //ProfileModuleGrant profileModuleGrant = getById(profileModuleGrantId);
        /*profileModuleGrant.setDeleted(Boolean.TRUE);
        return profileModuleGrantRepository.save(profileModuleGrant);*/
        profileModuleGrantRepository.deleteById(profileModuleGrantId);
    }
    
    public void deleteByProfile(Profile profile){
        profileModuleGrantRepository.deleteByProfile(profile);
    }

    public List<ProfileModuleGrant> findAll(){
        return profileModuleGrantRepository.findAll();
    }
    
    public List<ProfileModuleGrant> findByProfileAndModule_NameAndGrant_Name(Profile profile, String moduleName, String grantName){
        return profileModuleGrantRepository.findByProfileAndModule_NameAndGrant_Name(profile, moduleName, grantName);
    }
    
    public List<ProfileModuleGrant> findByProfileAndModule_Application_Name(Profile profile, String applicationName){
        return profileModuleGrantRepository.findByProfileAndModule_Application_Name(profile, applicationName);
    }
    
    
}