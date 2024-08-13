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
import com.digivalle.sentinel.models.ProfileLog;
import com.digivalle.sentinel.repositories.ProfileLogRepository;
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
public class ProfileLogManager {
    
    @Autowired
    private ProfileLogRepository profileLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public ProfileLog getById(UUID id) throws EntityNotExistentException {
        Optional<ProfileLog> profileLog = profileLogRepository.findById(id);
        if (!profileLog.isEmpty()) {
            return profileLog.get();
        }
        throw new EntityNotExistentException(ProfileLog.class,id.toString());
    }
    
    public PagedResponse<ProfileLog> getProfileLog(ProfileLog filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ProfileLog> cq = cb.createQuery(ProfileLog.class);
        Root<ProfileLog> root = cq.from(ProfileLog.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<ProfileLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<ProfileLog> result = query.getResultList();
        
        Page<ProfileLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(ProfileLog filter, CriteriaBuilder cb, Root<ProfileLog> root) {
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
        if(filter.getProfileId()!=null){
            predicates.add(cb.equal(root.get("profileId"), filter.getProfileId()));
        }
        if(filter.getTransactionId()!=null){
            predicates.add(cb.equal(root.get("transactionId"), filter.getTransactionId()));
        }
        if(filter.getAction()!=null){
            predicates.add(cb.equal(root.get("action"), filter.getAction()));
        }

        return predicates;
    }

    private void applySorting(CriteriaQuery<ProfileLog> cq, CriteriaBuilder cb, Root<ProfileLog> root, ProfileLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, ProfileLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ProfileLog> countRoot = countQuery.from(ProfileLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    public ProfileLog createProfileLog(ProfileLog profileLog) throws BusinessLogicException {
        //validateProfileLog(profileLog);
        //validateUnique(profileLog);
        return profileLogRepository.save(profileLog);
    }

    private void validateProfileLog(ProfileLog profileLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(profileLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto ProfileLog");
        } else if (StringUtils.isEmpty(profileLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto ProfileLog");
        } 
    }
    
    private void validateUnique(ProfileLog profileLog) throws ExistentEntityException {
        List<ProfileLog> profileLoges = profileLogRepository.getByName(profileLog.getName());
        if (profileLoges!=null && !profileLoges.isEmpty()) {
            throw new ExistentEntityException(ProfileLog.class,"name="+profileLog.getName());
        } 
    }

    public ProfileLog updateProfileLog(UUID profileLogId, ProfileLog profileLog) throws EntityNotExistentException {
        ProfileLog persistedProfileLog = getById(profileLogId);
        if (persistedProfileLog != null) {
            persistedProfileLog.setName(profileLog.getName());
            return profileLogRepository.save(persistedProfileLog);
        } else {
            throw new EntityNotExistentException(ProfileLog.class,profileLogId.toString());
        }
    }

    public void deleteProfileLog(UUID profileLogId) throws EntityNotExistentException {
        ProfileLog profileLog = getById(profileLogId);
        profileLog.setDeleted(Boolean.TRUE);
        profileLog.setActive(Boolean.FALSE);
        profileLogRepository.save(profileLog);
    }

    public List<ProfileLog> findAll(){
        return profileLogRepository.findAll();
    }
    
    public List<ProfileLog> getByName(String name){
        return profileLogRepository.getByName(name);
    }
    
    public List<ProfileLog> findByNameIgnoreCaseContaining(String name){
        return profileLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<ProfileLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return profileLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}
