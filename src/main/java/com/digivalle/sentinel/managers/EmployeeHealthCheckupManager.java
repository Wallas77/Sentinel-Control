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
import com.digivalle.sentinel.models.EmployeeHealthCheckup;
import com.digivalle.sentinel.repositories.EmployeeHealthCheckupRepository;
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
public class EmployeeHealthCheckupManager {
    
    @Autowired
    private EmployeeHealthCheckupRepository employeeHealthCheckupRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public EmployeeHealthCheckup getById(UUID id) throws EntityNotExistentException {
        Optional<EmployeeHealthCheckup> employeeHealthCheckup = employeeHealthCheckupRepository.findById(id);
        if (!employeeHealthCheckup.isEmpty()) {
            return employeeHealthCheckup.get();
        }
        throw new EntityNotExistentException(EmployeeHealthCheckup.class,id.toString());
    }
    
    public PagedResponse<EmployeeHealthCheckup> getEmployeeHealthCheckup(EmployeeHealthCheckup filter, Paging paging){
        
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<EmployeeHealthCheckup> cq = cb.createQuery(EmployeeHealthCheckup.class);
        Root<EmployeeHealthCheckup> root = cq.from(EmployeeHealthCheckup.class);
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
        if(filter.getIssuedDate()!=null && filter.getIssuedDate2()!=null){
            predicates.add(cb.between(root.get("issuedDate"), filter.getIssuedDate(),filter.getIssuedDate2()));
            cq.orderBy(cb.desc(root.get("issuedDate")));
        }
        if(filter.getSerial()!=null){
            predicates.add(cb.equal(root.get("serial"), filter.getSerial()));
        }
        if(filter.getName()!=null){
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase()+ "%"));
        }
        if(filter.getNotes()!=null){
            predicates.add(cb.like(cb.lower(root.get("notes")), "%" + filter.getNotes().toLowerCase()+ "%"));
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
        if(filter.getFileName()!=null){
            predicates.add(cb.like(cb.lower(root.get("fileName")), "%" + filter.getFileName().toLowerCase()+ "%"));
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
        
        TypedQuery<EmployeeHealthCheckup> query = entityManager.createQuery(cq);
        
        int iTotal = query.getResultList().size();

        
        
        List<EmployeeHealthCheckup> result = query.setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        
        Page<EmployeeHealthCheckup> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<EmployeeHealthCheckup>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }

    public EmployeeHealthCheckup createEmployeeHealthCheckup(EmployeeHealthCheckup employeeHealthCheckup) throws BusinessLogicException, ExistentEntityException {
        validateEmployeeHealthCheckup(employeeHealthCheckup);
        validateUnique(employeeHealthCheckup);
        return employeeHealthCheckupRepository.save(employeeHealthCheckup);
    }

    private void validateEmployeeHealthCheckup(EmployeeHealthCheckup employeeHealthCheckup) throws BusinessLogicException {
        if (StringUtils.isEmpty(employeeHealthCheckup.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto EmployeeHealthCheckup");
        } else if (StringUtils.isEmpty(employeeHealthCheckup.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto EmployeeHealthCheckup");
        } else if(employeeHealthCheckup.getEmployee().getId()==null){
            throw new BusinessLogicException("El campo Employee es requerido para el objeto EmployeeHealthCheckup");
        } 
    }
    
    private void validateUnique(EmployeeHealthCheckup employeeHealthCheckup) throws ExistentEntityException {
        List<EmployeeHealthCheckup> employeeHealthCheckupes = employeeHealthCheckupRepository.findByNameAndEmployeeAndDeleted(employeeHealthCheckup.getName(),employeeHealthCheckup.getEmployee(),Boolean.FALSE);
        if (employeeHealthCheckupes!=null && !employeeHealthCheckupes.isEmpty()) {
            throw new ExistentEntityException(EmployeeHealthCheckup.class,"name="+employeeHealthCheckup.getName()+", employeeId="+employeeHealthCheckup.getEmployee().getId());
        } 
    }

    public EmployeeHealthCheckup updateEmployeeHealthCheckup(UUID employeeHealthCheckupId, EmployeeHealthCheckup employeeHealthCheckup) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(employeeHealthCheckup.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto EmployeeHealthCheckup");
        } 
    
        EmployeeHealthCheckup persistedEmployeeHealthCheckup = getById(employeeHealthCheckupId);
        if (persistedEmployeeHealthCheckup != null) {
            if(employeeHealthCheckup.getName()!=null){
                persistedEmployeeHealthCheckup.setName(employeeHealthCheckup.getName());
            }
            if(employeeHealthCheckup.getEmployee()!=null){
                persistedEmployeeHealthCheckup.setEmployee(employeeHealthCheckup.getEmployee());
            }
            if(employeeHealthCheckup.getNotes()!=null){
                persistedEmployeeHealthCheckup.setNotes(employeeHealthCheckup.getNotes());
            }
            if(employeeHealthCheckup.getFile()!=null){
                persistedEmployeeHealthCheckup.setFile(employeeHealthCheckup.getFile());
            }
            if(employeeHealthCheckup.getFileName()!=null){
                persistedEmployeeHealthCheckup.setFileName(employeeHealthCheckup.getFileName());
            }
            if(employeeHealthCheckup.getIssuedDate()!=null){
                persistedEmployeeHealthCheckup.setIssuedDate(employeeHealthCheckup.getIssuedDate());
            }
            if(employeeHealthCheckup.getActive()!=null){
                persistedEmployeeHealthCheckup.setActive(employeeHealthCheckup.getActive());
            }
            persistedEmployeeHealthCheckup.setUpdateUser(employeeHealthCheckup.getUpdateUser());
            return employeeHealthCheckupRepository.save(persistedEmployeeHealthCheckup);
        } else {
            throw new EntityNotExistentException(EmployeeHealthCheckup.class,employeeHealthCheckupId.toString());
        }
    }

    public EmployeeHealthCheckup deleteEmployeeHealthCheckup(UUID employeeHealthCheckupId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto EmployeeHealthCheckup");
        } 
        EmployeeHealthCheckup employeeHealthCheckup = getById(employeeHealthCheckupId);
        employeeHealthCheckup.setDeleted(Boolean.TRUE);
        employeeHealthCheckup.setActive(Boolean.FALSE);
        return employeeHealthCheckupRepository.save(employeeHealthCheckup);
    }

    public List<EmployeeHealthCheckup> findAll(){
        return employeeHealthCheckupRepository.findAll();
    }
    
    public EmployeeHealthCheckup getByName(String name){
        return employeeHealthCheckupRepository.getByName(name);
    }
    
    public List<EmployeeHealthCheckup> findByNameIgnoreCaseContaining(String name){
        return employeeHealthCheckupRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<EmployeeHealthCheckup> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return employeeHealthCheckupRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public EmployeeHealthCheckup getBySerial(Integer serial) {
        return employeeHealthCheckupRepository.getBySerial(serial);
    }
}
