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
import com.digivalle.sentinel.models.IncidentType;
import com.digivalle.sentinel.repositories.IncidentTypeRepository;
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
public class IncidentTypeManager {
    
    @Autowired
    private IncidentTypeRepository incidentTypeRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public IncidentType getById(UUID id) throws EntityNotExistentException {
        Optional<IncidentType> incidentType = incidentTypeRepository.findById(id);
        if (!incidentType.isEmpty()) {
            return incidentType.get();
        }
        throw new EntityNotExistentException(IncidentType.class,id.toString());
    }
    
    public PagedResponse<IncidentType> getIncidentType(IncidentType filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<IncidentType> cq = cb.createQuery(IncidentType.class);
        Root<IncidentType> root = cq.from(IncidentType.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<IncidentType> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<IncidentType> result = query.getResultList();
        
        Page<IncidentType> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(IncidentType filter, CriteriaBuilder cb, Root<IncidentType> root) {
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

    private void applySorting(CriteriaQuery<IncidentType> cq, CriteriaBuilder cb, Root<IncidentType> root, IncidentType filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, IncidentType filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<IncidentType> countRoot = countQuery.from(IncidentType.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    

    public IncidentType createIncidentType(IncidentType incidentType) throws BusinessLogicException, ExistentEntityException {
        validateIncidentType(incidentType);
        validateUnique(incidentType);
        return incidentTypeRepository.save(incidentType);
    }

    private void validateIncidentType(IncidentType incidentType) throws BusinessLogicException {
        if (StringUtils.isEmpty(incidentType.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto IncidentType");
        } else if (StringUtils.isEmpty(incidentType.getDescription())) {
            throw new BusinessLogicException("El campo Description es requerido para el objeto IncidentType");
        } else if (StringUtils.isEmpty(incidentType.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto IncidentType");
        } 
    }
    
    private void validateUnique(IncidentType incidentType) throws ExistentEntityException {
        List<IncidentType> incidentTypees = incidentTypeRepository.findByName(incidentType.getName());
        if (incidentTypees!=null && !incidentTypees.isEmpty()) {
            throw new ExistentEntityException(IncidentType.class,"name="+incidentType.getName());
        } 
    }

    public IncidentType updateIncidentType(UUID incidentTypeId, IncidentType incidentType) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(incidentType.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto IncidentType");
        } 
    
        IncidentType persistedIncidentType = getById(incidentTypeId);
        if (persistedIncidentType != null) {
            if(incidentType.getName()!=null){
                persistedIncidentType.setName(incidentType.getName());
            }
            if(incidentType.getDescription()!=null){
                persistedIncidentType.setDescription(incidentType.getDescription());
            }
            if(incidentType.getBranch()!=null){
                persistedIncidentType.setBranch(incidentType.getBranch());
            }
            if(incidentType.getCustomer()!=null){
                persistedIncidentType.setCustomer(incidentType.getCustomer());
            }
            if(incidentType.getPenaltyAmount()!=null){
                persistedIncidentType.setPenaltyAmount(incidentType.getPenaltyAmount());
            }
            if(incidentType.getPenaltyHours()!=null){
                persistedIncidentType.setPenaltyHours(incidentType.getPenaltyHours());
            }
            if(incidentType.getActive()!=null){
                persistedIncidentType.setActive(incidentType.getActive());
            }
            persistedIncidentType.setUpdateUser(incidentType.getUpdateUser());
            return incidentTypeRepository.save(persistedIncidentType);
        } else {
            throw new EntityNotExistentException(IncidentType.class,incidentTypeId.toString());
        }
    }

    public IncidentType deleteIncidentType(UUID incidentTypeId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto IncidentType");
        } 
        IncidentType incidentType = getById(incidentTypeId);
        incidentType.setDeleted(Boolean.TRUE);
        incidentType.setActive(Boolean.FALSE);
        return incidentTypeRepository.save(incidentType);
    }

    public List<IncidentType> findAll(){
        return incidentTypeRepository.findAll();
    }
    
    public IncidentType getByName(String name){
        return incidentTypeRepository.getByName(name);
    }
    
    public List<IncidentType> findByNameIgnoreCaseContaining(String name){
        return incidentTypeRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<IncidentType> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return incidentTypeRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public IncidentType getBySerial(Integer serial) {
        return incidentTypeRepository.getBySerial(serial);
    }
}
