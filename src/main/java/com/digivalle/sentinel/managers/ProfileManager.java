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
import com.digivalle.sentinel.models.Profile;
import com.digivalle.sentinel.repositories.ProfileRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.sql.Timestamp;
import java.time.Instant;
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
public class ProfileManager {
    
    @Autowired
    private ProfileRepository profileRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public Profile getById(UUID id) throws EntityNotExistentException {
        Optional<Profile> profile = profileRepository.findById(id);
        if (!profile.isEmpty()) {
            return profile.get();
        }
        throw new EntityNotExistentException(Profile.class,id.toString());
    }
    
    public PagedResponse<Profile> getProfile(Profile filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Profile> cq = cb.createQuery(Profile.class);
        Root<Profile> root = cq.from(Profile.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<Profile> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<Profile> result = query.getResultList();
        
        Page<Profile> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(Profile filter, CriteriaBuilder cb, Root<Profile> root) {
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
        if(filter.getSerial()!=null){
            predicates.add(cb.equal(root.get("serial"), filter.getSerial()));
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

        return predicates;
    }

    private void applySorting(CriteriaQuery<Profile> cq, CriteriaBuilder cb, Root<Profile> root, Profile filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, Profile filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Profile> countRoot = countQuery.from(Profile.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    public Profile createProfile(Profile profile) throws BusinessLogicException, ExistentEntityException {
        validateProfile(profile);
        validateUnique(profile);
        return profileRepository.save(profile);
    }

    private void validateProfile(Profile profile) throws BusinessLogicException {
        if (StringUtils.isEmpty(profile.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto Profile");
        } else if (StringUtils.isEmpty(profile.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Profile");
        } 
    }
    
    private void validateUnique(Profile profile) throws ExistentEntityException {
        List<Profile> profilees = profileRepository.findByName(profile.getName());
        if (profilees!=null && !profilees.isEmpty()) {
            throw new ExistentEntityException(Profile.class,"name="+profile.getName());
        } 
    }

    public Profile updateProfile(UUID profileId, Profile profile) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(profile.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Profile");
        } 
        Profile persistedProfile = getById(profileId);
        if (persistedProfile != null) {
            if(profile.getName()!=null){
                persistedProfile.setName(profile.getName());
            }
            if(profile.getActive()!=null){
                persistedProfile.setActive(profile.getActive());
            }
            persistedProfile.setUpdateUser(profile.getUpdateUser());
            persistedProfile.setUpdateDate(Timestamp.from(Instant.now()));
            return profileRepository.save(persistedProfile);
        } else {
            throw new EntityNotExistentException(Profile.class,profileId.toString());
        }
    }

    public Profile deleteProfile(UUID profileId) throws EntityNotExistentException {
        Profile profile = getById(profileId);
        profile.setDeleted(Boolean.TRUE);
        profile.setActive(Boolean.FALSE);
        return profileRepository.save(profile);
    }

    public List<Profile> findAll(){
        return profileRepository.findAll();
    }
    
    public List<Profile> findByName(String name){
        return profileRepository.findByName(name);
    }
    
    public List<Profile> findByNameIgnoreCaseContaining(String name){
        return profileRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<Profile> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return profileRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public Profile getBySerial(Integer serial) {
        return profileRepository.getBySerial(serial);
    }
}
