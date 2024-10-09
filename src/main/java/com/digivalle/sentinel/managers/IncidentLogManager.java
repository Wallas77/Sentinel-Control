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
import com.digivalle.sentinel.models.IncidentLog;
import com.digivalle.sentinel.repositories.IncidentLogRepository;
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
public class IncidentLogManager {
    
    @Autowired
    private IncidentLogRepository incidentLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public IncidentLog getById(UUID id) throws EntityNotExistentException {
        Optional<IncidentLog> incidentLog = incidentLogRepository.findById(id);
        if (!incidentLog.isEmpty()) {
            return incidentLog.get();
        }
        throw new EntityNotExistentException(IncidentLog.class,id.toString());
    }
    
    public PagedResponse<IncidentLog> getIncidentLog(IncidentLog filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<IncidentLog> cq = cb.createQuery(IncidentLog.class);
        Root<IncidentLog> root = cq.from(IncidentLog.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<IncidentLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<IncidentLog> result = query.getResultList();
        
        Page<IncidentLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(IncidentLog filter, CriteriaBuilder cb, Root<IncidentLog> root) {
        List<Predicate> predicates = new ArrayList<>();

        if(filter.getCreationDate()!=null && filter.getCreationDate2()!=null){
            predicates.add(cb.between(root.get("creationDate"), filter.getCreationDate(),filter.getCreationDate2()));
        }
        if(filter.getUpdateDate()!=null && filter.getUpdateDate2()!=null){
            predicates.add(cb.between(root.get("updateDate"), filter.getUpdateDate(),filter.getUpdateDate2()));
        }
        
        if(filter.getEmployee()!=null){
            if(filter.getEmployee().getId()!=null){
                predicates.add(cb.equal(root.get("employee").get("id"), filter.getEmployee().getId()));
            }
            if(filter.getEmployee().getCode()!=null){
                predicates.add(cb.like(cb.lower(root.get("employee").get("code")), "%" + filter.getEmployee().getCode().toLowerCase()+ "%"));
            }
            if(filter.getEmployee().getFirstSurname()!=null){
                predicates.add(cb.like(cb.lower(root.get("employee").get("name")), "%" + filter.getEmployee().getName().toLowerCase()+ "%"));
            }
            if(filter.getEmployee().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("employee").get("firstSurname")), "%" + filter.getEmployee().getFirstSurname().toLowerCase()+ "%"));
            }
        }
        if(filter.getIncidentType()!=null){
            if(filter.getIncidentType().getId()!=null){
                predicates.add(cb.equal(root.get("incidentType").get("id"), filter.getIncidentType().getId()));
            }
            if(filter.getIncidentType().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("incidentType").get("name")), "%" + filter.getIncidentType().getName().toLowerCase()+ "%"));
            }
        }
        if(filter.getService()!=null){
            if(filter.getService().getId()!=null){
                predicates.add(cb.equal(root.get("service").get("id"), filter.getService().getId()));
            }
            if(filter.getService().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("service").get("name")), "%" + filter.getService().getName().toLowerCase()+ "%"));
            }
        }
        if(filter.getNotes()!=null){
            predicates.add(cb.like(cb.lower(root.get("notes")), "%" + filter.getNotes().toLowerCase()+ "%"));
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

        if(filter.getIncidentId()!=null){
            predicates.add(cb.equal(root.get("incidentId"), filter.getIncidentId()));
        }
        if(filter.getTransactionId()!=null){
            predicates.add(cb.equal(root.get("transactionId"), filter.getTransactionId()));
        }
        if(filter.getAction()!=null){
            predicates.add(cb.equal(root.get("action"), filter.getAction()));
        }

        return predicates;
    }

    private void applySorting(CriteriaQuery<IncidentLog> cq, CriteriaBuilder cb, Root<IncidentLog> root, IncidentLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, IncidentLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<IncidentLog> countRoot = countQuery.from(IncidentLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public IncidentLog createIncidentLog(IncidentLog incidentLog) throws BusinessLogicException {
        //validateIncidentLog(incidentLog);
        //validateUnique(incidentLog);
        return incidentLogRepository.save(incidentLog);
    }

    private void validateIncidentLog(IncidentLog incidentLog) throws BusinessLogicException {
        if (incidentLog.getEmployee()==null) {
            throw new BusinessLogicException("El campo Employee es requerido para el objeto IncidentLog");
        } else if (incidentLog.getIncidentType()==null) {
            throw new BusinessLogicException("El campo IncidentType es requerido para el objeto IncidentLog");
        } else if (incidentLog.getService()==null) {
            throw new BusinessLogicException("El campo Service es requerido para el objeto IncidentLog");
        } 
    }
    
    

    public IncidentLog updateIncidentLog(UUID incidentLogId, IncidentLog incidentLog) throws EntityNotExistentException {
        IncidentLog persistedIncidentLog = getById(incidentLogId);
        if (persistedIncidentLog != null) {
            persistedIncidentLog.setNotes(incidentLog.getNotes());
            return incidentLogRepository.save(persistedIncidentLog);
        } else {
            throw new EntityNotExistentException(IncidentLog.class,incidentLogId.toString());
        }
    }

    public void deleteIncidentLog(UUID incidentLogId) throws EntityNotExistentException {
        IncidentLog incidentLog = getById(incidentLogId);
        incidentLog.setDeleted(Boolean.TRUE);
        incidentLog.setActive(Boolean.FALSE);
        incidentLogRepository.save(incidentLog);
    }

    public List<IncidentLog> findAll(){
        return incidentLogRepository.findAll();
    }
    
    
}
