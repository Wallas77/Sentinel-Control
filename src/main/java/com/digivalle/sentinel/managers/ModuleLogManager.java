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
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
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

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<ModuleLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<ModuleLog> result = query.getResultList();
        
        Page<ModuleLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(ModuleLog filter, CriteriaBuilder cb, Root<ModuleLog> root) {
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
        if(filter.getModuleId()!=null){
            predicates.add(cb.equal(root.get("moduleId"), filter.getModuleId()));
        }
        if(filter.getTransactionId()!=null){
            predicates.add(cb.equal(root.get("transactionId"), filter.getTransactionId()));
        }
        if(filter.getAction()!=null){
            predicates.add(cb.equal(root.get("action"), filter.getAction()));
        }
        
        return predicates;
    }

    private void applySorting(CriteriaQuery<ModuleLog> cq, CriteriaBuilder cb, Root<ModuleLog> root, ModuleLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, ModuleLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ModuleLog> countRoot = countQuery.from(ModuleLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
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
