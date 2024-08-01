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
import com.digivalle.sentinel.models.EmployeeLog;
import com.digivalle.sentinel.repositories.EmployeeLogRepository;
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
public class EmployeeLogManager {
    
    @Autowired
    private EmployeeLogRepository employeeLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public EmployeeLog getById(UUID id) throws EntityNotExistentException {
        Optional<EmployeeLog> employeeLog = employeeLogRepository.findById(id);
        if (!employeeLog.isEmpty()) {
            return employeeLog.get();
        }
        throw new EntityNotExistentException(EmployeeLog.class,id.toString());
    }
    
    public PagedResponse<EmployeeLog> getEmployeeLog(EmployeeLog filter, Paging paging){
        
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<EmployeeLog> cq = cb.createQuery(EmployeeLog.class);
        Root<EmployeeLog> root = cq.from(EmployeeLog.class);
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
        if(filter.getBirthday()!=null){
            predicates.add(cb.equal(root.get("birthday"), filter.getBirthday()));
        }
        if(filter.getBloodType()!=null){
            predicates.add(cb.equal(root.get("bloodType"), filter.getBloodType()));
        }
        if(filter.getBodyComplexion()!=null){
            predicates.add(cb.equal(root.get("bodyComplexion"), filter.getBodyComplexion()));
        }
        if(filter.getCode()!=null){
            predicates.add(cb.like(cb.lower(root.get("code")), "%" + filter.getCode().toLowerCase()+ "%"));
        }
        if(filter.getCity()!=null){
            predicates.add(cb.like(cb.lower(root.get("city")), "%" + filter.getCity().toLowerCase()+ "%"));
        }
        if(filter.getColony()!=null){
            predicates.add(cb.like(cb.lower(root.get("colony")), "%" + filter.getColony().toLowerCase()+ "%"));
        }
        if(filter.getSalaryAmount()!=null && filter.getSalaryAmount2()!=null){
            predicates.add(cb.between(root.get("salaryAmount"), filter.getSalaryAmount(),filter.getSalaryAmount2()));
            cq.orderBy(cb.desc(root.get("salaryAmount")));
        }
        if(filter.getEmergencyContactName()!=null){
            predicates.add(cb.like(cb.lower(root.get("emergencyContactName")), "%" + filter.getEmergencyContactName().toLowerCase()+ "%"));
        }
        if(filter.getEmergencyContactPhone()!=null){
            predicates.add(cb.like(cb.lower(root.get("emergencyContactPhone")), "%" + filter.getEmergencyContactPhone().toLowerCase()+ "%"));
        }
        if(filter.getCountry()!=null){
            if(filter.getCountry().getId()!=null){
                predicates.add(cb.equal(root.get("country").get("id"), filter.getCountry().getId()));
            }
            if(filter.getCountry().getCode()!=null){
                predicates.add(cb.like(cb.lower(root.get("country").get("code")), "%" + filter.getCountry().getCode().toLowerCase()+ "%"));
            }
            if(filter.getCountry().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("country").get("name")), "%" + filter.getCountry().getName().toLowerCase()+ "%"));
            }
        }
        
        if(filter.getEmail()!=null){
            predicates.add(cb.like(cb.lower(root.get("email")), "%" + filter.getEmail().toLowerCase()+ "%"));
        }
        if(filter.getExternalNumber()!=null){
            predicates.add(cb.like(cb.lower(root.get("externalNumber")), "%" + filter.getExternalNumber().toLowerCase()+ "%"));
        }
        if(filter.getEyesColor()!=null){
            predicates.add(cb.equal(root.get("eyesColor"), filter.getEyesColor()));
        }
        if(filter.getFirstSurname()!=null){
            predicates.add(cb.like(cb.lower(root.get("firstSurname")), "%" + filter.getFirstSurname().toLowerCase()+ "%"));
        }
        if(filter.getHairColor()!=null){
            predicates.add(cb.equal(root.get("hairColor"), filter.getHairColor()));
        }
        if(filter.getHeight()!=null && filter.getHeight2()!=null){
            predicates.add(cb.between(root.get("height"), filter.getHeight(),filter.getHeight2()));
            cq.orderBy(cb.desc(root.get("height")));
        }
        if(filter.getHomePhone()!=null){
            predicates.add(cb.like(cb.lower(root.get("homePhone")), "%" + filter.getHomePhone().toLowerCase()+ "%"));
        }
        if(filter.getInternalNumber()!=null){
            predicates.add(cb.like(cb.lower(root.get("internalNumber")), "%" + filter.getInternalNumber().toLowerCase()+ "%"));
        }
        if(filter.getMobilePhone()!=null){
            predicates.add(cb.like(cb.lower(root.get("mobilePhone")), "%" + filter.getMobilePhone().toLowerCase()+ "%"));
        }
        if(filter.getName()!=null){
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase()+ "%"));
        }
        if(filter.getNationality()!=null){
            predicates.add(cb.like(cb.lower(root.get("nationality")), "%" + filter.getNationality().toLowerCase()+ "%"));
        }
        if(filter.getSecondSurname()!=null){
            predicates.add(cb.like(cb.lower(root.get("secondSurname")), "%" + filter.getSecondSurname().toLowerCase()+ "%"));
        }
        if(filter.getSkinColor()!=null){
            predicates.add(cb.equal(root.get("skinColor"), filter.getSkinColor()));
        }
        if(filter.getState()!=null){
            predicates.add(cb.like(cb.lower(root.get("state")), "%" + filter.getState().toLowerCase()+ "%"));
        }
        if(filter.getStreet()!=null){
            predicates.add(cb.like(cb.lower(root.get("street")), "%" + filter.getStreet().toLowerCase()+ "%"));
        }
        if(filter.getSuburb()!=null){
            predicates.add(cb.like(cb.lower(root.get("suburb")), "%" + filter.getSuburb().toLowerCase()+ "%"));
        }
        if(filter.getWeight()!=null && filter.getWeight2()!=null){
            predicates.add(cb.between(root.get("weight"), filter.getWeight(),filter.getWeight2()));
            cq.orderBy(cb.desc(root.get("weight")));
        }
        if(filter.getZipCode()!=null){
            predicates.add(cb.like(cb.lower(root.get("zipCode")), "%" + filter.getZipCode().toLowerCase()+ "%"));
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
        
        TypedQuery<EmployeeLog> query = entityManager.createQuery(cq);
        
        int iTotal = query.getResultList().size();

        
        
        List<EmployeeLog> result = query.setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        
        Page<EmployeeLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<EmployeeLog>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }

    public EmployeeLog createEmployeeLog(EmployeeLog employeeLog) throws BusinessLogicException {
        //validateEmployeeLog(employeeLog);
        //validateUnique(employeeLog);
        return employeeLogRepository.save(employeeLog);
    }

    private void validateEmployeeLog(EmployeeLog employeeLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(employeeLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto EmployeeLog");
        } else if (StringUtils.isEmpty(employeeLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto EmployeeLog");
        } 
    }
    
    private void validateUnique(EmployeeLog employeeLog) throws ExistentEntityException {
        List<EmployeeLog> employeeLoges = employeeLogRepository.findByName(employeeLog.getName());
        if (employeeLoges!=null && !employeeLoges.isEmpty()) {
            throw new ExistentEntityException(EmployeeLog.class,"name="+employeeLog.getName());
        } 
    }

    public EmployeeLog updateEmployeeLog(UUID employeeLogId, EmployeeLog employeeLog) throws EntityNotExistentException {
        EmployeeLog persistedEmployeeLog = getById(employeeLogId);
        if (persistedEmployeeLog != null) {
            persistedEmployeeLog.setName(employeeLog.getName());
            return employeeLogRepository.save(persistedEmployeeLog);
        } else {
            throw new EntityNotExistentException(EmployeeLog.class,employeeLogId.toString());
        }
    }

    public void deleteEmployeeLog(UUID employeeLogId) throws EntityNotExistentException {
        EmployeeLog employeeLog = getById(employeeLogId);
        employeeLog.setDeleted(Boolean.TRUE);
        employeeLog.setActive(Boolean.FALSE);
        employeeLogRepository.save(employeeLog);
    }

    public List<EmployeeLog> findAll(){
        return employeeLogRepository.findAll();
    }
    
    public EmployeeLog getByName(String name){
        return employeeLogRepository.getByName(name);
    }
    
    public List<EmployeeLog> findByNameIgnoreCaseContaining(String name){
        return employeeLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<EmployeeLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return employeeLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}
