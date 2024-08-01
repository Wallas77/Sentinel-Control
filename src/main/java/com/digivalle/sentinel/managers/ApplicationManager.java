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
import com.digivalle.sentinel.models.Application;
import com.digivalle.sentinel.repositories.ApplicationRepository;
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
public class ApplicationManager {
    
    @Autowired
    private ApplicationRepository applicationRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public Application getById(UUID id) throws EntityNotExistentException {
        Optional<Application> application = applicationRepository.findById(id);
        if (!application.isEmpty()) {
            return application.get();
        }
        throw new EntityNotExistentException(Application.class,id.toString());
    }
    
    public PagedResponse<Application> getApplication(Application filter, Paging paging){
        
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<Application> cq = cb.createQuery(Application.class);
        Root<Application> root = cq.from(Application.class);
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
        
        TypedQuery<Application> query = entityManager.createQuery(cq);
        
        int iTotal = query.getResultList().size();

        
        
        List<Application> result = query.setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        
        Page<Application> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<Application>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }

    public Application createApplication(Application application) throws BusinessLogicException, ExistentEntityException {
        validateApplication(application);
        validateUnique(application);
        return applicationRepository.save(application);
    }

    private void validateApplication(Application application) throws BusinessLogicException {
        if (StringUtils.isEmpty(application.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto Application");
        } else if (StringUtils.isEmpty(application.getDescription())) {
            throw new BusinessLogicException("El campo Description es requerido para el objeto Application");
        } else if (StringUtils.isEmpty(application.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Application");
        } 
    }
    
    private void validateUnique(Application application) throws ExistentEntityException {
        List<Application> applicationes = applicationRepository.findByName(application.getName());
        if (applicationes!=null && !applicationes.isEmpty()) {
            throw new ExistentEntityException(Application.class,"name="+application.getName());
        } 
    }

    public Application updateApplication(UUID applicationId, Application application) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(application.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Application");
        } 
    
        Application persistedApplication = getById(applicationId);
        if (persistedApplication != null) {
            if(application.getName()!=null){
                persistedApplication.setName(application.getName());
            }
            if(application.getDescription()!=null){
                persistedApplication.setDescription(application.getDescription());
            }
            if(application.getActive()!=null){
                persistedApplication.setActive(application.getActive());
            }
            persistedApplication.setUpdateUser(application.getUpdateUser());
            return applicationRepository.save(persistedApplication);
        } else {
            throw new EntityNotExistentException(Application.class,applicationId.toString());
        }
    }

    public Application deleteApplication(UUID applicationId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto Application");
        } 
        Application application = getById(applicationId);
        application.setDeleted(Boolean.TRUE);
        application.setActive(Boolean.FALSE);
        return applicationRepository.save(application);
    }

    public List<Application> findAll(){
        return applicationRepository.findAll();
    }
    
    public Application getByName(String name){
        return applicationRepository.getByName(name);
    }
    
    public List<Application> findByNameIgnoreCaseContaining(String name){
        return applicationRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<Application> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return applicationRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public Application getBySerial(Integer serial) {
        return applicationRepository.getBySerial(serial);
    }
}
