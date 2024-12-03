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
import com.digivalle.sentinel.models.EmployeeWorkExperience;
import com.digivalle.sentinel.repositories.EmployeeWorkExperienceRepository;
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
public class EmployeeWorkExperienceManager {
    
    @Autowired
    private EmployeeWorkExperienceRepository employeeWorkExperienceRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public EmployeeWorkExperience getById(UUID id) throws EntityNotExistentException {
        Optional<EmployeeWorkExperience> employeeWorkExperience = employeeWorkExperienceRepository.findById(id);
        if (!employeeWorkExperience.isEmpty()) {
            return employeeWorkExperience.get();
        }
        throw new EntityNotExistentException(EmployeeWorkExperience.class,id.toString());
    }
    
    public PagedResponse<EmployeeWorkExperience> getEmployeeWorkExperience(EmployeeWorkExperience filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<EmployeeWorkExperience> cq = cb.createQuery(EmployeeWorkExperience.class);
        Root<EmployeeWorkExperience> root = cq.from(EmployeeWorkExperience.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<EmployeeWorkExperience> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<EmployeeWorkExperience> result = query.getResultList();
        
        Page<EmployeeWorkExperience> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(EmployeeWorkExperience filter, CriteriaBuilder cb, Root<EmployeeWorkExperience> root) {
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
        if(filter.getCompany()!=null){
            predicates.add(cb.like(cb.lower(root.get("company")), "%" + filter.getCompany().toLowerCase()+ "%"));
        }
        if(filter.getDescription()!=null){
            predicates.add(cb.like(cb.lower(root.get("description")), "%" + filter.getDescription().toLowerCase()+ "%"));
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
            if(filter.getEmployee().getSecondSurname()!=null){
                predicates.add(cb.like(cb.lower(root.get("employee").get("secondSurname")), "%" + filter.getEmployee().getSecondSurname().toLowerCase()+ "%"));
            }
        }
        if(filter.getEndDate()!=null && filter.getEndDate2()!=null){
            predicates.add(cb.between(root.get("endDate"), filter.getEndDate(),filter.getEndDate2()));
        }
        if(filter.getStartDate()!=null && filter.getStartDate2()!=null){
            predicates.add(cb.between(root.get("startDate"), filter.getStartDate(),filter.getStartDate2()));
        }
        if(filter.getJobTitle()!=null){
            predicates.add(cb.like(cb.lower(root.get("jobTitle")), "%" + filter.getJobTitle().toLowerCase()+ "%"));
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

    private void applySorting(CriteriaQuery<EmployeeWorkExperience> cq, CriteriaBuilder cb, Root<EmployeeWorkExperience> root, EmployeeWorkExperience filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else if (filter.getEndDate()!= null && filter.getEndDate2() != null) {
            orderList.add(cb.desc(root.get("endDate")));
        } else if (filter.getStartDate()!= null && filter.getStartDate2() != null) {
            orderList.add(cb.desc(root.get("startDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, EmployeeWorkExperience filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<EmployeeWorkExperience> countRoot = countQuery.from(EmployeeWorkExperience.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    

    public EmployeeWorkExperience createEmployeeWorkExperience(EmployeeWorkExperience employeeWorkExperience) throws BusinessLogicException, ExistentEntityException {
        validateEmployeeWorkExperience(employeeWorkExperience);
        //validateUnique(employeeWorkExperience);
        return employeeWorkExperienceRepository.save(employeeWorkExperience);
    }

    private void validateEmployeeWorkExperience(EmployeeWorkExperience employeeWorkExperience) throws BusinessLogicException {
        if (StringUtils.isEmpty(employeeWorkExperience.getJobTitle())) {
            throw new BusinessLogicException("El campo JobTitle es requerido para el objeto EmployeeWorkExperience");
        } else if (StringUtils.isEmpty(employeeWorkExperience.getDescription())) {
            throw new BusinessLogicException("El campo Description es requerido para el objeto EmployeeWorkExperience");
        } else if (StringUtils.isEmpty(employeeWorkExperience.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto EmployeeWorkExperience");
        } 
    }
    
    /*private void validateUnique(EmployeeWorkExperience employeeWorkExperience) throws ExistentEntityException {
        List<EmployeeWorkExperience> employeeWorkExperiencees = employeeWorkExperienceRepository.findByName(employeeWorkExperience.getName());
        if (employeeWorkExperiencees!=null && !employeeWorkExperiencees.isEmpty()) {
            throw new ExistentEntityException(EmployeeWorkExperience.class,"name="+employeeWorkExperience.getName());
        } 
    }*/

    public EmployeeWorkExperience updateEmployeeWorkExperience(UUID employeeWorkExperienceId, EmployeeWorkExperience employeeWorkExperience) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(employeeWorkExperience.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto EmployeeWorkExperience");
        } 
    
        EmployeeWorkExperience persistedEmployeeWorkExperience = getById(employeeWorkExperienceId);
        if (persistedEmployeeWorkExperience != null) {
            if(employeeWorkExperience.getCompany()!=null){
                persistedEmployeeWorkExperience.setCompany(employeeWorkExperience.getCompany());
            }
            if(employeeWorkExperience.getEmployee()!=null){
                persistedEmployeeWorkExperience.setEmployee(employeeWorkExperience.getEmployee());
            }
            if(employeeWorkExperience.getDescription()!=null){
                persistedEmployeeWorkExperience.setDescription(employeeWorkExperience.getDescription());
            }
            if(employeeWorkExperience.getEndDate()!=null){
                persistedEmployeeWorkExperience.setEndDate(employeeWorkExperience.getEndDate());
            }
            if(employeeWorkExperience.getJobTitle()!=null){
                persistedEmployeeWorkExperience.setJobTitle(employeeWorkExperience.getJobTitle());
            }
            if(employeeWorkExperience.getStartDate()!=null){
                persistedEmployeeWorkExperience.setStartDate(employeeWorkExperience.getStartDate());
            }
            if(employeeWorkExperience.getActive()!=null){
                persistedEmployeeWorkExperience.setActive(employeeWorkExperience.getActive());
            }
            persistedEmployeeWorkExperience.setUpdateUser(employeeWorkExperience.getUpdateUser());
            return employeeWorkExperienceRepository.save(persistedEmployeeWorkExperience);
        } else {
            throw new EntityNotExistentException(EmployeeWorkExperience.class,employeeWorkExperienceId.toString());
        }
    }

    public EmployeeWorkExperience deleteEmployeeWorkExperience(UUID employeeWorkExperienceId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto EmployeeWorkExperience");
        } 
        EmployeeWorkExperience employeeWorkExperience = getById(employeeWorkExperienceId);
        employeeWorkExperience.setDeleted(Boolean.TRUE);
        employeeWorkExperience.setActive(Boolean.FALSE);
        return employeeWorkExperienceRepository.save(employeeWorkExperience);
    }

    public List<EmployeeWorkExperience> findAll(){
        return employeeWorkExperienceRepository.findAll();
    }
    
    public EmployeeWorkExperience getBySerial(Integer serial) {
        return employeeWorkExperienceRepository.getBySerial(serial);
    }
}
