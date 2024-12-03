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
import com.digivalle.sentinel.models.VehicleBrandLog;
import com.digivalle.sentinel.repositories.VehicleBrandLogRepository;
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
public class VehicleBrandLogManager {
    
    @Autowired
    private VehicleBrandLogRepository vehicleBrandLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public VehicleBrandLog getById(UUID id) throws EntityNotExistentException {
        Optional<VehicleBrandLog> vehicleBrandLog = vehicleBrandLogRepository.findById(id);
        if (!vehicleBrandLog.isEmpty()) {
            return vehicleBrandLog.get();
        }
        throw new EntityNotExistentException(VehicleBrandLog.class,id.toString());
    }
    
    public PagedResponse<VehicleBrandLog> getVehicleBrandLog(VehicleBrandLog filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<VehicleBrandLog> cq = cb.createQuery(VehicleBrandLog.class);
        Root<VehicleBrandLog> root = cq.from(VehicleBrandLog.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<VehicleBrandLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<VehicleBrandLog> result = query.getResultList();
        
        Page<VehicleBrandLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(VehicleBrandLog filter, CriteriaBuilder cb, Root<VehicleBrandLog> root) {
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
        if(filter.getVehicleBrandId()!=null){
            predicates.add(cb.equal(root.get("vehicleBrandId"), filter.getVehicleBrandId()));
        }
        if(filter.getTransactionId()!=null){
            predicates.add(cb.equal(root.get("transactionId"), filter.getTransactionId()));
        }
        if(filter.getAction()!=null){
            predicates.add(cb.equal(root.get("action"), filter.getAction()));
        }

        return predicates;
    }

    private void applySorting(CriteriaQuery<VehicleBrandLog> cq, CriteriaBuilder cb, Root<VehicleBrandLog> root, VehicleBrandLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, VehicleBrandLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<VehicleBrandLog> countRoot = countQuery.from(VehicleBrandLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public VehicleBrandLog createVehicleBrandLog(VehicleBrandLog vehicleBrandLog) throws BusinessLogicException {
        //validateVehicleBrandLog(vehicleBrandLog);
        //validateUnique(vehicleBrandLog);
        return vehicleBrandLogRepository.save(vehicleBrandLog);
    }

    private void validateVehicleBrandLog(VehicleBrandLog vehicleBrandLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(vehicleBrandLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto VehicleBrandLog");
        } else if (StringUtils.isEmpty(vehicleBrandLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto VehicleBrandLog");
        } 
    }
    
    private void validateUnique(VehicleBrandLog vehicleBrandLog) throws ExistentEntityException {
        List<VehicleBrandLog> vehicleBrandLoges = vehicleBrandLogRepository.findByName(vehicleBrandLog.getName());
        if (vehicleBrandLoges!=null && !vehicleBrandLoges.isEmpty()) {
            throw new ExistentEntityException(VehicleBrandLog.class,"name="+vehicleBrandLog.getName());
        } 
    }

    public VehicleBrandLog updateVehicleBrandLog(UUID vehicleBrandLogId, VehicleBrandLog vehicleBrandLog) throws EntityNotExistentException {
        VehicleBrandLog persistedVehicleBrandLog = getById(vehicleBrandLogId);
        if (persistedVehicleBrandLog != null) {
            persistedVehicleBrandLog.setName(vehicleBrandLog.getName());
            return vehicleBrandLogRepository.save(persistedVehicleBrandLog);
        } else {
            throw new EntityNotExistentException(VehicleBrandLog.class,vehicleBrandLogId.toString());
        }
    }

    public void deleteVehicleBrandLog(UUID vehicleBrandLogId) throws EntityNotExistentException {
        VehicleBrandLog vehicleBrandLog = getById(vehicleBrandLogId);
        vehicleBrandLog.setDeleted(Boolean.TRUE);
        vehicleBrandLog.setActive(Boolean.FALSE);
        vehicleBrandLogRepository.save(vehicleBrandLog);
    }

    public List<VehicleBrandLog> findAll(){
        return vehicleBrandLogRepository.findAll();
    }
    
    public VehicleBrandLog getByName(String name){
        return vehicleBrandLogRepository.getByName(name);
    }
    
    public List<VehicleBrandLog> findByNameIgnoreCaseContaining(String name){
        return vehicleBrandLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<VehicleBrandLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return vehicleBrandLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}
