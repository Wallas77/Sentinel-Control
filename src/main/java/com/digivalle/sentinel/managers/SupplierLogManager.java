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
import com.digivalle.sentinel.models.SupplierLog;
import com.digivalle.sentinel.models.SupplierLog;
import com.digivalle.sentinel.repositories.SupplierLogRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
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
public class SupplierLogManager {
    
    @Autowired
    private SupplierLogRepository supplierLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public SupplierLog getById(UUID id) throws EntityNotExistentException {
        Optional<SupplierLog> supplierLog = supplierLogRepository.findById(id);
        if (!supplierLog.isEmpty()) {
            return supplierLog.get();
        }
        throw new EntityNotExistentException(SupplierLog.class,id.toString());
    }
    
    public PagedResponse<SupplierLog> getSupplierLog(SupplierLog filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<SupplierLog> cq = cb.createQuery(SupplierLog.class);
        Root<SupplierLog> root = cq.from(SupplierLog.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<SupplierLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<SupplierLog> result = query.getResultList();
        
        Page<SupplierLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(SupplierLog filter, CriteriaBuilder cb, Root<SupplierLog> root) {
        List<Predicate> predicates = new ArrayList<>();

        if(filter.getName()!=null){
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase()+ "%"));
        }
        if(filter.getCity()!=null){
            predicates.add(cb.like(cb.lower(root.get("city")), "%" + filter.getCity().toLowerCase()+ "%"));
        }
        
        if(filter.getColony()!=null){
            predicates.add(cb.like(cb.lower(root.get("colony")), "%" + filter.getColony().toLowerCase()+ "%"));
        }
        if(filter.getCountry()!=null){
            if(filter.getCountry().getCode()!=null){
                predicates.add(cb.like(cb.lower(root.get("country").get("code")), "%" + filter.getCountry().getCode().toLowerCase()+ "%"));
            }
            if(filter.getCountry().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("country").get("name")), "%" + filter.getCountry().getName().toLowerCase()+ "%"));
            }
        }
        if(filter.getFiscalInfo()!=null){
            if(filter.getFiscalInfo().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("fiscalInfo").get("name")), "%" + filter.getFiscalInfo().getName().toLowerCase()+ "%"));
            }
        }
        if(filter.getExternalNumber()!=null){
            predicates.add(cb.like(cb.lower(root.get("externalNumber")), "%" + filter.getExternalNumber().toLowerCase()+ "%"));
        }
        if(filter.getInternalNumber()!=null){
            predicates.add(cb.like(cb.lower(root.get("internalNumber")), "%" + filter.getInternalNumber().toLowerCase()+ "%"));
        }
        if(filter.getState()!=null){
            predicates.add(cb.like(cb.lower(root.get("state")), "%" + filter.getState().toLowerCase()+ "%"));
        }
        if(filter.getStreet()!=null){
            predicates.add(cb.like(cb.lower(root.get("street")), "%" + filter.getStreet().toLowerCase()+ "%"));
        }
        if(filter.getSuburb()!=null){
            predicates.add(cb.like(cb.lower(root.get("suburb")), "%" + filter.getSuburb().toLowerCase()+ "%"));
        }
        if(filter.getZipCode()!=null){
            predicates.add(cb.like(cb.lower(root.get("zipCode")), "%" + filter.getZipCode().toLowerCase()+ "%"));
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

        return predicates;
    }

    private void applySorting(CriteriaQuery<SupplierLog> cq, CriteriaBuilder cb, Root<SupplierLog> root, SupplierLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, SupplierLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<SupplierLog> countRoot = countQuery.from(SupplierLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    

    public SupplierLog createSupplierLog(SupplierLog supplierLog) throws BusinessLogicException {
        //validateSupplierLog(supplierLog);
        //validateUnique(supplierLog);
        return supplierLogRepository.save(supplierLog);
    }

    private void validateSupplierLog(SupplierLog supplierLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(supplierLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto SupplierLog");
        } else if (StringUtils.isEmpty(supplierLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto SupplierLog");
        } 
    }
    
    private void validateUnique(SupplierLog supplierLog) throws ExistentEntityException {
        List<SupplierLog> supplierLoges = supplierLogRepository.findByName(supplierLog.getName());
        if (supplierLoges!=null && !supplierLoges.isEmpty()) {
            throw new ExistentEntityException(SupplierLog.class,"name="+supplierLog.getName());
        } 
    }

    public SupplierLog updateSupplierLog(UUID supplierLogId, SupplierLog supplierLog) throws EntityNotExistentException {
        SupplierLog persistedSupplierLog = getById(supplierLogId);
        if (persistedSupplierLog != null) {
            persistedSupplierLog.setName(supplierLog.getName());
            return supplierLogRepository.save(persistedSupplierLog);
        } else {
            throw new EntityNotExistentException(SupplierLog.class,supplierLogId.toString());
        }
    }

    public void deleteSupplierLog(UUID supplierLogId) throws EntityNotExistentException {
        SupplierLog supplierLog = getById(supplierLogId);
        supplierLog.setDeleted(Boolean.TRUE);
        supplierLog.setActive(Boolean.FALSE);
        supplierLogRepository.save(supplierLog);
    }

    public List<SupplierLog> findAll(){
        return supplierLogRepository.findAll();
    }
    
    public SupplierLog getByName(String name){
        return supplierLogRepository.getByName(name);
    }
    
    public List<SupplierLog> findByNameIgnoreCaseContaining(String name){
        return supplierLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<SupplierLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return supplierLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}
