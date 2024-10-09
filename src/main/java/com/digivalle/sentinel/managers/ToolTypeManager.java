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
import com.digivalle.sentinel.models.ToolType;
import com.digivalle.sentinel.repositories.ToolTypeRepository;
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
public class ToolTypeManager {
    
    @Autowired
    private ToolTypeRepository toolTypeRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public ToolType getById(UUID id) throws EntityNotExistentException {
        Optional<ToolType> toolType = toolTypeRepository.findById(id);
        if (!toolType.isEmpty()) {
            return toolType.get();
        }
        throw new EntityNotExistentException(ToolType.class,id.toString());
    }
    
    public PagedResponse<ToolType> getToolType(ToolType filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ToolType> cq = cb.createQuery(ToolType.class);
        Root<ToolType> root = cq.from(ToolType.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<ToolType> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<ToolType> result = query.getResultList();
        
        Page<ToolType> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(ToolType filter, CriteriaBuilder cb, Root<ToolType> root) {
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

    private void applySorting(CriteriaQuery<ToolType> cq, CriteriaBuilder cb, Root<ToolType> root, ToolType filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, ToolType filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ToolType> countRoot = countQuery.from(ToolType.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    

    public ToolType createToolType(ToolType toolType) throws BusinessLogicException, ExistentEntityException {
        validateToolType(toolType);
        validateUnique(toolType);
        return toolTypeRepository.save(toolType);
    }

    private void validateToolType(ToolType toolType) throws BusinessLogicException {
        if (StringUtils.isEmpty(toolType.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto ToolType");
        } else if (StringUtils.isEmpty(toolType.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto ToolType");
        } 
    }
    
    private void validateUnique(ToolType toolType) throws ExistentEntityException {
        List<ToolType> toolTypees = toolTypeRepository.findByName(toolType.getName());
        if (toolTypees!=null && !toolTypees.isEmpty()) {
            throw new ExistentEntityException(ToolType.class,"name="+toolType.getName());
        } 
    }

    public ToolType updateToolType(UUID toolTypeId, ToolType toolType) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(toolType.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto ToolType");
        } 
    
        ToolType persistedToolType = getById(toolTypeId);
        if (persistedToolType != null) {
            if(toolType.getName()!=null){
                persistedToolType.setName(toolType.getName());
            }
            
            if(toolType.getActive()!=null){
                persistedToolType.setActive(toolType.getActive());
            }
            persistedToolType.setUpdateUser(toolType.getUpdateUser());
            return toolTypeRepository.save(persistedToolType);
        } else {
            throw new EntityNotExistentException(ToolType.class,toolTypeId.toString());
        }
    }

    public ToolType deleteToolType(UUID toolTypeId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto ToolType");
        } 
        ToolType toolType = getById(toolTypeId);
        toolType.setDeleted(Boolean.TRUE);
        toolType.setActive(Boolean.FALSE);
        return toolTypeRepository.save(toolType);
    }

    public List<ToolType> findAll(){
        return toolTypeRepository.findAll();
    }
    
    public ToolType getByName(String name){
        return toolTypeRepository.getByName(name);
    }
    
    public List<ToolType> findByNameIgnoreCaseContaining(String name){
        return toolTypeRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<ToolType> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return toolTypeRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public ToolType getBySerial(Integer serial) {
        return toolTypeRepository.getBySerial(serial);
    }
}
