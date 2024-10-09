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
import com.digivalle.sentinel.models.VehicleBrand;
import com.digivalle.sentinel.repositories.VehicleBrandRepository;
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
public class VehicleBrandManager {
    
    @Autowired
    private VehicleBrandRepository vehicleBrandRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public VehicleBrand getById(UUID id) throws EntityNotExistentException {
        Optional<VehicleBrand> vehicleBrand = vehicleBrandRepository.findById(id);
        if (!vehicleBrand.isEmpty()) {
            return vehicleBrand.get();
        }
        throw new EntityNotExistentException(VehicleBrand.class,id.toString());
    }
    
    public PagedResponse<VehicleBrand> getVehicleBrand(VehicleBrand filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<VehicleBrand> cq = cb.createQuery(VehicleBrand.class);
        Root<VehicleBrand> root = cq.from(VehicleBrand.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<VehicleBrand> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<VehicleBrand> result = query.getResultList();
        
        Page<VehicleBrand> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(VehicleBrand filter, CriteriaBuilder cb, Root<VehicleBrand> root) {
        List<Predicate> predicates = new ArrayList<>();

        if(filter.getCreationDate()!=null && filter.getCreationDate2()!=null){
            predicates.add(cb.between(root.get("creationDate"), filter.getCreationDate(),filter.getCreationDate2()));
        }
        if(filter.getUpdateDate()!=null && filter.getUpdateDate2()!=null){
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

    private void applySorting(CriteriaQuery<VehicleBrand> cq, CriteriaBuilder cb, Root<VehicleBrand> root, VehicleBrand filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, VehicleBrand filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<VehicleBrand> countRoot = countQuery.from(VehicleBrand.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public VehicleBrand createVehicleBrand(VehicleBrand vehicleBrand) throws BusinessLogicException, ExistentEntityException {
        validateVehicleBrand(vehicleBrand);
        validateUnique(vehicleBrand);
        return vehicleBrandRepository.save(vehicleBrand);
    }

    private void validateVehicleBrand(VehicleBrand vehicleBrand) throws BusinessLogicException {
        if (StringUtils.isEmpty(vehicleBrand.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto VehicleBrand");
        } else if (StringUtils.isEmpty(vehicleBrand.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto VehicleBrand");
        } 
    }
    
    private void validateUnique(VehicleBrand vehicleBrand) throws ExistentEntityException {
        List<VehicleBrand> vehicleBrandes = vehicleBrandRepository.findByName(vehicleBrand.getName());
        if (vehicleBrandes!=null && !vehicleBrandes.isEmpty()) {
            throw new ExistentEntityException(VehicleBrand.class,"name="+vehicleBrand.getName());
        } 
    }

    public VehicleBrand updateVehicleBrand(UUID vehicleBrandId, VehicleBrand vehicleBrand) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(vehicleBrand.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto VehicleBrand");
        } 
    
        VehicleBrand persistedVehicleBrand = getById(vehicleBrandId);
        if (persistedVehicleBrand != null) {
            if(vehicleBrand.getName()!=null){
                persistedVehicleBrand.setName(vehicleBrand.getName());
            }
            
            if(vehicleBrand.getActive()!=null){
                persistedVehicleBrand.setActive(vehicleBrand.getActive());
            }
            persistedVehicleBrand.setUpdateUser(vehicleBrand.getUpdateUser());
            return vehicleBrandRepository.save(persistedVehicleBrand);
        } else {
            throw new EntityNotExistentException(VehicleBrand.class,vehicleBrandId.toString());
        }
    }

    public VehicleBrand deleteVehicleBrand(UUID vehicleBrandId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto VehicleBrand");
        } 
        VehicleBrand vehicleBrand = getById(vehicleBrandId);
        vehicleBrand.setDeleted(Boolean.TRUE);
        vehicleBrand.setActive(Boolean.FALSE);
        return vehicleBrandRepository.save(vehicleBrand);
    }

    public List<VehicleBrand> findAll(){
        return vehicleBrandRepository.findAll();
    }
    
    public VehicleBrand getByName(String name){
        return vehicleBrandRepository.getByName(name);
    }
    
    public List<VehicleBrand> findByNameIgnoreCaseContaining(String name){
        return vehicleBrandRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<VehicleBrand> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return vehicleBrandRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public VehicleBrand getBySerial(Integer serial) {
        return vehicleBrandRepository.getBySerial(serial);
    }
}
