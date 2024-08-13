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
import com.digivalle.sentinel.models.IncidentTypeLog;
import com.digivalle.sentinel.repositories.IncidentTypeLogRepository;
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
public class IncidentTypeLogManager {
    
    @Autowired
    private IncidentTypeLogRepository incidentTypeLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public IncidentTypeLog getById(UUID id) throws EntityNotExistentException {
        Optional<IncidentTypeLog> incidentTypeLog = incidentTypeLogRepository.findById(id);
        if (!incidentTypeLog.isEmpty()) {
            return incidentTypeLog.get();
        }
        throw new EntityNotExistentException(IncidentTypeLog.class,id.toString());
    }
    
    public PagedResponse<IncidentTypeLog> getIncidentTypeLog(IncidentTypeLog filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<IncidentTypeLog> cq = cb.createQuery(IncidentTypeLog.class);
        Root<IncidentTypeLog> root = cq.from(IncidentTypeLog.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<IncidentTypeLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<IncidentTypeLog> result = query.getResultList();
        
        Page<IncidentTypeLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(IncidentTypeLog filter, CriteriaBuilder cb, Root<IncidentTypeLog> root) {
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

    private void applySorting(CriteriaQuery<IncidentTypeLog> cq, CriteriaBuilder cb, Root<IncidentTypeLog> root, IncidentTypeLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, IncidentTypeLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<IncidentTypeLog> countRoot = countQuery.from(IncidentTypeLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    
    public IncidentTypeLog createIncidentTypeLog(IncidentTypeLog incidentTypeLog) throws BusinessLogicException {
        //validateIncidentTypeLog(incidentTypeLog);
        //validateUnique(incidentTypeLog);
        return incidentTypeLogRepository.save(incidentTypeLog);
    }

    private void validateIncidentTypeLog(IncidentTypeLog incidentTypeLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(incidentTypeLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto IncidentTypeLog");
        } else if (StringUtils.isEmpty(incidentTypeLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto IncidentTypeLog");
        } 
    }
    
    private void validateUnique(IncidentTypeLog incidentTypeLog) throws ExistentEntityException {
        List<IncidentTypeLog> incidentTypeLoges = incidentTypeLogRepository.findByName(incidentTypeLog.getName());
        if (incidentTypeLoges!=null && !incidentTypeLoges.isEmpty()) {
            throw new ExistentEntityException(IncidentTypeLog.class,"name="+incidentTypeLog.getName());
        } 
    }

    public IncidentTypeLog updateIncidentTypeLog(UUID incidentTypeLogId, IncidentTypeLog incidentTypeLog) throws EntityNotExistentException {
        IncidentTypeLog persistedIncidentTypeLog = getById(incidentTypeLogId);
        if (persistedIncidentTypeLog != null) {
            persistedIncidentTypeLog.setName(incidentTypeLog.getName());
            return incidentTypeLogRepository.save(persistedIncidentTypeLog);
        } else {
            throw new EntityNotExistentException(IncidentTypeLog.class,incidentTypeLogId.toString());
        }
    }

    public void deleteIncidentTypeLog(UUID incidentTypeLogId) throws EntityNotExistentException {
        IncidentTypeLog incidentTypeLog = getById(incidentTypeLogId);
        incidentTypeLog.setDeleted(Boolean.TRUE);
        incidentTypeLog.setActive(Boolean.FALSE);
        incidentTypeLogRepository.save(incidentTypeLog);
    }

    public List<IncidentTypeLog> findAll(){
        return incidentTypeLogRepository.findAll();
    }
    
    public IncidentTypeLog getByName(String name){
        return incidentTypeLogRepository.getByName(name);
    }
    
    public List<IncidentTypeLog> findByNameIgnoreCaseContaining(String name){
        return incidentTypeLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<IncidentTypeLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return incidentTypeLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}
