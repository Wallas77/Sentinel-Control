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
import com.digivalle.sentinel.models.ApplicationLog;
import com.digivalle.sentinel.repositories.ApplicationLogRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
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
public class ApplicationLogManager {
    
    @Autowired
    private ApplicationLogRepository applicationLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public ApplicationLog getById(UUID id) throws EntityNotExistentException {
        Optional<ApplicationLog> applicationLog = applicationLogRepository.findById(id);
        if (!applicationLog.isEmpty()) {
            return applicationLog.get();
        }
        throw new EntityNotExistentException(ApplicationLog.class,id.toString());
    }
    
    public PagedResponse<ApplicationLog> getApplicationLog(ApplicationLog filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ApplicationLog> cq = cb.createQuery(ApplicationLog.class);
        Root<ApplicationLog> root = cq.from(ApplicationLog.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<ApplicationLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<ApplicationLog> result = query.getResultList();
        
        Page<ApplicationLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(ApplicationLog filter, CriteriaBuilder cb, Root<ApplicationLog> root) {
        List<Predicate> predicates = new ArrayList<>();

        if(filter.getCreationDate()!=null && filter.getCreationDate2()!=null){
            Calendar cal = Calendar.getInstance();
            cal.setTime(filter.getCreationDate());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            filter.setCreationDate(cal.getTime());
            
            cal = Calendar.getInstance();
            cal.setTime(filter.getCreationDate2());
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            filter.setCreationDate2(cal.getTime());
            predicates.add(cb.between(root.get("creationDate"), filter.getCreationDate(),filter.getCreationDate2()));
        }
        if(filter.getUpdateDate()!=null && filter.getUpdateDate2()!=null){
            Calendar cal = Calendar.getInstance();
            cal.setTime(filter.getUpdateDate());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            filter.setUpdateDate(cal.getTime());
            
            cal = Calendar.getInstance();
            cal.setTime(filter.getUpdateDate2());
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            filter.setUpdateDate2(cal.getTime());
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
        if(filter.getApplicationId()!=null){
            predicates.add(cb.equal(root.get("applicationId"), filter.getApplicationId()));
        }
        if(filter.getTransactionId()!=null){
            predicates.add(cb.equal(root.get("transactionId"), filter.getTransactionId()));
        }
        if(filter.getAction()!=null){
            predicates.add(cb.equal(root.get("action"), filter.getAction()));
        }

        return predicates;
    }

    private void applySorting(CriteriaQuery<ApplicationLog> cq, CriteriaBuilder cb, Root<ApplicationLog> root, ApplicationLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, ApplicationLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ApplicationLog> countRoot = countQuery.from(ApplicationLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public ApplicationLog createApplicationLog(ApplicationLog applicationLog) throws BusinessLogicException {
        //validateApplicationLog(applicationLog);
        //validateUnique(applicationLog);
        return applicationLogRepository.save(applicationLog);
    }

    private void validateApplicationLog(ApplicationLog applicationLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(applicationLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto ApplicationLog");
        } else if (StringUtils.isEmpty(applicationLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto ApplicationLog");
        } 
    }
    
    private void validateUnique(ApplicationLog applicationLog) throws ExistentEntityException {
        List<ApplicationLog> applicationLoges = applicationLogRepository.findByName(applicationLog.getName());
        if (applicationLoges!=null && !applicationLoges.isEmpty()) {
            throw new ExistentEntityException(ApplicationLog.class,"name="+applicationLog.getName());
        } 
    }

    public ApplicationLog updateApplicationLog(UUID applicationLogId, ApplicationLog applicationLog) throws EntityNotExistentException {
        ApplicationLog persistedApplicationLog = getById(applicationLogId);
        if (persistedApplicationLog != null) {
            persistedApplicationLog.setName(applicationLog.getName());
            return applicationLogRepository.save(persistedApplicationLog);
        } else {
            throw new EntityNotExistentException(ApplicationLog.class,applicationLogId.toString());
        }
    }

    public void deleteApplicationLog(UUID applicationLogId) throws EntityNotExistentException {
        ApplicationLog applicationLog = getById(applicationLogId);
        applicationLog.setDeleted(Boolean.TRUE);
        applicationLog.setActive(Boolean.FALSE);
        applicationLogRepository.save(applicationLog);
    }

    public List<ApplicationLog> findAll(){
        return applicationLogRepository.findAll();
    }
    
    public ApplicationLog getByName(String name){
        return applicationLogRepository.getByName(name);
    }
    
    public List<ApplicationLog> findByNameIgnoreCaseContaining(String name){
        return applicationLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<ApplicationLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return applicationLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}
