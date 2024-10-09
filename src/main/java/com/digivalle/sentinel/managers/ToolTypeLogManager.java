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
import com.digivalle.sentinel.models.ToolTypeLog;
import com.digivalle.sentinel.repositories.ToolTypeLogRepository;
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
public class ToolTypeLogManager {
    
    @Autowired
    private ToolTypeLogRepository toolTypeLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public ToolTypeLog getById(UUID id) throws EntityNotExistentException {
        Optional<ToolTypeLog> toolTypeLog = toolTypeLogRepository.findById(id);
        if (!toolTypeLog.isEmpty()) {
            return toolTypeLog.get();
        }
        throw new EntityNotExistentException(ToolTypeLog.class,id.toString());
    }
    
    public PagedResponse<ToolTypeLog> getToolTypeLog(ToolTypeLog filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ToolTypeLog> cq = cb.createQuery(ToolTypeLog.class);
        Root<ToolTypeLog> root = cq.from(ToolTypeLog.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<ToolTypeLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<ToolTypeLog> result = query.getResultList();
        
        Page<ToolTypeLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(ToolTypeLog filter, CriteriaBuilder cb, Root<ToolTypeLog> root) {
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
        if(filter.getActive()!=null){
            predicates.add(cb.equal(root.get("active"), filter.getActive()));
        }
        if(filter.getDeleted()!=null){
            predicates.add(cb.equal(root.get("deleted"), filter.getDeleted()));
        }
        if(filter.getUpdateUser()!=null){
            predicates.add(cb.equal(root.get("updateUser"), filter.getUpdateUser()));
        }
        if(filter.getToolTypeId()!=null){
            predicates.add(cb.equal(root.get("toolTypeId"), filter.getToolTypeId()));
        }
        if(filter.getTransactionId()!=null){
            predicates.add(cb.equal(root.get("transactionId"), filter.getTransactionId()));
        }
        if(filter.getAction()!=null){
            predicates.add(cb.equal(root.get("action"), filter.getAction()));
        }

        return predicates;
    }

    private void applySorting(CriteriaQuery<ToolTypeLog> cq, CriteriaBuilder cb, Root<ToolTypeLog> root, ToolTypeLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, ToolTypeLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ToolTypeLog> countRoot = countQuery.from(ToolTypeLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    

    public ToolTypeLog createToolTypeLog(ToolTypeLog toolTypeLog) throws BusinessLogicException {
        //validateToolTypeLog(toolTypeLog);
        //validateUnique(toolTypeLog);
        return toolTypeLogRepository.save(toolTypeLog);
    }

    private void validateToolTypeLog(ToolTypeLog toolTypeLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(toolTypeLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto ToolTypeLog");
        } else if (StringUtils.isEmpty(toolTypeLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto ToolTypeLog");
        } 
    }
    
    private void validateUnique(ToolTypeLog toolTypeLog) throws ExistentEntityException {
        List<ToolTypeLog> toolTypeLoges = toolTypeLogRepository.findByName(toolTypeLog.getName());
        if (toolTypeLoges!=null && !toolTypeLoges.isEmpty()) {
            throw new ExistentEntityException(ToolTypeLog.class,"name="+toolTypeLog.getName());
        } 
    }

    public ToolTypeLog updateToolTypeLog(UUID toolTypeLogId, ToolTypeLog toolTypeLog) throws EntityNotExistentException {
        ToolTypeLog persistedToolTypeLog = getById(toolTypeLogId);
        if (persistedToolTypeLog != null) {
            persistedToolTypeLog.setName(toolTypeLog.getName());
            return toolTypeLogRepository.save(persistedToolTypeLog);
        } else {
            throw new EntityNotExistentException(ToolTypeLog.class,toolTypeLogId.toString());
        }
    }

    public void deleteToolTypeLog(UUID toolTypeLogId) throws EntityNotExistentException {
        ToolTypeLog toolTypeLog = getById(toolTypeLogId);
        toolTypeLog.setDeleted(Boolean.TRUE);
        toolTypeLog.setActive(Boolean.FALSE);
        toolTypeLogRepository.save(toolTypeLog);
    }

    public List<ToolTypeLog> findAll(){
        return toolTypeLogRepository.findAll();
    }
    
    public ToolTypeLog getByName(String name){
        return toolTypeLogRepository.getByName(name);
    }
    
    public List<ToolTypeLog> findByNameIgnoreCaseContaining(String name){
        return toolTypeLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<ToolTypeLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return toolTypeLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}
