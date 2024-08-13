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
import com.digivalle.sentinel.models.Supplier;
import com.digivalle.sentinel.repositories.SupplierRepository;
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
public class SupplierManager {
    
    @Autowired
    private SupplierRepository supplierRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public Supplier getById(UUID id) throws EntityNotExistentException {
        Optional<Supplier> supplier = supplierRepository.findById(id);
        if (!supplier.isEmpty()) {
            return supplier.get();
        }
        throw new EntityNotExistentException(Supplier.class,id.toString());
    }
    
    public PagedResponse<Supplier> getSupplier(Supplier filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Supplier> cq = cb.createQuery(Supplier.class);
        Root<Supplier> root = cq.from(Supplier.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<Supplier> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<Supplier> result = query.getResultList();
        
        Page<Supplier> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(Supplier filter, CriteriaBuilder cb, Root<Supplier> root) {
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

    private void applySorting(CriteriaQuery<Supplier> cq, CriteriaBuilder cb, Root<Supplier> root, Supplier filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, Supplier filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Supplier> countRoot = countQuery.from(Supplier.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    

    public Supplier createSupplier(Supplier supplier) throws BusinessLogicException, ExistentEntityException {
        validateSupplier(supplier);
        validateUnique(supplier);
        return supplierRepository.save(supplier);
    }

    private void validateSupplier(Supplier supplier) throws BusinessLogicException {
        if (StringUtils.isEmpty(supplier.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto Supplier");
        } else if (StringUtils.isEmpty(supplier.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Supplier");
        } 
    }
    
    private void validateUnique(Supplier supplier) throws ExistentEntityException {
        List<Supplier> supplieres = supplierRepository.findByName(supplier.getName());
        if (supplieres!=null && !supplieres.isEmpty()) {
            throw new ExistentEntityException(Supplier.class,"name="+supplier.getName());
        } 
    }

    public Supplier updateSupplier(UUID supplierId, Supplier supplier) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(supplier.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Supplier");
        } 
    
        Supplier persistedSupplier = getById(supplierId);
        if (persistedSupplier != null) {
            if(supplier.getCity()!=null){
                persistedSupplier.setCity(supplier.getCity());
            }
            
            if(supplier.getColony()!=null){
                persistedSupplier.setColony(supplier.getColony());
            }
            if(supplier.getCountry()!=null){
                persistedSupplier.setCountry(supplier.getCountry());
            }
            if(supplier.getFiscalInfo()!=null){
                persistedSupplier.setFiscalInfo(supplier.getFiscalInfo());
            }
            if(supplier.getExternalNumber()!=null){
                persistedSupplier.setExternalNumber(supplier.getExternalNumber());
            }
            if(supplier.getInternalNumber()!=null){
                persistedSupplier.setInternalNumber(supplier.getInternalNumber());
            }
            if(supplier.getName()!=null){
                persistedSupplier.setName(supplier.getName());
            }
            if(supplier.getState()!=null){
                persistedSupplier.setState(supplier.getState());
            }
            if(supplier.getStreet()!=null){
                persistedSupplier.setStreet(supplier.getStreet());
            }
            if(supplier.getSuburb()!=null){
                persistedSupplier.setSuburb(supplier.getSuburb());
            }
            if(supplier.getZipCode()!=null){
                persistedSupplier.setZipCode(supplier.getZipCode());
            }
            if(supplier.getActive()!=null){
                persistedSupplier.setActive(supplier.getActive());
            }
            persistedSupplier.setUpdateUser(supplier.getUpdateUser());
            return supplierRepository.save(persistedSupplier);
        } else {
            throw new EntityNotExistentException(Supplier.class,supplierId.toString());
        }
    }

    public Supplier deleteSupplier(UUID supplierId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto Supplier");
        } 
        Supplier supplier = getById(supplierId);
        supplier.setDeleted(Boolean.TRUE);
        supplier.setActive(Boolean.FALSE);
        return supplierRepository.save(supplier);
    }

    public List<Supplier> findAll(){
        return supplierRepository.findAll();
    }
    
    public Supplier getByName(String name){
        return supplierRepository.getByName(name);
    }
    
    public List<Supplier> findByNameIgnoreCaseContaining(String name){
        return supplierRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<Supplier> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return supplierRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public Supplier getBySerial(Integer serial) {
        return supplierRepository.getBySerial(serial);
    }
}
