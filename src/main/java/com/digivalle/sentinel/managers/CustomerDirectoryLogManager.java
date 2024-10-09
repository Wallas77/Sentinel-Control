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
import com.digivalle.sentinel.models.CustomerDirectoryLog;
import com.digivalle.sentinel.repositories.CustomerDirectoryLogRepository;
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
public class CustomerDirectoryLogManager {
    
    @Autowired
    private CustomerDirectoryLogRepository customerDirectoryLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public CustomerDirectoryLog getById(UUID id) throws EntityNotExistentException {
        Optional<CustomerDirectoryLog> customerDirectoryLog = customerDirectoryLogRepository.findById(id);
        if (!customerDirectoryLog.isEmpty()) {
            return customerDirectoryLog.get();
        }
        throw new EntityNotExistentException(CustomerDirectoryLog.class,id.toString());
    }
    
    public PagedResponse<CustomerDirectoryLog> getCustomerDirectoryLog(CustomerDirectoryLog filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CustomerDirectoryLog> cq = cb.createQuery(CustomerDirectoryLog.class);
        Root<CustomerDirectoryLog> root = cq.from(CustomerDirectoryLog.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<CustomerDirectoryLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<CustomerDirectoryLog> result = query.getResultList();
        
        Page<CustomerDirectoryLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(CustomerDirectoryLog filter, CriteriaBuilder cb, Root<CustomerDirectoryLog> root) {
        List<Predicate> predicates = new ArrayList<>();

        if(filter.getCreationDate()!=null && filter.getCreationDate2()!=null){
            predicates.add(cb.between(root.get("creationDate"), filter.getCreationDate(),filter.getCreationDate2()));
        }
        if(filter.getUpdateDate()!=null && filter.getUpdateDate2()!=null){
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
        if(filter.getCustomer()!=null){
            if(filter.getCustomer().getId()!=null){
                predicates.add(cb.equal(root.get("customer").get("id"), filter.getCustomer().getId()));
            }
            if(filter.getCustomer().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("customer").get("name")), "%" + filter.getCustomer().getName().toLowerCase()+ "%"));
            }
        }
        if(filter.getCustomerDirectoryId()!=null){
            predicates.add(cb.equal(root.get("customerDirectoryId"), filter.getCustomerDirectoryId()));
        }
        if(filter.getTransactionId()!=null){
            predicates.add(cb.equal(root.get("transactionId"), filter.getTransactionId()));
        }
        if(filter.getAction()!=null){
            predicates.add(cb.equal(root.get("action"), filter.getAction()));
        }

        return predicates;
    }

    private void applySorting(CriteriaQuery<CustomerDirectoryLog> cq, CriteriaBuilder cb, Root<CustomerDirectoryLog> root, CustomerDirectoryLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, CustomerDirectoryLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<CustomerDirectoryLog> countRoot = countQuery.from(CustomerDirectoryLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public CustomerDirectoryLog createCustomerDirectoryLog(CustomerDirectoryLog customerDirectoryLog) throws BusinessLogicException {
        //validateCustomerDirectoryLog(customerDirectoryLog);
        //validateUnique(customerDirectoryLog);
        return customerDirectoryLogRepository.save(customerDirectoryLog);
    }

    private void validateCustomerDirectoryLog(CustomerDirectoryLog customerDirectoryLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(customerDirectoryLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto CustomerDirectoryLog");
        } else if (StringUtils.isEmpty(customerDirectoryLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto CustomerDirectoryLog");
        } 
    }
    
    private void validateUnique(CustomerDirectoryLog customerDirectoryLog) throws ExistentEntityException {
        List<CustomerDirectoryLog> customerDirectoryLoges = customerDirectoryLogRepository.findByName(customerDirectoryLog.getName());
        if (customerDirectoryLoges!=null && !customerDirectoryLoges.isEmpty()) {
            throw new ExistentEntityException(CustomerDirectoryLog.class,"name="+customerDirectoryLog.getName());
        } 
    }

    public CustomerDirectoryLog updateCustomerDirectoryLog(UUID customerDirectoryLogId, CustomerDirectoryLog customerDirectoryLog) throws EntityNotExistentException {
        CustomerDirectoryLog persistedCustomerDirectoryLog = getById(customerDirectoryLogId);
        if (persistedCustomerDirectoryLog != null) {
            persistedCustomerDirectoryLog.setName(customerDirectoryLog.getName());
            return customerDirectoryLogRepository.save(persistedCustomerDirectoryLog);
        } else {
            throw new EntityNotExistentException(CustomerDirectoryLog.class,customerDirectoryLogId.toString());
        }
    }

    public void deleteCustomerDirectoryLog(UUID customerDirectoryLogId) throws EntityNotExistentException {
        CustomerDirectoryLog customerDirectoryLog = getById(customerDirectoryLogId);
        customerDirectoryLog.setDeleted(Boolean.TRUE);
        customerDirectoryLog.setActive(Boolean.FALSE);
        customerDirectoryLogRepository.save(customerDirectoryLog);
    }

    public List<CustomerDirectoryLog> findAll(){
        return customerDirectoryLogRepository.findAll();
    }
    
    public CustomerDirectoryLog getByName(String name){
        return customerDirectoryLogRepository.getByName(name);
    }
    
    public List<CustomerDirectoryLog> findByNameIgnoreCaseContaining(String name){
        return customerDirectoryLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<CustomerDirectoryLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return customerDirectoryLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}
