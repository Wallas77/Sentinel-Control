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
import com.digivalle.sentinel.models.BranchLog;
import com.digivalle.sentinel.repositories.BranchLogRepository;
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
public class BranchLogManager {
    
    @Autowired
    private BranchLogRepository branchLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public BranchLog getById(UUID id) throws EntityNotExistentException {
        Optional<BranchLog> branchLog = branchLogRepository.findById(id);
        if (!branchLog.isEmpty()) {
            return branchLog.get();
        }
        throw new EntityNotExistentException(BranchLog.class,id.toString());
    }
    
    public PagedResponse<BranchLog> getBranchLog(BranchLog filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<BranchLog> cq = cb.createQuery(BranchLog.class);
        Root<BranchLog> root = cq.from(BranchLog.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<BranchLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<BranchLog> result = query.getResultList();
        
        Page<BranchLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(BranchLog filter, CriteriaBuilder cb, Root<BranchLog> root) {
        List<Predicate> predicates = new ArrayList<>();

        if(filter.getName()!=null){
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase()+ "%"));
        }
        if(filter.getCity()!=null){
            predicates.add(cb.like(cb.lower(root.get("city")), "%" + filter.getCity().toLowerCase()+ "%"));
        }
        if(filter.getCode()!=null){
            predicates.add(cb.like(cb.lower(root.get("code")), "%" + filter.getCode().toLowerCase()+ "%"));
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
        if(filter.getCustomer()!=null){
            if(filter.getCustomer().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("customer").get("name")), "%" + filter.getCustomer().getName().toLowerCase()+ "%"));
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

    private void applySorting(CriteriaQuery<BranchLog> cq, CriteriaBuilder cb, Root<BranchLog> root, BranchLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, BranchLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<BranchLog> countRoot = countQuery.from(BranchLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    

    public BranchLog createBranchLog(BranchLog branchLog) throws BusinessLogicException {
        //validateBranchLog(branchLog);
        //validateUnique(branchLog);
        return branchLogRepository.save(branchLog);
    }

    private void validateBranchLog(BranchLog branchLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(branchLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto BranchLog");
        } else if (StringUtils.isEmpty(branchLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto BranchLog");
        } 
    }
    
    private void validateUnique(BranchLog branchLog) throws ExistentEntityException {
        List<BranchLog> branchLoges = branchLogRepository.findByName(branchLog.getName());
        if (branchLoges!=null && !branchLoges.isEmpty()) {
            throw new ExistentEntityException(BranchLog.class,"name="+branchLog.getName());
        } 
    }

    public BranchLog updateBranchLog(UUID branchLogId, BranchLog branchLog) throws EntityNotExistentException {
        BranchLog persistedBranchLog = getById(branchLogId);
        if (persistedBranchLog != null) {
            persistedBranchLog.setName(branchLog.getName());
            return branchLogRepository.save(persistedBranchLog);
        } else {
            throw new EntityNotExistentException(BranchLog.class,branchLogId.toString());
        }
    }

    public void deleteBranchLog(UUID branchLogId) throws EntityNotExistentException {
        BranchLog branchLog = getById(branchLogId);
        branchLog.setDeleted(Boolean.TRUE);
        branchLog.setActive(Boolean.FALSE);
        branchLogRepository.save(branchLog);
    }

    public List<BranchLog> findAll(){
        return branchLogRepository.findAll();
    }
    
    public BranchLog getByName(String name){
        return branchLogRepository.getByName(name);
    }
    
    public List<BranchLog> findByNameIgnoreCaseContaining(String name){
        return branchLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<BranchLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return branchLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}
