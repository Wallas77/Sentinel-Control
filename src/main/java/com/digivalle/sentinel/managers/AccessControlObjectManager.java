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
import com.digivalle.sentinel.models.AccessControlObject;
import com.digivalle.sentinel.repositories.AccessControlObjectRepository;
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
public class AccessControlObjectManager {
    
    @Autowired
    private AccessControlObjectRepository accessControlObjectRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public AccessControlObject getById(UUID id) throws EntityNotExistentException {
        Optional<AccessControlObject> accessControlObject = accessControlObjectRepository.findById(id);
        if (!accessControlObject.isEmpty()) {
            return accessControlObject.get();
        }
        throw new EntityNotExistentException(AccessControlObject.class,id.toString());
    }
    
    public PagedResponse<AccessControlObject> getAccessControlObject(AccessControlObject filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AccessControlObject> cq = cb.createQuery(AccessControlObject.class);
        Root<AccessControlObject> root = cq.from(AccessControlObject.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<AccessControlObject> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<AccessControlObject> result = query.getResultList();
        
        Page<AccessControlObject> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(AccessControlObject filter, CriteriaBuilder cb, Root<AccessControlObject> root) {
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
        if(filter.getSerialNumber()!=null){
            predicates.add(cb.like(cb.lower(root.get("serialNumber")), "%" + filter.getSerialNumber().toLowerCase()+ "%"));
        }
        if(filter.getAccessControl()!=null){
            if(filter.getAccessControl().getId()!=null){
                predicates.add(cb.equal(root.get("accessControl").get("id"), filter.getAccessControl().getId()));
            }
            if(filter.getAccessControl().getContact()!=null){
                if(filter.getAccessControl().getContact().getId()!=null){
                    predicates.add(cb.equal(root.get("accessControl").get("contact").get("id"), filter.getAccessControl().getContact().getId()));
                }
                if(filter.getAccessControl().getContact().getName()!=null){
                    predicates.add(cb.like(cb.lower(root.get("accessControl").get("contact").get("name")), "%" + filter.getAccessControl().getContact().getName().toLowerCase()+ "%"));
                }
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

    private void applySorting(CriteriaQuery<AccessControlObject> cq, CriteriaBuilder cb, Root<AccessControlObject> root, AccessControlObject filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, AccessControlObject filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<AccessControlObject> countRoot = countQuery.from(AccessControlObject.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public AccessControlObject createAccessControlObject(AccessControlObject accessControlObject) throws BusinessLogicException, ExistentEntityException {
        validateAccessControlObject(accessControlObject);
        //validateUnique(accessControlObject);
        return accessControlObjectRepository.save(accessControlObject);
    }

    private void validateAccessControlObject(AccessControlObject accessControlObject) throws BusinessLogicException {
        if (StringUtils.isEmpty(accessControlObject.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto AccessControlObject");
        } else if (StringUtils.isEmpty(accessControlObject.getDescription())) {
            throw new BusinessLogicException("El campo Description es requerido para el objeto AccessControlObject");
        } else if (StringUtils.isEmpty(accessControlObject.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto AccessControlObject");
        } else if(accessControlObject.getAccessControl()==null){
            throw new BusinessLogicException("El campo AccessControl es requerido para el objeto AccessControlObject");
        }
    }
    
    private void validateUnique(AccessControlObject accessControlObject) throws ExistentEntityException {
        List<AccessControlObject> accessControlObjectes = accessControlObjectRepository.findByName(accessControlObject.getName());
        if (accessControlObjectes!=null && !accessControlObjectes.isEmpty()) {
            throw new ExistentEntityException(AccessControlObject.class,"name="+accessControlObject.getName());
        } 
    }

    public AccessControlObject updateAccessControlObject(UUID accessControlObjectId, AccessControlObject accessControlObject) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(accessControlObject.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto AccessControlObject");
        } 
    
        AccessControlObject persistedAccessControlObject = getById(accessControlObjectId);
        if (persistedAccessControlObject != null) {
            if(accessControlObject.getAccessControl()!=null){
                persistedAccessControlObject.setAccessControl(accessControlObject.getAccessControl());
            }
            if(accessControlObject.getDescription()!=null){
                persistedAccessControlObject.setDescription(accessControlObject.getDescription());
            }
            if(accessControlObject.getName()!=null){
                persistedAccessControlObject.setName(accessControlObject.getName());
            }
            if(accessControlObject.getSerialNumber()!=null){
                persistedAccessControlObject.setSerialNumber(accessControlObject.getSerialNumber());
            }
            if(accessControlObject.getActive()!=null){
                persistedAccessControlObject.setActive(accessControlObject.getActive());
            }
            persistedAccessControlObject.setUpdateUser(accessControlObject.getUpdateUser());
            return accessControlObjectRepository.save(persistedAccessControlObject);
        } else {
            throw new EntityNotExistentException(AccessControlObject.class,accessControlObjectId.toString());
        }
    }

    public AccessControlObject deleteAccessControlObject(UUID accessControlObjectId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto AccessControlObject");
        } 
        AccessControlObject accessControlObject = getById(accessControlObjectId);
        accessControlObject.setDeleted(Boolean.TRUE);
        accessControlObject.setActive(Boolean.FALSE);
        return accessControlObjectRepository.save(accessControlObject);
    }

    public List<AccessControlObject> findAll(){
        return accessControlObjectRepository.findAll();
    }
    
    public AccessControlObject getByName(String name){
        return accessControlObjectRepository.getByName(name);
    }
    
    public List<AccessControlObject> findByNameIgnoreCaseContaining(String name){
        return accessControlObjectRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<AccessControlObject> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return accessControlObjectRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public AccessControlObject getBySerial(Integer serial) {
        return accessControlObjectRepository.getBySerial(serial);
    }
}
