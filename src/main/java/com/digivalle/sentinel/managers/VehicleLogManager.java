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
import com.digivalle.sentinel.models.VehicleLog;
import com.digivalle.sentinel.repositories.VehicleLogRepository;
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
public class VehicleLogManager {
    
    @Autowired
    private VehicleLogRepository vehicleLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public VehicleLog getById(UUID id) throws EntityNotExistentException {
        Optional<VehicleLog> vehicleLog = vehicleLogRepository.findById(id);
        if (!vehicleLog.isEmpty()) {
            return vehicleLog.get();
        }
        throw new EntityNotExistentException(VehicleLog.class,id.toString());
    }
    
    public PagedResponse<VehicleLog> getVehicleLog(VehicleLog filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<VehicleLog> cq = cb.createQuery(VehicleLog.class);
        Root<VehicleLog> root = cq.from(VehicleLog.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<VehicleLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<VehicleLog> result = query.getResultList();
        
        Page<VehicleLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(VehicleLog filter, CriteriaBuilder cb, Root<VehicleLog> root) {
        List<Predicate> predicates = new ArrayList<>();

        if(filter.getCreationDate()!=null && filter.getCreationDate2()!=null){
            predicates.add(cb.between(root.get("creationDate"), filter.getCreationDate(),filter.getCreationDate2()));
        }
        if(filter.getUpdateDate()!=null && filter.getUpdateDate2()!=null){
            predicates.add(cb.between(root.get("updateDate"), filter.getUpdateDate(),filter.getUpdateDate2()));
        }
        
        if(filter.getColor()!=null){
            predicates.add(cb.like(cb.lower(root.get("color")), "%" + filter.getColor().toLowerCase()+ "%"));
        }
        if(filter.getDescription()!=null){
            predicates.add(cb.like(cb.lower(root.get("description")), "%" + filter.getDescription().toLowerCase()+ "%"));
        }
        if(filter.getPlates()!=null){
            predicates.add(cb.like(cb.lower(root.get("plates")), "%" + filter.getPlates().toLowerCase()+ "%"));
        }
        if(filter.getSubBrand()!=null){
            predicates.add(cb.like(cb.lower(root.get("subBrand")), "%" + filter.getSubBrand().toLowerCase()+ "%"));
        }
        if(filter.getVehicleBrand()!=null){
            if(filter.getVehicleBrand().getId()!=null){
                predicates.add(cb.equal(root.get("vehicleBrand").get("id"), filter.getVehicleBrand().getId()));
            }
            if(filter.getVehicleBrand().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("vehicleBrand").get("name")), "%" + filter.getVehicleBrand().getName().toLowerCase()+ "%"));
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
        if(filter.getVehicleId()!=null){
            predicates.add(cb.equal(root.get("vehicleId"), filter.getVehicleId()));
        }
        if(filter.getTransactionId()!=null){
            predicates.add(cb.equal(root.get("transactionId"), filter.getTransactionId()));
        }
        if(filter.getAction()!=null){
            predicates.add(cb.equal(root.get("action"), filter.getAction()));
        }

        return predicates;
    }

    private void applySorting(CriteriaQuery<VehicleLog> cq, CriteriaBuilder cb, Root<VehicleLog> root, VehicleLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, VehicleLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<VehicleLog> countRoot = countQuery.from(VehicleLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public VehicleLog createVehicleLog(VehicleLog vehicleLog) throws BusinessLogicException {
        //validateVehicleLog(vehicleLog);
        //validateUnique(vehicleLog);
        return vehicleLogRepository.save(vehicleLog);
    }

    private void validateVehicleLog(VehicleLog vehicleLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(vehicleLog.getSubBrand())) {
            throw new BusinessLogicException("El campo SubBrand es requerido para el objeto VehicleLog");
        } else if (StringUtils.isEmpty(vehicleLog.getColor())) {
            throw new BusinessLogicException("El campo Color es requerido para el objeto VehicleLog");
        } else if (StringUtils.isEmpty(vehicleLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto VehicleLog");
        } 
    }
    
    /*private void validateUnique(VehicleLog vehicleLog) throws ExistentEntityException {
        List<VehicleLog> vehicleLoges = vehicleLogRepository.findByName(vehicleLog.getName());
        if (vehicleLoges!=null && !vehicleLoges.isEmpty()) {
            throw new ExistentEntityException(VehicleLog.class,"name="+vehicleLog.getName());
        } 
    }*/

    public VehicleLog updateVehicleLog(UUID vehicleLogId, VehicleLog vehicleLog) throws EntityNotExistentException {
        VehicleLog persistedVehicleLog = getById(vehicleLogId);
        if (persistedVehicleLog != null) {
            persistedVehicleLog.setPlates(vehicleLog.getPlates());
            return vehicleLogRepository.save(persistedVehicleLog);
        } else {
            throw new EntityNotExistentException(VehicleLog.class,vehicleLogId.toString());
        }
    }

    public void deleteVehicleLog(UUID vehicleLogId) throws EntityNotExistentException {
        VehicleLog vehicleLog = getById(vehicleLogId);
        vehicleLog.setDeleted(Boolean.TRUE);
        vehicleLog.setActive(Boolean.FALSE);
        vehicleLogRepository.save(vehicleLog);
    }

    public List<VehicleLog> findAll(){
        return vehicleLogRepository.findAll();
    }
    
    public VehicleLog getByPlates(String plates){
        return vehicleLogRepository.getByPlates(plates);
    }
    
    public List<VehicleLog> findByPlatesIgnoreCaseContaining(String plates){
        return vehicleLogRepository.findByPlatesIgnoreCaseContaining(plates);
    }
    
    public List<VehicleLog> findByPlatesIgnoreCaseContainingAndDeleted(String plates,Boolean deleted){
        return vehicleLogRepository.findByPlatesIgnoreCaseContainingAndDeleted(plates,deleted);
    }
    
   
}
