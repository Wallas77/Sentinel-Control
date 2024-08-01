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
import com.digivalle.sentinel.models.EmployeeTraining;
import com.digivalle.sentinel.repositories.EmployeeTrainingRepository;
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
public class EmployeeTrainingManager {
    
    @Autowired
    private EmployeeTrainingRepository employeeTrainingRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public EmployeeTraining getById(UUID id) throws EntityNotExistentException {
        Optional<EmployeeTraining> employeeTraining = employeeTrainingRepository.findById(id);
        if (!employeeTraining.isEmpty()) {
            return employeeTraining.get();
        }
        throw new EntityNotExistentException(EmployeeTraining.class,id.toString());
    }
    
    public PagedResponse<EmployeeTraining> getEmployeeTraining(EmployeeTraining filter, Paging paging){
        
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<EmployeeTraining> cq = cb.createQuery(EmployeeTraining.class);
        Root<EmployeeTraining> root = cq.from(EmployeeTraining.class);
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
        if(filter.getFileName()!=null){
            predicates.add(cb.like(cb.lower(root.get("fileName")), "%" + filter.getFileName().toLowerCase()+ "%"));
        }
        if(filter.getEvaluation()!=null){
            predicates.add(cb.like(cb.lower(root.get("evaluation")), "%" + filter.getEvaluation().toLowerCase()+ "%"));
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
        
        TypedQuery<EmployeeTraining> query = entityManager.createQuery(cq);
        
        int iTotal = query.getResultList().size();

        
        
        List<EmployeeTraining> result = query.setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        
        Page<EmployeeTraining> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<EmployeeTraining>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }

    public EmployeeTraining createEmployeeTraining(EmployeeTraining employeeTraining) throws BusinessLogicException, ExistentEntityException {
        validateEmployeeTraining(employeeTraining);
        validateUnique(employeeTraining);
        return employeeTrainingRepository.save(employeeTraining);
    }

    private void validateEmployeeTraining(EmployeeTraining employeeTraining) throws BusinessLogicException {
        if (StringUtils.isEmpty(employeeTraining.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto EmployeeTraining");
        } else if (StringUtils.isEmpty(employeeTraining.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto EmployeeTraining");
        } else if(employeeTraining.getEmployee().getId()==null){
            throw new BusinessLogicException("El campo Employee es requerido para el objeto EmployeeTraining");
        } 
    }
    
    private void validateUnique(EmployeeTraining employeeTraining) throws ExistentEntityException {
        List<EmployeeTraining> employeeTraininges = employeeTrainingRepository.findByNameAndEmployeeAndDeleted(employeeTraining.getName(),employeeTraining.getEmployee(),Boolean.FALSE);
        if (employeeTraininges!=null && !employeeTraininges.isEmpty()) {
            throw new ExistentEntityException(EmployeeTraining.class,"name="+employeeTraining.getName()+", employeeId="+employeeTraining.getEmployee().getId());
        } 
    }

    public EmployeeTraining updateEmployeeTraining(UUID employeeTrainingId, EmployeeTraining employeeTraining) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(employeeTraining.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto EmployeeTraining");
        } 
    
        EmployeeTraining persistedEmployeeTraining = getById(employeeTrainingId);
        if (persistedEmployeeTraining != null) {
            if(employeeTraining.getName()!=null){
                persistedEmployeeTraining.setName(employeeTraining.getName());
            }
            if(employeeTraining.getEmployee()!=null){
                persistedEmployeeTraining.setEmployee(employeeTraining.getEmployee());
            }
            if(employeeTraining.getDescription()!=null){
                persistedEmployeeTraining.setDescription(employeeTraining.getDescription());
            }
            if(employeeTraining.getFile()!=null){
                persistedEmployeeTraining.setFile(employeeTraining.getFile());
            }
            if(employeeTraining.getFileName()!=null){
                persistedEmployeeTraining.setFileName(employeeTraining.getFileName());
            }
            if(employeeTraining.getIssuedDate()!=null){
                persistedEmployeeTraining.setIssuedDate(employeeTraining.getIssuedDate());
            }
            if(employeeTraining.getEvaluation()!=null){
                persistedEmployeeTraining.setEvaluation(employeeTraining.getEvaluation());
            }
            if(employeeTraining.getActive()!=null){
                persistedEmployeeTraining.setActive(employeeTraining.getActive());
            }
            persistedEmployeeTraining.setUpdateUser(employeeTraining.getUpdateUser());
            return employeeTrainingRepository.save(persistedEmployeeTraining);
        } else {
            throw new EntityNotExistentException(EmployeeTraining.class,employeeTrainingId.toString());
        }
    }

    public EmployeeTraining deleteEmployeeTraining(UUID employeeTrainingId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto EmployeeTraining");
        } 
        EmployeeTraining employeeTraining = getById(employeeTrainingId);
        employeeTraining.setDeleted(Boolean.TRUE);
        employeeTraining.setActive(Boolean.FALSE);
        return employeeTrainingRepository.save(employeeTraining);
    }

    public List<EmployeeTraining> findAll(){
        return employeeTrainingRepository.findAll();
    }
    
    public EmployeeTraining getByName(String name){
        return employeeTrainingRepository.getByName(name);
    }
    
    public List<EmployeeTraining> findByNameIgnoreCaseContaining(String name){
        return employeeTrainingRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<EmployeeTraining> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return employeeTrainingRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public EmployeeTraining getBySerial(Integer serial) {
        return employeeTrainingRepository.getBySerial(serial);
    }
}
