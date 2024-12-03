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
import com.digivalle.sentinel.models.AccessControl;
import com.digivalle.sentinel.repositories.AccessControlRepository;
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
public class AccessControlManager {
    
    @Autowired
    private AccessControlRepository accessControlRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public AccessControl getById(UUID id) throws EntityNotExistentException {
        Optional<AccessControl> accessControl = accessControlRepository.findById(id);
        if (!accessControl.isEmpty()) {
            return accessControl.get();
        }
        throw new EntityNotExistentException(AccessControl.class,id.toString());
    }
    
    public PagedResponse<AccessControl> getAccessControl(AccessControl filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AccessControl> cq = cb.createQuery(AccessControl.class);
        Root<AccessControl> root = cq.from(AccessControl.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<AccessControl> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<AccessControl> result = query.getResultList();
        
        Page<AccessControl> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(AccessControl filter, CriteriaBuilder cb, Root<AccessControl> root) {
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
        if(filter.getAccessControlType()!=null){
            predicates.add(cb.equal(root.get("accessControlType"), filter.getAccessControlType()));
        }
        if(filter.getAccessDate()!=null && filter.getAccessDate2()!=null){
            predicates.add(cb.between(root.get("accessDate"), filter.getAccessDate(),filter.getAccessDate2()));
        }
        if(filter.getExitDate()!=null && filter.getExitDate2()!=null){
            predicates.add(cb.between(root.get("exitDate"), filter.getExitDate(),filter.getExitDate2()));
        }
        if(filter.getCompany()!=null){
            predicates.add(cb.like(cb.lower(root.get("company")), "%" + filter.getCompany().toLowerCase()+ "%"));
        }
        
        if(filter.getContact()!=null){
            if(filter.getContact().getId()!=null){
                predicates.add(cb.equal(root.get("contact").get("id"), filter.getContact().getId()));
            }
            if(filter.getContact().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("contact").get("name")), "%" + filter.getContact().getName().toLowerCase()+ "%"));
            }
            if(filter.getContact().getFirstSurname()!=null){
                predicates.add(cb.like(cb.lower(root.get("contact").get("firstSurname")), "%" + filter.getContact().getFirstSurname().toLowerCase()+ "%"));
            }
        }
        if(filter.getCustomerDirectory()!=null){
            if(filter.getCustomerDirectory().getId()!=null){
                predicates.add(cb.equal(root.get("customerDirectory").get("id"), filter.getCustomerDirectory().getId()));
            }
            if(filter.getCustomerDirectory().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("customerDirectory").get("name")), "%" + filter.getCustomerDirectory().getName().toLowerCase()+ "%"));
            }
            
        }
        if(filter.getEmployee()!=null){
            if(filter.getEmployee().getId()!=null){
                predicates.add(cb.equal(root.get("employee").get("id"), filter.getEmployee().getId()));
            }
            if(filter.getEmployee().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("employee").get("name")), "%" + filter.getEmployee().getName().toLowerCase()+ "%"));
            }
            if(filter.getEmployee().getFirstSurname()!=null){
                predicates.add(cb.like(cb.lower(root.get("employee").get("firstSurname")), "%" + filter.getEmployee().getFirstSurname().toLowerCase()+ "%"));
            }
            
        }
        if(filter.getIdNumber()!=null){
            predicates.add(cb.like(cb.lower(root.get("idNumber")), "%" + filter.getIdNumber().toLowerCase()+ "%"));
        }
        if(filter.getName()!=null){
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase()+ "%"));
        }
        if(filter.getNotes()!=null){
            predicates.add(cb.like(cb.lower(root.get("notes")), "%" + filter.getNotes().toLowerCase()+ "%"));
        }
        if(filter.getParkinglotNumber()!=null){
            predicates.add(cb.like(cb.lower(root.get("parkinglotNumber")), "%" + filter.getParkinglotNumber().toLowerCase()+ "%"));
        }
        if(filter.getSupplier()!=null){
            if(filter.getSupplier().getId()!=null){
                predicates.add(cb.equal(root.get("supplier").get("id"), filter.getSupplier().getId()));
            }
            if(filter.getSupplier().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("supplier").get("name")), "%" + filter.getSupplier().getName().toLowerCase()+ "%"));
            }
        }
        if(filter.getVehicle()!=null){
            if(filter.getVehicle().getId()!=null){
                predicates.add(cb.equal(root.get("vehicle").get("id"), filter.getVehicle().getId()));
            }
            if(filter.getVehicle().getPlates()!=null){
                predicates.add(cb.like(cb.lower(root.get("vehicle").get("plates")), "%" + filter.getVehicle().getPlates().toLowerCase()+ "%"));
            }
        }
        if(filter.getVisitPerson()!=null){
            predicates.add(cb.like(cb.lower(root.get("visitPerson")), "%" + filter.getVisitPerson().toLowerCase()+ "%"));
        }
        if(filter.getVisitReason()!=null){
            predicates.add(cb.like(cb.lower(root.get("visitReason")), "%" + filter.getVisitReason().toLowerCase()+ "%"));
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

    private void applySorting(CriteriaQuery<AccessControl> cq, CriteriaBuilder cb, Root<AccessControl> root, AccessControl filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, AccessControl filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<AccessControl> countRoot = countQuery.from(AccessControl.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public AccessControl createAccessControl(AccessControl accessControl) throws BusinessLogicException, ExistentEntityException {
        validateAccessControl(accessControl);
        //validateUnique(accessControl);
        return accessControlRepository.save(accessControl);
    }

    private void validateAccessControl(AccessControl accessControl) throws BusinessLogicException {
        if (StringUtils.isEmpty(accessControl.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto AccessControl");
        } else if (StringUtils.isEmpty(accessControl.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto AccessControl");
        } 
    }
    
    private void validateUnique(AccessControl accessControl) throws ExistentEntityException {
        List<AccessControl> accessControles = accessControlRepository.findByName(accessControl.getName());
        if (accessControles!=null && !accessControles.isEmpty()) {
            throw new ExistentEntityException(AccessControl.class,"name="+accessControl.getName());
        } 
    }

    public AccessControl updateAccessControl(UUID accessControlId, AccessControl accessControl) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(accessControl.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto AccessControl");
        } 
    
        AccessControl persistedAccessControl = getById(accessControlId);
        if (persistedAccessControl != null) {
            if(accessControl.getAccessControlObjects()!=null){
                persistedAccessControl.setAccessControlObjects(accessControl.getAccessControlObjects());
            }
            if(accessControl.getAccessControlType()!=null){
                persistedAccessControl.setAccessControlType(accessControl.getAccessControlType());
            }
            if(accessControl.getAccessDate()!=null){
                persistedAccessControl.setAccessDate(accessControl.getAccessDate());
            }
            if(accessControl.getCompany()!=null){
                persistedAccessControl.setCompany(accessControl.getCompany());
            }
            if(accessControl.getContact()!=null){
                persistedAccessControl.setContact(accessControl.getContact());
            }
            if(accessControl.getCustomerDirectory()!=null){
                persistedAccessControl.setCustomerDirectory(accessControl.getCustomerDirectory());
            }
            if(accessControl.getEmployee()!=null){
                persistedAccessControl.setEmployee(accessControl.getEmployee());
            }
            if(accessControl.getExitDate()!=null){
                persistedAccessControl.setExitDate(accessControl.getExitDate());
            }
            if(accessControl.getIdImage()!=null){
                persistedAccessControl.setIdImage(accessControl.getIdImage());
            }
            if(accessControl.getIdNumber()!=null){
                persistedAccessControl.setIdNumber(accessControl.getIdNumber());
            }
            if(accessControl.getLastNames()!=null){
                persistedAccessControl.setLastNames(accessControl.getLastNames());
            }
            if(accessControl.getName()!=null){
                persistedAccessControl.setName(accessControl.getName());
            }
            if(accessControl.getNotes()!=null){
                persistedAccessControl.setNotes(accessControl.getNotes());
            }
            if(accessControl.getParkinglotNumber()!=null){
                persistedAccessControl.setParkinglotNumber(accessControl.getParkinglotNumber());
            }
            if(accessControl.getPhoto()!=null){
                persistedAccessControl.setPhoto(accessControl.getPhoto());
            }
            if(accessControl.getSignature()!=null){
                persistedAccessControl.setSignature(accessControl.getSignature());
            }
            if(accessControl.getSupplier()!=null){
                persistedAccessControl.setSupplier(accessControl.getSupplier());
            }
            if(accessControl.getTotalResidenceTime()!=null){
                persistedAccessControl.setTotalResidenceTime(accessControl.getTotalResidenceTime());
            }
            if(accessControl.getVehicle()!=null){
                persistedAccessControl.setVehicle(accessControl.getVehicle());
            }
            if(accessControl.getVisitPerson()!=null){
                persistedAccessControl.setVisitPerson(accessControl.getVisitPerson());
            }
            if(accessControl.getVisitReason()!=null){
                persistedAccessControl.setVisitReason(accessControl.getVisitReason());
            }
            if(accessControl.getActive()!=null){
                persistedAccessControl.setActive(accessControl.getActive());
            }
            persistedAccessControl.setUpdateUser(accessControl.getUpdateUser());
            return accessControlRepository.save(persistedAccessControl);
        } else {
            throw new EntityNotExistentException(AccessControl.class,accessControlId.toString());
        }
    }

    public AccessControl deleteAccessControl(UUID accessControlId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto AccessControl");
        } 
        AccessControl accessControl = getById(accessControlId);
        accessControl.setDeleted(Boolean.TRUE);
        accessControl.setActive(Boolean.FALSE);
        return accessControlRepository.save(accessControl);
    }

    public List<AccessControl> findAll(){
        return accessControlRepository.findAll();
    }
    
    public AccessControl getByName(String name){
        return accessControlRepository.getByName(name);
    }
    
    public List<AccessControl> findByNameIgnoreCaseContaining(String name){
        return accessControlRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<AccessControl> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return accessControlRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public AccessControl getBySerial(Integer serial) {
        return accessControlRepository.getBySerial(serial);
    }
}
