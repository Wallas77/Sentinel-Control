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
import com.digivalle.sentinel.models.CustomerDirectory;
import com.digivalle.sentinel.repositories.CustomerDirectoryRepository;
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
public class CustomerDirectoryManager {
    
    @Autowired
    private CustomerDirectoryRepository customerDirectoryRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public CustomerDirectory getById(UUID id) throws EntityNotExistentException {
        Optional<CustomerDirectory> customerDirectory = customerDirectoryRepository.findById(id);
        if (!customerDirectory.isEmpty()) {
            return customerDirectory.get();
        }
        throw new EntityNotExistentException(CustomerDirectory.class,id.toString());
    }
    
    public PagedResponse<CustomerDirectory> getCustomerDirectory(CustomerDirectory filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CustomerDirectory> cq = cb.createQuery(CustomerDirectory.class);
        Root<CustomerDirectory> root = cq.from(CustomerDirectory.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<CustomerDirectory> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<CustomerDirectory> result = query.getResultList();
        
        Page<CustomerDirectory> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(CustomerDirectory filter, CriteriaBuilder cb, Root<CustomerDirectory> root) {
        List<Predicate> predicates = new ArrayList<>();

        if(filter.getCreationDate()!=null && filter.getCreationDate2()!=null){
            predicates.add(cb.between(root.get("creationDate"), filter.getCreationDate(),filter.getCreationDate2()));
        }
        if(filter.getUpdateDate()!=null && filter.getUpdateDate2()!=null){
            predicates.add(cb.between(root.get("updateDate"), filter.getUpdateDate(),filter.getUpdateDate2()));
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
        if(filter.getCustomer()!=null){
            if(filter.getCustomer().getId()!=null){
                predicates.add(cb.equal(root.get("customer").get("id"), filter.getCustomer().getId()));
            }
            if(filter.getCustomer().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("customer").get("name")), "%" + filter.getCustomer().getName().toLowerCase()+ "%"));
            }
        }
        if(filter.getBranch()!=null){
            if(filter.getBranch().getId()!=null){
                predicates.add(cb.equal(root.get("branch").get("id"), filter.getBranch().getId()));
            }
            if(filter.getBranch().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("branch").get("name")), "%" + filter.getBranch().getName().toLowerCase()+ "%"));
            }
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

    private void applySorting(CriteriaQuery<CustomerDirectory> cq, CriteriaBuilder cb, Root<CustomerDirectory> root, CustomerDirectory filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, CustomerDirectory filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<CustomerDirectory> countRoot = countQuery.from(CustomerDirectory.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public CustomerDirectory createCustomerDirectory(CustomerDirectory customerDirectory) throws BusinessLogicException, ExistentEntityException {
        validateCustomerDirectory(customerDirectory);
        validateUnique(customerDirectory);
        if(customerDirectory.getBranch()!=null && customerDirectory.getBranch().getId()==null){
            customerDirectory.setBranch(null);
        }
        return customerDirectoryRepository.save(customerDirectory);
    }

    private void validateCustomerDirectory(CustomerDirectory customerDirectory) throws BusinessLogicException {
        if (StringUtils.isEmpty(customerDirectory.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto CustomerDirectory");
        } else if (StringUtils.isEmpty(customerDirectory.getDescription())) {
            throw new BusinessLogicException("El campo Description es requerido para el objeto CustomerDirectory");
        } else if (StringUtils.isEmpty(customerDirectory.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto CustomerDirectory");
        } else if(customerDirectory.getCustomer()==null){
            throw new BusinessLogicException("El campo Customer es requerido para el objeto CustomerDirectory");
        }
    }
    
    private void validateUnique(CustomerDirectory customerDirectory) throws ExistentEntityException {
        List<CustomerDirectory> customerDirectoryes = customerDirectoryRepository.findByNameIgnoreCaseContainingAndCustomerAndDeleted(customerDirectory.getName(), customerDirectory.getCustomer(),Boolean.FALSE);
        if (customerDirectoryes!=null && !customerDirectoryes.isEmpty()) {
            throw new ExistentEntityException(CustomerDirectory.class,"name="+customerDirectory.getName()+", customer="+customerDirectory.getCustomer().getName());
        } 
    }

    public CustomerDirectory updateCustomerDirectory(UUID customerDirectoryId, CustomerDirectory customerDirectory) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(customerDirectory.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto CustomerDirectory");
        } 
    
        CustomerDirectory persistedCustomerDirectory = getById(customerDirectoryId);
        if (persistedCustomerDirectory != null) {
            if(customerDirectory.getName()!=null){
                persistedCustomerDirectory.setName(customerDirectory.getName());
            }
            if(customerDirectory.getDescription()!=null){
                persistedCustomerDirectory.setDescription(customerDirectory.getDescription());
            }
            if(customerDirectory.getCustomer()!=null){
                persistedCustomerDirectory.setCustomer(customerDirectory.getCustomer());
            }
            if(customerDirectory.getBranch()!=null){
                persistedCustomerDirectory.setBranch(customerDirectory.getBranch());
            }
            if(customerDirectory.getActive()!=null){
                persistedCustomerDirectory.setActive(customerDirectory.getActive());
            }
            persistedCustomerDirectory.setUpdateUser(customerDirectory.getUpdateUser());
            return customerDirectoryRepository.save(persistedCustomerDirectory);
        } else {
            throw new EntityNotExistentException(CustomerDirectory.class,customerDirectoryId.toString());
        }
    }

    public CustomerDirectory deleteCustomerDirectory(UUID customerDirectoryId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto CustomerDirectory");
        } 
        CustomerDirectory customerDirectory = getById(customerDirectoryId);
        customerDirectory.setDeleted(Boolean.TRUE);
        customerDirectory.setActive(Boolean.FALSE);
        return customerDirectoryRepository.save(customerDirectory);
    }

    public List<CustomerDirectory> findAll(){
        return customerDirectoryRepository.findAll();
    }
    
    public CustomerDirectory getByName(String name){
        return customerDirectoryRepository.getByName(name);
    }
    
    public List<CustomerDirectory> findByNameIgnoreCaseContaining(String name){
        return customerDirectoryRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<CustomerDirectory> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return customerDirectoryRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public CustomerDirectory getBySerial(Integer serial) {
        return customerDirectoryRepository.getBySerial(serial);
    }
}
