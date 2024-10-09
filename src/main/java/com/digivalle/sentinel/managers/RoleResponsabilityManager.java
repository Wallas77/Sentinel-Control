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
import com.digivalle.sentinel.models.RoleResponsability;
import com.digivalle.sentinel.repositories.RoleResponsabilityRepository;
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
public class RoleResponsabilityManager {
    
    @Autowired
    private RoleResponsabilityRepository roleResponsabilityRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public RoleResponsability getById(UUID id) throws EntityNotExistentException {
        Optional<RoleResponsability> roleResponsability = roleResponsabilityRepository.findById(id);
        if (!roleResponsability.isEmpty()) {
            return roleResponsability.get();
        }
        throw new EntityNotExistentException(RoleResponsability.class,id.toString());
    }
    
    public PagedResponse<RoleResponsability> getRoleResponsability(RoleResponsability filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<RoleResponsability> cq = cb.createQuery(RoleResponsability.class);
        Root<RoleResponsability> root = cq.from(RoleResponsability.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<RoleResponsability> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<RoleResponsability> result = query.getResultList();
        
        Page<RoleResponsability> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(RoleResponsability filter, CriteriaBuilder cb, Root<RoleResponsability> root) {
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
        if(filter.getRecurrence()!=null){
            predicates.add(cb.equal(root.get("recurrence"), filter.getRecurrence()));
        }
        if(filter.getTimePeriod()!=null){
            predicates.add(cb.equal(root.get("timePeriod"), filter.getTimePeriod()));
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
        if(filter.getRole()!=null){
            if(filter.getRole().getId()!=null){
                predicates.add(cb.equal(root.get("role").get("id"), filter.getRole().getId()));
            }
            if(filter.getRole().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("role").get("name")), "%" + filter.getRole().getName().toLowerCase()+ "%"));
            }
        }
        return predicates;
    }

    private void applySorting(CriteriaQuery<RoleResponsability> cq, CriteriaBuilder cb, Root<RoleResponsability> root, RoleResponsability filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, RoleResponsability filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<RoleResponsability> countRoot = countQuery.from(RoleResponsability.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    

    public RoleResponsability createRoleResponsability(RoleResponsability roleResponsability) throws BusinessLogicException, ExistentEntityException {
        validateRoleResponsability(roleResponsability);
        validateUnique(roleResponsability);
        return roleResponsabilityRepository.save(roleResponsability);
    }

    private void validateRoleResponsability(RoleResponsability roleResponsability) throws BusinessLogicException {
        if (StringUtils.isEmpty(roleResponsability.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto RoleResponsability");
        } else if (StringUtils.isEmpty(roleResponsability.getDescription())) {
            throw new BusinessLogicException("El campo Description es requerido para el objeto RoleResponsability");
        } else if (StringUtils.isEmpty(roleResponsability.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto RoleResponsability");
        } 
    }
    
    private void validateUnique(RoleResponsability roleResponsability) throws ExistentEntityException {
        List<RoleResponsability> roleResponsabilityes = roleResponsabilityRepository.findByName(roleResponsability.getName());
        if (roleResponsabilityes!=null && !roleResponsabilityes.isEmpty()) {
            throw new ExistentEntityException(RoleResponsability.class,"name="+roleResponsability.getName());
        } 
    }

    public RoleResponsability updateRoleResponsability(UUID roleResponsabilityId, RoleResponsability roleResponsability) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(roleResponsability.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto RoleResponsability");
        } 
    
        RoleResponsability persistedRoleResponsability = getById(roleResponsabilityId);
        if (persistedRoleResponsability != null) {
            if(roleResponsability.getName()!=null){
                persistedRoleResponsability.setName(roleResponsability.getName());
            }
            if(roleResponsability.getDescription()!=null){
                persistedRoleResponsability.setDescription(roleResponsability.getDescription());
            }
            if(roleResponsability.getRecurrence()!=null){
                persistedRoleResponsability.setRecurrence(roleResponsability.getRecurrence());
            }
            if(roleResponsability.getTimePeriod()!=null){
                persistedRoleResponsability.setTimePeriod(roleResponsability.getTimePeriod());
            }
            if(roleResponsability.getRole()!=null){
                persistedRoleResponsability.setRole(roleResponsability.getRole());
            }
            if(roleResponsability.getActive()!=null){
                persistedRoleResponsability.setActive(roleResponsability.getActive());
            }
            persistedRoleResponsability.setUpdateUser(roleResponsability.getUpdateUser());
            return roleResponsabilityRepository.save(persistedRoleResponsability);
        } else {
            throw new EntityNotExistentException(RoleResponsability.class,roleResponsabilityId.toString());
        }
    }

    public RoleResponsability deleteRoleResponsability(UUID roleResponsabilityId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto RoleResponsability");
        } 
        RoleResponsability roleResponsability = getById(roleResponsabilityId);
        roleResponsability.setDeleted(Boolean.TRUE);
        roleResponsability.setActive(Boolean.FALSE);
        return roleResponsabilityRepository.save(roleResponsability);
    }

    public List<RoleResponsability> findAll(){
        return roleResponsabilityRepository.findAll();
    }
    
    public RoleResponsability getByName(String name){
        return roleResponsabilityRepository.getByName(name);
    }
    
    public List<RoleResponsability> findByNameIgnoreCaseContaining(String name){
        return roleResponsabilityRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<RoleResponsability> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return roleResponsabilityRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public RoleResponsability getBySerial(Integer serial) {
        return roleResponsabilityRepository.getBySerial(serial);
    }
}
