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
import com.digivalle.sentinel.models.Vehicle;
import com.digivalle.sentinel.repositories.VehicleRepository;
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
public class VehicleManager {
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public Vehicle getById(UUID id) throws EntityNotExistentException {
        Optional<Vehicle> vehicle = vehicleRepository.findById(id);
        if (!vehicle.isEmpty()) {
            return vehicle.get();
        }
        throw new EntityNotExistentException(Vehicle.class,id.toString());
    }
    
    public PagedResponse<Vehicle> getVehicle(Vehicle filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Vehicle> cq = cb.createQuery(Vehicle.class);
        Root<Vehicle> root = cq.from(Vehicle.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<Vehicle> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<Vehicle> result = query.getResultList();
        
        Page<Vehicle> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(Vehicle filter, CriteriaBuilder cb, Root<Vehicle> root) {
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

        return predicates;
    }

    private void applySorting(CriteriaQuery<Vehicle> cq, CriteriaBuilder cb, Root<Vehicle> root, Vehicle filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, Vehicle filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Vehicle> countRoot = countQuery.from(Vehicle.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public Vehicle createVehicle(Vehicle vehicle) throws BusinessLogicException, ExistentEntityException {
        validateVehicle(vehicle);
        validateUnique(vehicle);
        return vehicleRepository.save(vehicle);
    }

    private void validateVehicle(Vehicle vehicle) throws BusinessLogicException {
        if (StringUtils.isEmpty(vehicle.getSubBrand())) {
            throw new BusinessLogicException("El campo SubBrand es requerido para el objeto Vehicle");
        } else if (StringUtils.isEmpty(vehicle.getColor())) {
            throw new BusinessLogicException("El campo Color es requerido para el objeto Vehicle");
        } else if (StringUtils.isEmpty(vehicle.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Vehicle");
        } 
    }
    
    private void validateUnique(Vehicle vehicle) throws ExistentEntityException {
        if(!StringUtils.isEmpty(vehicle.getPlates())){
            List<Vehicle> vehicles = vehicleRepository.findByPlates(vehicle.getPlates());
            if (vehicles!=null && !vehicles.isEmpty()) {
                throw new ExistentEntityException(Vehicle.class,"plates="+vehicle.getPlates());
            }
        }
    }

    public Vehicle updateVehicle(UUID vehicleId, Vehicle vehicle) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(vehicle.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Vehicle");
        } 
    
        Vehicle persistedVehicle = getById(vehicleId);
        if (persistedVehicle != null) {
            if(vehicle.getColor()!=null){
                persistedVehicle.setColor(vehicle.getColor());
            }
            if(vehicle.getDescription()!=null){
                persistedVehicle.setDescription(vehicle.getDescription());
            }
            if(vehicle.getPlates()!=null){
                persistedVehicle.setPlates(vehicle.getPlates());
            }
            if(vehicle.getSubBrand()!=null){
                persistedVehicle.setSubBrand(vehicle.getSubBrand());
            }
            if(vehicle.getVehicleBrand()!=null){
                persistedVehicle.setVehicleBrand(vehicle.getVehicleBrand());
            }
            if(vehicle.getActive()!=null){
                persistedVehicle.setActive(vehicle.getActive());
            }
            persistedVehicle.setUpdateUser(vehicle.getUpdateUser());
            return vehicleRepository.save(persistedVehicle);
        } else {
            throw new EntityNotExistentException(Vehicle.class,vehicleId.toString());
        }
    }

    public Vehicle deleteVehicle(UUID vehicleId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto Vehicle");
        } 
        Vehicle vehicle = getById(vehicleId);
        vehicle.setDeleted(Boolean.TRUE);
        vehicle.setActive(Boolean.FALSE);
        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> findAll(){
        return vehicleRepository.findAll();
    }
    
    public Vehicle getByPlates(String plates){
        return vehicleRepository.getByPlates(plates);
    }
    
    public List<Vehicle> findByPlatesIgnoreCaseContaining(String plates){
        return vehicleRepository.findByPlatesIgnoreCaseContaining(plates);
    }
    
    public List<Vehicle> findByPlatesIgnoreCaseContainingAndDeleted(String plates,Boolean deleted){
        return vehicleRepository.findByPlatesIgnoreCaseContainingAndDeleted(plates,deleted);
    }
    
    public Vehicle getBySerial(Integer serial) {
        return vehicleRepository.getBySerial(serial);
    }
}
