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
import com.digivalle.sentinel.models.GrantLog;
import com.digivalle.sentinel.repositories.GrantLogRepository;
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
public class GrantLogManager {
    
    @Autowired
    private GrantLogRepository grantLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public GrantLog getById(UUID id) throws EntityNotExistentException {
        Optional<GrantLog> grantLog = grantLogRepository.findById(id);
        if (!grantLog.isEmpty()) {
            return grantLog.get();
        }
        throw new EntityNotExistentException(GrantLog.class,id.toString());
    }
    
    public PagedResponse<GrantLog> getGrantLog(GrantLog filter, Paging paging){
        
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<GrantLog> cq = cb.createQuery(GrantLog.class);
        Root<GrantLog> root = cq.from(GrantLog.class);
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
        if(filter.getGrantId()!=null){
            predicates.add(cb.equal(root.get("grantId"), filter.getGrantId()));
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
        
        TypedQuery<GrantLog> query = entityManager.createQuery(cq);
        
        int iTotal = query.getResultList().size();

        
        
        List<GrantLog> result = query.setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        
        Page<GrantLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<GrantLog>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }

    public GrantLog createGrantLog(GrantLog grantLog) throws BusinessLogicException {
        //validateGrantLog(grantLog);
        //validateUnique(grantLog);
        return grantLogRepository.save(grantLog);
    }

    private void validateGrantLog(GrantLog grantLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(grantLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto GrantLog");
        } else if (StringUtils.isEmpty(grantLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto GrantLog");
        } 
    }
    
    private void validateUnique(GrantLog grantLog) throws ExistentEntityException {
        List<GrantLog> grantLoges = grantLogRepository.getByName(grantLog.getName());
        if (grantLoges!=null && !grantLoges.isEmpty()) {
            throw new ExistentEntityException(GrantLog.class,"name="+grantLog.getName());
        } 
    }

    public GrantLog updateGrantLog(UUID grantLogId, GrantLog grantLog) throws EntityNotExistentException {
        GrantLog persistedGrantLog = getById(grantLogId);
        if (persistedGrantLog != null) {
            persistedGrantLog.setName(grantLog.getName());
            return grantLogRepository.save(persistedGrantLog);
        } else {
            throw new EntityNotExistentException(GrantLog.class,grantLogId.toString());
        }
    }

    public void deleteGrantLog(UUID grantLogId) throws EntityNotExistentException {
        GrantLog grantLog = getById(grantLogId);
        grantLog.setDeleted(Boolean.TRUE);
        grantLog.setActive(Boolean.FALSE);
        grantLogRepository.save(grantLog);
    }

    public List<GrantLog> findAll(){
        return grantLogRepository.findAll();
    }
    
    public List<GrantLog> getByName(String name){
        return grantLogRepository.getByName(name);
    }
    
    public List<GrantLog> findByNameIgnoreCaseContaining(String name){
        return grantLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<GrantLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return grantLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}