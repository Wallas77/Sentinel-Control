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
import com.digivalle.sentinel.models.ModuleLog;
import com.digivalle.sentinel.repositories.ModuleLogRepository;
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
public class ModuleLogManager {
    
    @Autowired
    private ModuleLogRepository moduleLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public ModuleLog getById(UUID id) throws EntityNotExistentException {
        Optional<ModuleLog> moduleLog = moduleLogRepository.findById(id);
        if (!moduleLog.isEmpty()) {
            return moduleLog.get();
        }
        throw new EntityNotExistentException(ModuleLog.class,id.toString());
    }
    
    public PagedResponse<ModuleLog> getModuleLog(ModuleLog filter, Paging paging){
        
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<ModuleLog> cq = cb.createQuery(ModuleLog.class);
        Root<ModuleLog> root = cq.from(ModuleLog.class);
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
        if(filter.getModuleId()!=null){
            predicates.add(cb.equal(root.get("moduleId"), filter.getModuleId()));
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
        
        TypedQuery<ModuleLog> query = entityManager.createQuery(cq);
        
        int iTotal = query.getResultList().size();

        
        
        List<ModuleLog> result = query.setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        
        Page<ModuleLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<ModuleLog>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }

    public ModuleLog createModuleLog(ModuleLog moduleLog) throws BusinessLogicException {
        //validateModuleLog(moduleLog);
        //validateUnique(moduleLog);
        return moduleLogRepository.save(moduleLog);
    }

    private void validateModuleLog(ModuleLog moduleLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(moduleLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto ModuleLog");
        } else if (StringUtils.isEmpty(moduleLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto ModuleLog");
        } 
    }
    
    private void validateUnique(ModuleLog moduleLog) throws ExistentEntityException {
        List<ModuleLog> moduleLoges = moduleLogRepository.getByName(moduleLog.getName());
        if (moduleLoges!=null && !moduleLoges.isEmpty()) {
            throw new ExistentEntityException(ModuleLog.class,"name="+moduleLog.getName());
        } 
    }

    public ModuleLog updateModuleLog(UUID moduleLogId, ModuleLog moduleLog) throws EntityNotExistentException {
        ModuleLog persistedModuleLog = getById(moduleLogId);
        if (persistedModuleLog != null) {
            persistedModuleLog.setName(moduleLog.getName());
            return moduleLogRepository.save(persistedModuleLog);
        } else {
            throw new EntityNotExistentException(ModuleLog.class,moduleLogId.toString());
        }
    }

    public void deleteModuleLog(UUID moduleLogId) throws EntityNotExistentException {
        ModuleLog moduleLog = getById(moduleLogId);
        moduleLog.setDeleted(Boolean.TRUE);
        moduleLog.setActive(Boolean.FALSE);
        moduleLogRepository.save(moduleLog);
    }

    public List<ModuleLog> findAll(){
        return moduleLogRepository.findAll();
    }
    
    public List<ModuleLog> getByName(String name){
        return moduleLogRepository.getByName(name);
    }
    
    public List<ModuleLog> findByNameIgnoreCaseContaining(String name){
        return moduleLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<ModuleLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return moduleLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}
