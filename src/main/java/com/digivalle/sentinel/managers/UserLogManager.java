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
import com.digivalle.sentinel.models.UserLog;
import com.digivalle.sentinel.repositories.UserLogRepository;
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
public class UserLogManager {
    
    @Autowired
    private UserLogRepository userLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public UserLog getById(UUID id) throws EntityNotExistentException {
        Optional<UserLog> userLog = userLogRepository.findById(id);
        if (!userLog.isEmpty()) {
            return userLog.get();
        }
        throw new EntityNotExistentException(UserLog.class,id.toString());
    }
    
    public PagedResponse<UserLog> getUserLog(UserLog filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<UserLog> cq = cb.createQuery(UserLog.class);
        Root<UserLog> root = cq.from(UserLog.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<UserLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<UserLog> result = query.getResultList();
        
        Page<UserLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(UserLog filter, CriteriaBuilder cb, Root<UserLog> root) {
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
        if(filter.getUserId()!=null){
            predicates.add(cb.equal(root.get("userId"), filter.getUserId()));
        }
        if(filter.getTransactionId()!=null){
            predicates.add(cb.equal(root.get("transactionId"), filter.getTransactionId()));
        }
        if(filter.getAction()!=null){
            predicates.add(cb.equal(root.get("action"), filter.getAction()));
        }
        
        return predicates;
    }

    private void applySorting(CriteriaQuery<UserLog> cq, CriteriaBuilder cb, Root<UserLog> root, UserLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, UserLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<UserLog> countRoot = countQuery.from(UserLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    public UserLog createUserLog(UserLog userLog) throws BusinessLogicException {
        //validateUserLog(userLog);
        //validateUnique(userLog);
        return userLogRepository.save(userLog);
    }

    private void validateUserLog(UserLog userLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(userLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto UserLog");
        } else if (StringUtils.isEmpty(userLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto UserLog");
        } 
    }
    
    private void validateUnique(UserLog userLog) throws ExistentEntityException {
        List<UserLog> userLoges = userLogRepository.getByName(userLog.getName());
        if (userLoges!=null && !userLoges.isEmpty()) {
            throw new ExistentEntityException(UserLog.class,"name="+userLog.getName());
        } 
    }

    public UserLog updateUserLog(UUID userLogId, UserLog userLog) throws EntityNotExistentException {
        UserLog persistedUserLog = getById(userLogId);
        if (persistedUserLog != null) {
            persistedUserLog.setName(userLog.getName());
            return userLogRepository.save(persistedUserLog);
        } else {
            throw new EntityNotExistentException(UserLog.class,userLogId.toString());
        }
    }

    public void deleteUserLog(UUID userLogId) throws EntityNotExistentException {
        UserLog userLog = getById(userLogId);
        userLog.setDeleted(Boolean.TRUE);
        userLog.setActive(Boolean.FALSE);
        userLogRepository.save(userLog);
    }

    public List<UserLog> findAll(){
        return userLogRepository.findAll();
    }
    
    public List<UserLog> getByName(String name){
        return userLogRepository.getByName(name);
    }
    
    public List<UserLog> findByNameIgnoreCaseContaining(String name){
        return userLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<UserLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return userLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}
