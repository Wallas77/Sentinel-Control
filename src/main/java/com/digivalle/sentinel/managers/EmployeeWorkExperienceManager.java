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
        //cq.orderBy(cb.asc(root.get("id")));

        List<Predicate> predicates = new ArrayList<Predicate>();
        cq.orderBy(cb.desc(root.get("creationDate")));
        if(filter.getCreationDate()!=null && filter.getCreationDate2()!=null){
            predicates.add(cb.between(root.get("creationDate"), filter.getCreationDate(),filter.getCreationDate2()));
        }
        if(filter.getUpdateDate()!=null && filter.getUpdateDate2()!=null){
            predicates.add(cb.between(root.get("updateDate"), filter.getUpdateDate(),filter.getUpdateDate2()));
            cq.orderBy(cb.desc(root.get("updateDate")));
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
            cq.orderBy(cb.desc(root.get("endDate")));
        }
        if(filter.getStartDate()!=null && filter.getStartDate2()!=null){
            predicates.add(cb.between(root.get("startDate"), filter.getStartDate(),filter.getStartDate2()));
            cq.orderBy(cb.desc(root.get("startDate")));
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
        
        cq.select(root);
        if(predicates.size()>0){
            cq.where(predicates.toArray(new Predicate[0]));
        }
        
        TypedQuery<EmployeeWorkExperience> query = entityManager.createQuery(cq);
        
        int iTotal = query.getResultList().size();

        
        
        List<EmployeeWorkExperience> result = query.setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        
        Page<EmployeeWorkExperience> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<EmployeeWorkExperience>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
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