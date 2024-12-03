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
import com.digivalle.sentinel.models.ActivityFile;
import com.digivalle.sentinel.repositories.ActivityFileRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
public class ActivityFileManager {
    
    @Autowired
    private ActivityFileRepository activityFileRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public ActivityFile getById(UUID id) throws EntityNotExistentException {
        Optional<ActivityFile> activityFile = activityFileRepository.findById(id);
        if (!activityFile.isEmpty()) {
            return activityFile.get();
        }
        throw new EntityNotExistentException(ActivityFile.class,id.toString());
    }
    
    public PagedResponse<ActivityFile> getActivityFile(ActivityFile filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ActivityFile> cq = cb.createQuery(ActivityFile.class);
        Root<ActivityFile> root = cq.from(ActivityFile.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<ActivityFile> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<ActivityFile> result = query.getResultList();
        
        Page<ActivityFile> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(ActivityFile filter, CriteriaBuilder cb, Root<ActivityFile> root) {
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
        if(filter.getFileFormat()!=null){
            predicates.add(cb.like(cb.lower(root.get("fileFormat")), "%" + filter.getFileFormat().toLowerCase()+ "%"));
        }
        if(filter.getActivity()!=null){
            if(filter.getActivity().getId()!=null){
                predicates.add(cb.equal(root.get("activity").get("id"), filter.getActivity().getId()));
            }
        }
        return predicates;
    }

    private void applySorting(CriteriaQuery<ActivityFile> cq, CriteriaBuilder cb, Root<ActivityFile> root, ActivityFile filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, ActivityFile filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ActivityFile> countRoot = countQuery.from(ActivityFile.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    

    public ActivityFile createActivityFile(ActivityFile activityFile) throws BusinessLogicException, ExistentEntityException {
        validateActivityFile(activityFile);
        //validateUnique(activityFile);
        return activityFileRepository.save(activityFile);
    }

    private void validateActivityFile(ActivityFile activityFile) throws BusinessLogicException {
        if (StringUtils.isEmpty(activityFile.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto ActivityFile");
        } else if (StringUtils.isEmpty(activityFile.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto ActivityFile");
        } 
    }
    
    private void validateUnique(ActivityFile activityFile) throws ExistentEntityException {
        List<ActivityFile> activityFilees = activityFileRepository.findByName(activityFile.getName());
        if (activityFilees!=null && !activityFilees.isEmpty()) {
            throw new ExistentEntityException(ActivityFile.class,"name="+activityFile.getName());
        } 
    }

    public ActivityFile updateActivityFile(UUID activityFileId, ActivityFile activityFile) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(activityFile.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto ActivityFile");
        } 
    
        ActivityFile persistedActivityFile = getById(activityFileId);
        if (persistedActivityFile != null) {
            if(activityFile.getName()!=null){
                persistedActivityFile.setName(activityFile.getName());
            }
            if(activityFile.getFile()!=null){
                persistedActivityFile.setFile(activityFile.getFile());
            }
            if(activityFile.getFileFormat()!=null){
                persistedActivityFile.setFileFormat(activityFile.getFileFormat());
            }
            
            if(activityFile.getActive()!=null){
                persistedActivityFile.setActive(activityFile.getActive());
            }
            persistedActivityFile.setUpdateUser(activityFile.getUpdateUser());
            return activityFileRepository.save(persistedActivityFile);
        } else {
            throw new EntityNotExistentException(ActivityFile.class,activityFileId.toString());
        }
    }

    public ActivityFile deleteActivityFile(UUID activityFileId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto ActivityFile");
        } 
        ActivityFile activityFile = getById(activityFileId);
        activityFile.setDeleted(Boolean.TRUE);
        activityFile.setActive(Boolean.FALSE);
        return activityFileRepository.save(activityFile);
    }

    public List<ActivityFile> findAll(){
        return activityFileRepository.findAll();
    }
    
    public ActivityFile getByName(String name){
        return activityFileRepository.getByName(name);
    }
    
    public List<ActivityFile> findByNameIgnoreCaseContaining(String name){
        return activityFileRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<ActivityFile> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return activityFileRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public ActivityFile getBySerial(Integer serial) {
        return activityFileRepository.getBySerial(serial);
    }
}
