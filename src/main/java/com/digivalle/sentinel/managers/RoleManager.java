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
import com.digivalle.sentinel.models.Role;
import com.digivalle.sentinel.repositories.RoleRepository;
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
public class RoleManager {
    
    @Autowired
    private RoleRepository roleRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public Role getById(UUID id) throws EntityNotExistentException {
        Optional<Role> role = roleRepository.findById(id);
        if (!role.isEmpty()) {
            return role.get();
        }
        throw new EntityNotExistentException(Role.class,id.toString());
    }
    
    public PagedResponse<Role> getRole(Role filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Role> cq = cb.createQuery(Role.class);
        Root<Role> root = cq.from(Role.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<Role> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<Role> result = query.getResultList();
        
        Page<Role> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(Role filter, CriteriaBuilder cb, Root<Role> root) {
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

        return predicates;
    }

    private void applySorting(CriteriaQuery<Role> cq, CriteriaBuilder cb, Root<Role> root, Role filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, Role filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Role> countRoot = countQuery.from(Role.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    

    public Role createRole(Role role) throws BusinessLogicException, ExistentEntityException {
        validateRole(role);
        validateUnique(role);
        return roleRepository.save(role);
    }

    private void validateRole(Role role) throws BusinessLogicException {
        if (StringUtils.isEmpty(role.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto Role");
        } else if (StringUtils.isEmpty(role.getDescription())) {
            throw new BusinessLogicException("El campo Description es requerido para el objeto Role");
        } else if (StringUtils.isEmpty(role.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Role");
        } 
    }
    
    private void validateUnique(Role role) throws ExistentEntityException {
        List<Role> rolees = roleRepository.findByName(role.getName());
        if (rolees!=null && !rolees.isEmpty()) {
            throw new ExistentEntityException(Role.class,"name="+role.getName());
        } 
    }

    public Role updateRole(UUID roleId, Role role) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(role.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Role");
        } 
    
        Role persistedRole = getById(roleId);
        if (persistedRole != null) {
            if(role.getName()!=null){
                persistedRole.setName(role.getName());
            }
            if(role.getDescription()!=null){
                persistedRole.setDescription(role.getDescription());
            }
            if(role.getCustomer()!=null){
                persistedRole.setCustomer(role.getCustomer());
            }
            if(role.getActive()!=null){
                persistedRole.setActive(role.getActive());
            }
            persistedRole.setUpdateUser(role.getUpdateUser());
            return roleRepository.save(persistedRole);
        } else {
            throw new EntityNotExistentException(Role.class,roleId.toString());
        }
    }

    public Role deleteRole(UUID roleId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto Role");
        } 
        Role role = getById(roleId);
        role.setDeleted(Boolean.TRUE);
        role.setActive(Boolean.FALSE);
        return roleRepository.save(role);
    }

    public List<Role> findAll(){
        return roleRepository.findAll();
    }
    
    public Role getByName(String name){
        return roleRepository.getByName(name);
    }
    
    public List<Role> findByNameIgnoreCaseContaining(String name){
        return roleRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<Role> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return roleRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public Role getBySerial(Integer serial) {
        return roleRepository.getBySerial(serial);
    }
}
