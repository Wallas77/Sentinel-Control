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
import com.digivalle.sentinel.models.ServiceAssignment;
import com.digivalle.sentinel.repositories.ServiceAssignmentRepository;
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
public class ServiceAssignmentManager {
    
    @Autowired
    private ServiceAssignmentRepository serviceAssignmentRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public ServiceAssignment getById(UUID id) throws EntityNotExistentException {
        Optional<ServiceAssignment> serviceAssignment = serviceAssignmentRepository.findById(id);
        if (!serviceAssignment.isEmpty()) {
            return serviceAssignment.get();
        }
        throw new EntityNotExistentException(ServiceAssignment.class,id.toString());
    }
    
    public PagedResponse<ServiceAssignment> getServiceAssignment(ServiceAssignment filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ServiceAssignment> cq = cb.createQuery(ServiceAssignment.class);
        Root<ServiceAssignment> root = cq.from(ServiceAssignment.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<ServiceAssignment> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<ServiceAssignment> result = query.getResultList();
        
        Page<ServiceAssignment> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(ServiceAssignment filter, CriteriaBuilder cb, Root<ServiceAssignment> root) {
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
        if(filter.getEmployee()!=null){
            if(filter.getEmployee().getId()!=null){
                predicates.add(cb.equal(root.get("employee").get("id"), filter.getEmployee().getId()));
            }
            if(filter.getEmployee().getSerial()!=null){
                predicates.add(cb.equal(root.get("employee").get("serial"), filter.getEmployee().getSerial()));
            }
            if(filter.getEmployee().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("employee").get("name")), "%" + filter.getEmployee().getName().toLowerCase()+ "%"));
            }
        }
        if(filter.getRole()!=null){
            if(filter.getRole().getId()!=null){
                predicates.add(cb.equal(root.get("role").get("id"), filter.getRole().getId()));
            }
            if(filter.getRole().getSerial()!=null){
                predicates.add(cb.equal(root.get("role").get("serial"), filter.getRole().getSerial()));
            }
            if(filter.getRole().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("role").get("name")), "%" + filter.getRole().getName().toLowerCase()+ "%"));
            }
        }
        if(filter.getService()!=null){
            if(filter.getService().getId()!=null){
                predicates.add(cb.equal(root.get("service").get("id"), filter.getService().getId()));
            }
            if(filter.getService().getSerial()!=null){
                predicates.add(cb.equal(root.get("service").get("serial"), filter.getService().getSerial()));
            }
            if(filter.getService().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("service").get("name")), "%" + filter.getService().getName().toLowerCase()+ "%"));
            }
        }
        if(filter.getSalaryParDayAmount()!=null && filter.getSalaryParDayAmount2()!=null){
            predicates.add(cb.between(root.get("salaryParDayAmount"), filter.getSalaryParDayAmount(),filter.getSalaryParDayAmount2()));
        }
        if(filter.getHoursPerDay()!=null){
            predicates.add(cb.equal(root.get("hoursPerDay"), filter.getHoursPerDay()));
        }
        if(filter.getStartDate()!=null && filter.getStartDate2()!=null){
            predicates.add(cb.between(root.get("startDate"), filter.getStartDate(),filter.getStartDate2()));
        }
        if(filter.getEndDate()!=null && filter.getEndDate2()!=null){
            predicates.add(cb.between(root.get("endDate"), filter.getEndDate(),filter.getEndDate2()));
        }
        if(filter.getRecurrenceInDays()!=null){
            predicates.add(cb.equal(root.get("recurrenceInDays"), filter.getRecurrenceInDays()));
        }
        if(filter.getRecurrencePerWeekDays()!=null){
            predicates.add(cb.like(cb.lower(root.get("recurrencePerWeekDays")), "%" + filter.getRecurrencePerWeekDays().toLowerCase()+ "%"));
        }
        if(filter.getRecurrencePerNumberDays()!=null){
            predicates.add(cb.like(cb.lower(root.get("recurrencePerNumberDays")), "%" + filter.getRecurrencePerNumberDays().toLowerCase()+ "%"));
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

    private void applySorting(CriteriaQuery<ServiceAssignment> cq, CriteriaBuilder cb, Root<ServiceAssignment> root, ServiceAssignment filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, ServiceAssignment filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ServiceAssignment> countRoot = countQuery.from(ServiceAssignment.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public ServiceAssignment createServiceAssignment(ServiceAssignment serviceAssignment) throws BusinessLogicException, ExistentEntityException {
        validateServiceAssignment(serviceAssignment);
        validateUnique(serviceAssignment);
        return serviceAssignmentRepository.save(serviceAssignment);
    }

    private void validateServiceAssignment(ServiceAssignment serviceAssignment) throws BusinessLogicException {
        if (serviceAssignment.getRole()==null) {
            throw new BusinessLogicException("El campo Role es requerido para el objeto ServiceAssignment");
        } else if (StringUtils.isEmpty(serviceAssignment.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto ServiceAssignment");
        } else if (serviceAssignment.getService()==null) {
            throw new BusinessLogicException("El campo Service es requerido para el objeto ServiceAssignment");
        }
    }
    
    private void validateUnique(ServiceAssignment serviceAssignment) throws ExistentEntityException {
        List<ServiceAssignment> serviceAssignmentes = serviceAssignmentRepository.getByServiceAndRoleAndEmployeeAndActiveAndDeleted(serviceAssignment.getService(), serviceAssignment.getRole(), serviceAssignment.getEmployee(), Boolean.TRUE, Boolean.FALSE);
        if (serviceAssignmentes!=null && !serviceAssignmentes.isEmpty()) {
            throw new ExistentEntityException(ServiceAssignment.class,"service="+serviceAssignment.getService().getName()+" role="+serviceAssignment.getRole().getName()+" employee="+serviceAssignment.getEmployee().getName());
        } 
    }

    public ServiceAssignment updateServiceAssignment(UUID serviceAssignmentId, ServiceAssignment serviceAssignment) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(serviceAssignment.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto ServiceAssignment");
        } 
    
        ServiceAssignment persistedServiceAssignment = getById(serviceAssignmentId);
        if (persistedServiceAssignment != null) {
            if(serviceAssignment.getEmployee()!=null){
                persistedServiceAssignment.setEmployee(serviceAssignment.getEmployee());
            }
            if(serviceAssignment.getRole()!=null){
                persistedServiceAssignment.setRole(serviceAssignment.getRole());
            }
            if(serviceAssignment.getService()!=null){
                persistedServiceAssignment.setService(serviceAssignment.getService());
            }
            if(serviceAssignment.getSalaryParDayAmount()!=null){
                persistedServiceAssignment.setSalaryParDayAmount(serviceAssignment.getSalaryParDayAmount());
            }
            if(serviceAssignment.getHoursPerDay()!=null){
                persistedServiceAssignment.setHoursPerDay(serviceAssignment.getHoursPerDay());
            }
            if(serviceAssignment.getStartDate()!=null){
                persistedServiceAssignment.setStartDate(serviceAssignment.getStartDate());
            }
            if(serviceAssignment.getEndDate()!=null){
                persistedServiceAssignment.setEndDate(serviceAssignment.getEndDate());
            }
            if(serviceAssignment.getRecurrenceInDays()!=null){
                persistedServiceAssignment.setRecurrenceInDays(serviceAssignment.getRecurrenceInDays());
            }
            if(serviceAssignment.getRecurrencePerWeekDays()!=null){
                persistedServiceAssignment.setRecurrencePerWeekDays(serviceAssignment.getRecurrencePerWeekDays());
            }
            if(serviceAssignment.getRecurrencePerNumberDays()!=null){
                persistedServiceAssignment.setRecurrencePerNumberDays(serviceAssignment.getRecurrencePerNumberDays());
            }
            if(serviceAssignment.getActive()!=null){
                persistedServiceAssignment.setActive(serviceAssignment.getActive());
            }
            persistedServiceAssignment.setUpdateUser(serviceAssignment.getUpdateUser());
            return serviceAssignmentRepository.save(persistedServiceAssignment);
        } else {
            throw new EntityNotExistentException(ServiceAssignment.class,serviceAssignmentId.toString());
        }
    }

    public ServiceAssignment deleteServiceAssignment(UUID serviceAssignmentId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto ServiceAssignment");
        } 
        ServiceAssignment serviceAssignment = getById(serviceAssignmentId);
        serviceAssignment.setDeleted(Boolean.TRUE);
        serviceAssignment.setActive(Boolean.FALSE);
        return serviceAssignmentRepository.save(serviceAssignment);
    }

    public List<ServiceAssignment> findAll(){
        return serviceAssignmentRepository.findAll();
    }
    
    public ServiceAssignment getBySerial(Integer serial) {
        return serviceAssignmentRepository.getBySerial(serial);
    }
}
