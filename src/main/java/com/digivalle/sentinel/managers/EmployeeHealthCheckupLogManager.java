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
import com.digivalle.sentinel.models.EmployeeHealthCheckupLog;
import com.digivalle.sentinel.repositories.EmployeeHealthCheckupLogRepository;
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
public class EmployeeHealthCheckupLogManager {
    
    @Autowired
    private EmployeeHealthCheckupLogRepository employeeHealthCheckupLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public EmployeeHealthCheckupLog getById(UUID id) throws EntityNotExistentException {
        Optional<EmployeeHealthCheckupLog> employeeHealthCheckupLog = employeeHealthCheckupLogRepository.findById(id);
        if (!employeeHealthCheckupLog.isEmpty()) {
            return employeeHealthCheckupLog.get();
        }
        throw new EntityNotExistentException(EmployeeHealthCheckupLog.class,id.toString());
    }
    
    public PagedResponse<EmployeeHealthCheckupLog> getEmployeeHealthCheckupLog(EmployeeHealthCheckupLog filter, Paging paging){
        
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<EmployeeHealthCheckupLog> cq = cb.createQuery(EmployeeHealthCheckupLog.class);
        Root<EmployeeHealthCheckupLog> root = cq.from(EmployeeHealthCheckupLog.class);
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
        if(filter.getEmployeeHealthCheckupId()!=null){
            predicates.add(cb.equal(root.get("employeeHealthCheckupId"), filter.getEmployeeHealthCheckupId()));
        }
        if(filter.getTransactionId()!=null){
            predicates.add(cb.equal(root.get("transactionId"), filter.getTransactionId()));
        }
        if(filter.getAction()!=null){
            predicates.add(cb.equal(root.get("action"), filter.getAction()));
        }
        
        cq.select(root);
        if(predicates.size()>0){
            cq.where(predicates.toArray(new Predicate[0]));
        }
        
        TypedQuery<EmployeeHealthCheckupLog> query = entityManager.createQuery(cq);
        
        int iTotal = query.getResultList().size();

        
        
        List<EmployeeHealthCheckupLog> result = query.setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        
        Page<EmployeeHealthCheckupLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<EmployeeHealthCheckupLog>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }

    public EmployeeHealthCheckupLog createEmployeeHealthCheckupLog(EmployeeHealthCheckupLog employeeHealthCheckupLog) throws BusinessLogicException {
        //validateEmployeeHealthCheckupLog(employeeHealthCheckupLog);
        //validateUnique(employeeHealthCheckupLog);
        return employeeHealthCheckupLogRepository.save(employeeHealthCheckupLog);
    }

    private void validateEmployeeHealthCheckupLog(EmployeeHealthCheckupLog employeeHealthCheckupLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(employeeHealthCheckupLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto EmployeeHealthCheckupLog");
        } else if (StringUtils.isEmpty(employeeHealthCheckupLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto EmployeeHealthCheckupLog");
        } 
    }
    
    private void validateUnique(EmployeeHealthCheckupLog employeeHealthCheckupLog) throws ExistentEntityException {
        List<EmployeeHealthCheckupLog> employeeHealthCheckupLoges = employeeHealthCheckupLogRepository.findByName(employeeHealthCheckupLog.getName());
        if (employeeHealthCheckupLoges!=null && !employeeHealthCheckupLoges.isEmpty()) {
            throw new ExistentEntityException(EmployeeHealthCheckupLog.class,"name="+employeeHealthCheckupLog.getName());
        } 
    }

    public EmployeeHealthCheckupLog updateEmployeeHealthCheckupLog(UUID employeeHealthCheckupLogId, EmployeeHealthCheckupLog employeeHealthCheckupLog) throws EntityNotExistentException {
        EmployeeHealthCheckupLog persistedEmployeeHealthCheckupLog = getById(employeeHealthCheckupLogId);
        if (persistedEmployeeHealthCheckupLog != null) {
            persistedEmployeeHealthCheckupLog.setName(employeeHealthCheckupLog.getName());
            return employeeHealthCheckupLogRepository.save(persistedEmployeeHealthCheckupLog);
        } else {
            throw new EntityNotExistentException(EmployeeHealthCheckupLog.class,employeeHealthCheckupLogId.toString());
        }
    }

    public void deleteEmployeeHealthCheckupLog(UUID employeeHealthCheckupLogId) throws EntityNotExistentException {
        EmployeeHealthCheckupLog employeeHealthCheckupLog = getById(employeeHealthCheckupLogId);
        employeeHealthCheckupLog.setDeleted(Boolean.TRUE);
        employeeHealthCheckupLog.setActive(Boolean.FALSE);
        employeeHealthCheckupLogRepository.save(employeeHealthCheckupLog);
    }

    public List<EmployeeHealthCheckupLog> findAll(){
        return employeeHealthCheckupLogRepository.findAll();
    }
    
    public EmployeeHealthCheckupLog getByName(String name){
        return employeeHealthCheckupLogRepository.getByName(name);
    }
    
    public List<EmployeeHealthCheckupLog> findByNameIgnoreCaseContaining(String name){
        return employeeHealthCheckupLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<EmployeeHealthCheckupLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return employeeHealthCheckupLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}