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
import com.digivalle.sentinel.models.AccessControlObjectLog;
import com.digivalle.sentinel.repositories.AccessControlObjectLogRepository;
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
public class AccessControlObjectLogManager {
    
    @Autowired
    private AccessControlObjectLogRepository accessControlObjectLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public AccessControlObjectLog getById(UUID id) throws EntityNotExistentException {
        Optional<AccessControlObjectLog> accessControlObjectLog = accessControlObjectLogRepository.findById(id);
        if (!accessControlObjectLog.isEmpty()) {
            return accessControlObjectLog.get();
        }
        throw new EntityNotExistentException(AccessControlObjectLog.class,id.toString());
    }
    
    public PagedResponse<AccessControlObjectLog> getAccessControlObjectLog(AccessControlObjectLog filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AccessControlObjectLog> cq = cb.createQuery(AccessControlObjectLog.class);
        Root<AccessControlObjectLog> root = cq.from(AccessControlObjectLog.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<AccessControlObjectLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<AccessControlObjectLog> result = query.getResultList();
        
        Page<AccessControlObjectLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(AccessControlObjectLog filter, CriteriaBuilder cb, Root<AccessControlObjectLog> root) {
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
        if(filter.getAccessControlObjectId()!=null){
            predicates.add(cb.equal(root.get("accessControlObjectId"), filter.getAccessControlObjectId()));
        }
        if(filter.getTransactionId()!=null){
            predicates.add(cb.equal(root.get("transactionId"), filter.getTransactionId()));
        }
        if(filter.getAction()!=null){
            predicates.add(cb.equal(root.get("action"), filter.getAction()));
        }

        return predicates;
    }

    private void applySorting(CriteriaQuery<AccessControlObjectLog> cq, CriteriaBuilder cb, Root<AccessControlObjectLog> root, AccessControlObjectLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, AccessControlObjectLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<AccessControlObjectLog> countRoot = countQuery.from(AccessControlObjectLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public AccessControlObjectLog createAccessControlObjectLog(AccessControlObjectLog accessControlObjectLog) throws BusinessLogicException {
        //validateAccessControlObjectLog(accessControlObjectLog);
        //validateUnique(accessControlObjectLog);
        return accessControlObjectLogRepository.save(accessControlObjectLog);
    }

    private void validateAccessControlObjectLog(AccessControlObjectLog accessControlObjectLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(accessControlObjectLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto AccessControlObjectLog");
        } else if (StringUtils.isEmpty(accessControlObjectLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto AccessControlObjectLog");
        } 
    }
    
    private void validateUnique(AccessControlObjectLog accessControlObjectLog) throws ExistentEntityException {
        List<AccessControlObjectLog> accessControlObjectLoges = accessControlObjectLogRepository.findByName(accessControlObjectLog.getName());
        if (accessControlObjectLoges!=null && !accessControlObjectLoges.isEmpty()) {
            throw new ExistentEntityException(AccessControlObjectLog.class,"name="+accessControlObjectLog.getName());
        } 
    }

    public AccessControlObjectLog updateAccessControlObjectLog(UUID accessControlObjectLogId, AccessControlObjectLog accessControlObjectLog) throws EntityNotExistentException {
        AccessControlObjectLog persistedAccessControlObjectLog = getById(accessControlObjectLogId);
        if (persistedAccessControlObjectLog != null) {
            persistedAccessControlObjectLog.setName(accessControlObjectLog.getName());
            return accessControlObjectLogRepository.save(persistedAccessControlObjectLog);
        } else {
            throw new EntityNotExistentException(AccessControlObjectLog.class,accessControlObjectLogId.toString());
        }
    }

    public void deleteAccessControlObjectLog(UUID accessControlObjectLogId) throws EntityNotExistentException {
        AccessControlObjectLog accessControlObjectLog = getById(accessControlObjectLogId);
        accessControlObjectLog.setDeleted(Boolean.TRUE);
        accessControlObjectLog.setActive(Boolean.FALSE);
        accessControlObjectLogRepository.save(accessControlObjectLog);
    }

    public List<AccessControlObjectLog> findAll(){
        return accessControlObjectLogRepository.findAll();
    }
    
    public AccessControlObjectLog getByName(String name){
        return accessControlObjectLogRepository.getByName(name);
    }
    
    public List<AccessControlObjectLog> findByNameIgnoreCaseContaining(String name){
        return accessControlObjectLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<AccessControlObjectLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return accessControlObjectLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}
