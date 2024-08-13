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
import com.digivalle.sentinel.models.RoleResponsabilityLog;
import com.digivalle.sentinel.repositories.RoleResponsabilityLogRepository;
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
public class RoleResponsabilityLogManager {
    
    @Autowired
    private RoleResponsabilityLogRepository roleResponsabilityLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public RoleResponsabilityLog getById(UUID id) throws EntityNotExistentException {
        Optional<RoleResponsabilityLog> roleResponsabilityLog = roleResponsabilityLogRepository.findById(id);
        if (!roleResponsabilityLog.isEmpty()) {
            return roleResponsabilityLog.get();
        }
        throw new EntityNotExistentException(RoleResponsabilityLog.class,id.toString());
    }
    
    public PagedResponse<RoleResponsabilityLog> getRoleResponsabilityLog(RoleResponsabilityLog filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<RoleResponsabilityLog> cq = cb.createQuery(RoleResponsabilityLog.class);
        Root<RoleResponsabilityLog> root = cq.from(RoleResponsabilityLog.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<RoleResponsabilityLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<RoleResponsabilityLog> result = query.getResultList();
        
        Page<RoleResponsabilityLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(RoleResponsabilityLog filter, CriteriaBuilder cb, Root<RoleResponsabilityLog> root) {
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
        if(filter.getRoleResponsabilityId()!=null){
            predicates.add(cb.equal(root.get("roleResponsabilityId"), filter.getRoleResponsabilityId()));
        }
        if(filter.getTransactionId()!=null){
            predicates.add(cb.equal(root.get("transactionId"), filter.getTransactionId()));
        }
        if(filter.getAction()!=null){
            predicates.add(cb.equal(root.get("action"), filter.getAction()));
        }

        return predicates;
    }

    private void applySorting(CriteriaQuery<RoleResponsabilityLog> cq, CriteriaBuilder cb, Root<RoleResponsabilityLog> root, RoleResponsabilityLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, RoleResponsabilityLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<RoleResponsabilityLog> countRoot = countQuery.from(RoleResponsabilityLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    

    public RoleResponsabilityLog createRoleResponsabilityLog(RoleResponsabilityLog roleResponsabilityLog) throws BusinessLogicException {
        //validateRoleResponsabilityLog(roleResponsabilityLog);
        //validateUnique(roleResponsabilityLog);
        return roleResponsabilityLogRepository.save(roleResponsabilityLog);
    }

    private void validateRoleResponsabilityLog(RoleResponsabilityLog roleResponsabilityLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(roleResponsabilityLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto RoleResponsabilityLog");
        } else if (StringUtils.isEmpty(roleResponsabilityLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto RoleResponsabilityLog");
        } 
    }
    
    private void validateUnique(RoleResponsabilityLog roleResponsabilityLog) throws ExistentEntityException {
        List<RoleResponsabilityLog> roleResponsabilityLoges = roleResponsabilityLogRepository.findByName(roleResponsabilityLog.getName());
        if (roleResponsabilityLoges!=null && !roleResponsabilityLoges.isEmpty()) {
            throw new ExistentEntityException(RoleResponsabilityLog.class,"name="+roleResponsabilityLog.getName());
        } 
    }

    public RoleResponsabilityLog updateRoleResponsabilityLog(UUID roleResponsabilityLogId, RoleResponsabilityLog roleResponsabilityLog) throws EntityNotExistentException {
        RoleResponsabilityLog persistedRoleResponsabilityLog = getById(roleResponsabilityLogId);
        if (persistedRoleResponsabilityLog != null) {
            persistedRoleResponsabilityLog.setName(roleResponsabilityLog.getName());
            return roleResponsabilityLogRepository.save(persistedRoleResponsabilityLog);
        } else {
            throw new EntityNotExistentException(RoleResponsabilityLog.class,roleResponsabilityLogId.toString());
        }
    }

    public void deleteRoleResponsabilityLog(UUID roleResponsabilityLogId) throws EntityNotExistentException {
        RoleResponsabilityLog roleResponsabilityLog = getById(roleResponsabilityLogId);
        roleResponsabilityLog.setDeleted(Boolean.TRUE);
        roleResponsabilityLog.setActive(Boolean.FALSE);
        roleResponsabilityLogRepository.save(roleResponsabilityLog);
    }

    public List<RoleResponsabilityLog> findAll(){
        return roleResponsabilityLogRepository.findAll();
    }
    
    public RoleResponsabilityLog getByName(String name){
        return roleResponsabilityLogRepository.getByName(name);
    }
    
    public List<RoleResponsabilityLog> findByNameIgnoreCaseContaining(String name){
        return roleResponsabilityLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<RoleResponsabilityLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return roleResponsabilityLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}
