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
import com.digivalle.sentinel.models.AccessControlLog;
import com.digivalle.sentinel.repositories.AccessControlLogRepository;
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
public class AccessControlLogManager {
    
    @Autowired
    private AccessControlLogRepository accessControlLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public AccessControlLog getById(UUID id) throws EntityNotExistentException {
        Optional<AccessControlLog> accessControlLog = accessControlLogRepository.findById(id);
        if (!accessControlLog.isEmpty()) {
            return accessControlLog.get();
        }
        throw new EntityNotExistentException(AccessControlLog.class,id.toString());
    }
    
    public PagedResponse<AccessControlLog> getAccessControlLog(AccessControlLog filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<AccessControlLog> cq = cb.createQuery(AccessControlLog.class);
        Root<AccessControlLog> root = cq.from(AccessControlLog.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<AccessControlLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<AccessControlLog> result = query.getResultList();
        
        Page<AccessControlLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(AccessControlLog filter, CriteriaBuilder cb, Root<AccessControlLog> root) {
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
        if(filter.getAccessControlId()!=null){
            predicates.add(cb.equal(root.get("accessControlId"), filter.getAccessControlId()));
        }
        if(filter.getTransactionId()!=null){
            predicates.add(cb.equal(root.get("transactionId"), filter.getTransactionId()));
        }
        if(filter.getAction()!=null){
            predicates.add(cb.equal(root.get("action"), filter.getAction()));
        }

        return predicates;
    }

    private void applySorting(CriteriaQuery<AccessControlLog> cq, CriteriaBuilder cb, Root<AccessControlLog> root, AccessControlLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, AccessControlLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<AccessControlLog> countRoot = countQuery.from(AccessControlLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public AccessControlLog createAccessControlLog(AccessControlLog accessControlLog) throws BusinessLogicException {
        //validateAccessControlLog(accessControlLog);
        //validateUnique(accessControlLog);
        return accessControlLogRepository.save(accessControlLog);
    }

    private void validateAccessControlLog(AccessControlLog accessControlLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(accessControlLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto AccessControlLog");
        } else if (StringUtils.isEmpty(accessControlLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto AccessControlLog");
        } 
    }
    
    private void validateUnique(AccessControlLog accessControlLog) throws ExistentEntityException {
        List<AccessControlLog> accessControlLoges = accessControlLogRepository.findByName(accessControlLog.getName());
        if (accessControlLoges!=null && !accessControlLoges.isEmpty()) {
            throw new ExistentEntityException(AccessControlLog.class,"name="+accessControlLog.getName());
        } 
    }

    public AccessControlLog updateAccessControlLog(UUID accessControlLogId, AccessControlLog accessControlLog) throws EntityNotExistentException {
        AccessControlLog persistedAccessControlLog = getById(accessControlLogId);
        if (persistedAccessControlLog != null) {
            persistedAccessControlLog.setName(accessControlLog.getName());
            return accessControlLogRepository.save(persistedAccessControlLog);
        } else {
            throw new EntityNotExistentException(AccessControlLog.class,accessControlLogId.toString());
        }
    }

    public void deleteAccessControlLog(UUID accessControlLogId) throws EntityNotExistentException {
        AccessControlLog accessControlLog = getById(accessControlLogId);
        accessControlLog.setDeleted(Boolean.TRUE);
        accessControlLog.setActive(Boolean.FALSE);
        accessControlLogRepository.save(accessControlLog);
    }

    public List<AccessControlLog> findAll(){
        return accessControlLogRepository.findAll();
    }
    
    public AccessControlLog getByName(String name){
        return accessControlLogRepository.getByName(name);
    }
    
    public List<AccessControlLog> findByNameIgnoreCaseContaining(String name){
        return accessControlLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<AccessControlLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return accessControlLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}
