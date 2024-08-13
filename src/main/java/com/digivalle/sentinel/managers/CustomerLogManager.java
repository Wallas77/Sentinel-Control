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
import com.digivalle.sentinel.models.CustomerLog;
import com.digivalle.sentinel.repositories.CustomerLogRepository;
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
public class CustomerLogManager {
    
    @Autowired
    private CustomerLogRepository customerLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public CustomerLog getById(UUID id) throws EntityNotExistentException {
        Optional<CustomerLog> customerLog = customerLogRepository.findById(id);
        if (!customerLog.isEmpty()) {
            return customerLog.get();
        }
        throw new EntityNotExistentException(CustomerLog.class,id.toString());
    }
    
    public PagedResponse<CustomerLog> getCustomerLog(CustomerLog filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CustomerLog> cq = cb.createQuery(CustomerLog.class);
        Root<CustomerLog> root = cq.from(CustomerLog.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<CustomerLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<CustomerLog> result = query.getResultList();
        
        Page<CustomerLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(CustomerLog filter, CriteriaBuilder cb, Root<CustomerLog> root) {
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

    private void applySorting(CriteriaQuery<CustomerLog> cq, CriteriaBuilder cb, Root<CustomerLog> root, CustomerLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, CustomerLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<CustomerLog> countRoot = countQuery.from(CustomerLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    

    public CustomerLog createCustomerLog(CustomerLog customerLog) throws BusinessLogicException {
        //validateCustomerLog(customerLog);
        //validateUnique(customerLog);
        return customerLogRepository.save(customerLog);
    }

    private void validateCustomerLog(CustomerLog customerLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(customerLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto CustomerLog");
        } else if (StringUtils.isEmpty(customerLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto CustomerLog");
        } 
    }
    
    private void validateUnique(CustomerLog customerLog) throws ExistentEntityException {
        List<CustomerLog> customerLoges = customerLogRepository.findByName(customerLog.getName());
        if (customerLoges!=null && !customerLoges.isEmpty()) {
            throw new ExistentEntityException(CustomerLog.class,"name="+customerLog.getName());
        } 
    }

    public CustomerLog updateCustomerLog(UUID customerLogId, CustomerLog customerLog) throws EntityNotExistentException {
        CustomerLog persistedCustomerLog = getById(customerLogId);
        if (persistedCustomerLog != null) {
            persistedCustomerLog.setName(customerLog.getName());
            return customerLogRepository.save(persistedCustomerLog);
        } else {
            throw new EntityNotExistentException(CustomerLog.class,customerLogId.toString());
        }
    }

    public void deleteCustomerLog(UUID customerLogId) throws EntityNotExistentException {
        CustomerLog customerLog = getById(customerLogId);
        customerLog.setDeleted(Boolean.TRUE);
        customerLog.setActive(Boolean.FALSE);
        customerLogRepository.save(customerLog);
    }

    public List<CustomerLog> findAll(){
        return customerLogRepository.findAll();
    }
    
    public CustomerLog getByName(String name){
        return customerLogRepository.getByName(name);
    }
    
    public List<CustomerLog> findByNameIgnoreCaseContaining(String name){
        return customerLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<CustomerLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return customerLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}
