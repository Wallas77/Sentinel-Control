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
import com.digivalle.sentinel.models.Grant;
import com.digivalle.sentinel.repositories.GrantRepository;
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
public class GrantManager {
    
    @Autowired
    private GrantRepository grantRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public Grant getById(UUID id) throws EntityNotExistentException {
        Optional<Grant> grant = grantRepository.findById(id);
        if (!grant.isEmpty()) {
            return grant.get();
        }
        throw new EntityNotExistentException(Grant.class,id.toString());
    }
    
    public PagedResponse<Grant> getGrant(Grant filter, Paging paging){
        
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<Grant> cq = cb.createQuery(Grant.class);
        Root<Grant> root = cq.from(Grant.class);
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
        
        TypedQuery<Grant> query = entityManager.createQuery(cq);
        
        int iTotal = query.getResultList().size();

        
        
        List<Grant> result = query.setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        
        Page<Grant> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<Grant>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }

    public Grant createGrant(Grant grant) throws BusinessLogicException, ExistentEntityException {
        validateGrant(grant);
        validateUnique(grant);
        return grantRepository.save(grant);
    }

    private void validateGrant(Grant grant) throws BusinessLogicException {
        if (StringUtils.isEmpty(grant.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto Grant");
        } else if (StringUtils.isEmpty(grant.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Grant");
        } 
    }
    
    private void validateUnique(Grant grant) throws ExistentEntityException {
        List<Grant> grantes = grantRepository.getByName(grant.getName());
        if (grantes!=null && !grantes.isEmpty()) {
            throw new ExistentEntityException(Grant.class,"name="+grant.getName());
        } 
    }

    public Grant updateGrant(UUID grantId, Grant grant) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(grant.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Grant");
        } 
        Grant persistedGrant = getById(grantId);
        if (persistedGrant != null) {
            if(grant.getName()!=null){
                persistedGrant.setName(grant.getName());
            }
            if(grant.getActive()!=null){
                persistedGrant.setActive(grant.getActive());
            }
            persistedGrant.setUpdateUser(grant.getUpdateUser());
            return grantRepository.save(persistedGrant);
        } else {
            throw new EntityNotExistentException(Grant.class,grantId.toString());
        }
    }

    public Grant deleteGrant(UUID grantId) throws EntityNotExistentException {
        Grant grant = getById(grantId);
        grant.setActive(Boolean.FALSE);
        grant.setDeleted(Boolean.TRUE);
        return grantRepository.save(grant);
    }

    public List<Grant> findAll(){
        return grantRepository.findAll();
    }
    
    public List<Grant> getByName(String name){
        return grantRepository.getByName(name);
    }
    
    public List<Grant> findByNameIgnoreCaseContaining(String name){
        return grantRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<Grant> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return grantRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public Grant getBySerial(Integer serial) {
        return grantRepository.getBySerial(serial);
    }
}
