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

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<EmployeeTraining> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<EmployeeTraining> result = query.getResultList();
        
        Page<EmployeeTraining> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(EmployeeTraining filter, CriteriaBuilder cb, Root<EmployeeTraining> root) {
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
        if(filter.getIssuedDate()!=null && filter.getIssuedDate2()!=null){
            predicates.add(cb.between(root.get("issuedDate"), filter.getIssuedDate(),filter.getIssuedDate2()));
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
        
        return predicates;
    }

    private void applySorting(CriteriaQuery<EmployeeTraining> cq, CriteriaBuilder cb, Root<EmployeeTraining> root, EmployeeTraining filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else if (filter.getIssuedDate()!= null && filter.getIssuedDate2() != null) {
            orderList.add(cb.desc(root.get("issuedDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, EmployeeTraining filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<EmployeeTraining> countRoot = countQuery.from(EmployeeTraining.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
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
            if(employeeTraining.getFileFormat()!=null){
                persistedEmployeeTraining.setFileFormat(employeeTraining.getFileFormat());
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
