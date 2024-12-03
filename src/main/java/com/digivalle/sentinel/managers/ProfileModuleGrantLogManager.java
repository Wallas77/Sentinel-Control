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
import com.digivalle.sentinel.models.ProfileModuleGrantLog;
import com.digivalle.sentinel.repositories.ProfileModuleGrantLogRepository;
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
public class ProfileModuleGrantLogManager {
    
    @Autowired
    private ProfileModuleGrantLogRepository profileModuleGrantLogRepository;
    
    @Autowired
    private EntityManager entityManager;
    
    public ProfileModuleGrantLog getById(UUID id) throws EntityNotExistentException {
        Optional<ProfileModuleGrantLog> profileModuleGrantLog = profileModuleGrantLogRepository.findById(id);
        if (!profileModuleGrantLog.isEmpty()) {
            return profileModuleGrantLog.get();
        }
        throw new EntityNotExistentException(ProfileModuleGrantLog.class,id.toString());
    }
    
    public PagedResponse<ProfileModuleGrantLog> getProfileModuleGrantLog(ProfileModuleGrantLog filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProfileModuleGrantLog> cq = cb.createQuery(ProfileModuleGrantLog.class);
        Root<ProfileModuleGrantLog> root = cq.from(ProfileModuleGrantLog.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<ProfileModuleGrantLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<ProfileModuleGrantLog> result = query.getResultList();
        
        Page<ProfileModuleGrantLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(ProfileModuleGrantLog filter, CriteriaBuilder cb, Root<ProfileModuleGrantLog> root) {
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
        
        if(filter.getActive()!=null){
            predicates.add(cb.equal(root.get("active"), filter.getActive()));
        }
        if(filter.getDeleted()!=null){
            predicates.add(cb.equal(root.get("deleted"), filter.getDeleted()));
        }
        if(filter.getUpdateUser()!=null){
            predicates.add(cb.equal(root.get("updateUser"), filter.getUpdateUser()));
        }
        if(filter.getProfileModuleGrantId()!=null){
            predicates.add(cb.equal(root.get("profileModuleGrantId"), filter.getProfileModuleGrantId()));
        }
        if(filter.getTransactionId()!=null){
            predicates.add(cb.equal(root.get("transactionId"), filter.getTransactionId()));
        }
        if(filter.getAction()!=null){
            predicates.add(cb.equal(root.get("action"), filter.getAction()));
        }
        
        return predicates;
    }

    private void applySorting(CriteriaQuery<ProfileModuleGrantLog> cq, CriteriaBuilder cb, Root<ProfileModuleGrantLog> root, ProfileModuleGrantLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, ProfileModuleGrantLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ProfileModuleGrantLog> countRoot = countQuery.from(ProfileModuleGrantLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    public ProfileModuleGrantLog createProfileModuleGrantLog(ProfileModuleGrantLog profileModuleGrantLog) throws BusinessLogicException {
        validateProfileModuleGrantLog(profileModuleGrantLog);
        return profileModuleGrantLogRepository.save(profileModuleGrantLog);
    }

    private void validateProfileModuleGrantLog(ProfileModuleGrantLog profileModuleGrantLog) throws BusinessLogicException {
        if (profileModuleGrantLog.getProfile()==null) {
            throw new BusinessLogicException("El campo Profile es requerido para el objeto ProfileModuleGrantLog");
        } else if (profileModuleGrantLog.getModule()==null) {
            throw new BusinessLogicException("El campo Module es requerido para el objeto ProfileModuleGrantLog");
        } else if (profileModuleGrantLog.getGrant()==null) {
            throw new BusinessLogicException("El campo Grant es requerido para el objeto ProfileModuleGrantLog");
        } else if (StringUtils.isEmpty(profileModuleGrantLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto ProfileModuleGrantLog");
        } 
    }
    
    

    public ProfileModuleGrantLog updateProfileModuleGrantLog(UUID profileModuleGrantLogId, ProfileModuleGrantLog profileModuleGrantLog) throws EntityNotExistentException, BusinessLogicException {
        validateProfileModuleGrantLog(profileModuleGrantLog);
        ProfileModuleGrantLog persistedProfileModuleGrantLog = getById(profileModuleGrantLogId);
        if (persistedProfileModuleGrantLog != null) {
            if(profileModuleGrantLog.getProfile()!=null){
                persistedProfileModuleGrantLog.setProfile(profileModuleGrantLog.getProfile());
            }
            if(profileModuleGrantLog.getModule()!=null){
                persistedProfileModuleGrantLog.setModule(profileModuleGrantLog.getModule());
            }
            if(profileModuleGrantLog.getGrant()!=null){
                persistedProfileModuleGrantLog.setGrant(profileModuleGrantLog.getGrant());
            }
            if(profileModuleGrantLog.getActive()!=null){
                persistedProfileModuleGrantLog.setActive(profileModuleGrantLog.getActive());
            }
            persistedProfileModuleGrantLog.setUpdateUser(profileModuleGrantLog.getUpdateUser());
            return profileModuleGrantLogRepository.save(persistedProfileModuleGrantLog);
        } else {
            throw new EntityNotExistentException(ProfileModuleGrantLog.class,profileModuleGrantLogId.toString());
        }
    }

    public ProfileModuleGrantLog deleteProfileModuleGrantLog(UUID profileModuleGrantLogId) throws EntityNotExistentException {
        ProfileModuleGrantLog profileModuleGrantLog = getById(profileModuleGrantLogId);
        profileModuleGrantLog.setDeleted(Boolean.TRUE);
        profileModuleGrantLog.setActive(Boolean.FALSE);
        return profileModuleGrantLogRepository.save(profileModuleGrantLog);
    }

    public List<ProfileModuleGrantLog> findAll(){
        return profileModuleGrantLogRepository.findAll();
    }
    
   
}
