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
import com.digivalle.sentinel.models.Customer;
import com.digivalle.sentinel.repositories.CustomerRepository;
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
public class CustomerManager {
    
    @Autowired
    private CustomerRepository customerRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public Customer getById(UUID id) throws EntityNotExistentException {
        Optional<Customer> customer = customerRepository.findById(id);
        if (!customer.isEmpty()) {
            return customer.get();
        }
        throw new EntityNotExistentException(Customer.class,id.toString());
    }
    
    public PagedResponse<Customer> getCustomer(Customer filter, Paging paging){
        
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<Customer> cq = cb.createQuery(Customer.class);
        Root<Customer> root = cq.from(Customer.class);
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
        
        cq.select(root);
        if(predicates.size()>0){
            cq.where(predicates.toArray(new Predicate[0]));
        }
        
        TypedQuery<Customer> query = entityManager.createQuery(cq);
        
        int iTotal = query.getResultList().size();

        
        
        List<Customer> result = query.setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        
        Page<Customer> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<Customer>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }

    public Customer createCustomer(Customer customer) throws BusinessLogicException, ExistentEntityException {
        validateCustomer(customer);
        validateUnique(customer);
        return customerRepository.save(customer);
    }

    private void validateCustomer(Customer customer) throws BusinessLogicException {
        if (StringUtils.isEmpty(customer.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto Customer");
        } else if (StringUtils.isEmpty(customer.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Customer");
        } 
    }
    
    private void validateUnique(Customer customer) throws ExistentEntityException {
        List<Customer> customeres = customerRepository.findByName(customer.getName());
        if (customeres!=null && !customeres.isEmpty()) {
            throw new ExistentEntityException(Customer.class,"name="+customer.getName());
        } 
    }

    public Customer updateCustomer(UUID customerId, Customer customer) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(customer.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Customer");
        } 
    
        Customer persistedCustomer = getById(customerId);
        if (persistedCustomer != null) {
            if(customer.getCity()!=null){
                persistedCustomer.setCity(customer.getCity());
            }
            
            if(customer.getColony()!=null){
                persistedCustomer.setColony(customer.getColony());
            }
            if(customer.getCountry()!=null){
                persistedCustomer.setCountry(customer.getCountry());
            }
            if(customer.getFiscalInfo()!=null){
                persistedCustomer.setFiscalInfo(customer.getFiscalInfo());
            }
            if(customer.getExternalNumber()!=null){
                persistedCustomer.setExternalNumber(customer.getExternalNumber());
            }
            if(customer.getInternalNumber()!=null){
                persistedCustomer.setInternalNumber(customer.getInternalNumber());
            }
            if(customer.getName()!=null){
                persistedCustomer.setName(customer.getName());
            }
            if(customer.getState()!=null){
                persistedCustomer.setState(customer.getState());
            }
            if(customer.getStreet()!=null){
                persistedCustomer.setStreet(customer.getStreet());
            }
            if(customer.getSuburb()!=null){
                persistedCustomer.setSuburb(customer.getSuburb());
            }
            if(customer.getZipCode()!=null){
                persistedCustomer.setZipCode(customer.getZipCode());
            }
            if(customer.getActive()!=null){
                persistedCustomer.setActive(customer.getActive());
            }
            persistedCustomer.setUpdateUser(customer.getUpdateUser());
            return customerRepository.save(persistedCustomer);
        } else {
            throw new EntityNotExistentException(Customer.class,customerId.toString());
        }
    }

    public Customer deleteCustomer(UUID customerId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto Customer");
        } 
        Customer customer = getById(customerId);
        customer.setDeleted(Boolean.TRUE);
        customer.setActive(Boolean.FALSE);
        return customerRepository.save(customer);
    }

    public List<Customer> findAll(){
        return customerRepository.findAll();
    }
    
    public Customer getByName(String name){
        return customerRepository.getByName(name);
    }
    
    public List<Customer> findByNameIgnoreCaseContaining(String name){
        return customerRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<Customer> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return customerRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public Customer getBySerial(Integer serial) {
        return customerRepository.getBySerial(serial);
    }
}
