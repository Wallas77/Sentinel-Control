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
import com.digivalle.sentinel.models.EmployeeDocumentLog;
import com.digivalle.sentinel.repositories.EmployeeDocumentLogRepository;
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
public class EmployeeDocumentLogManager {
    
    @Autowired
    private EmployeeDocumentLogRepository employeeDocumentLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public EmployeeDocumentLog getById(UUID id) throws EntityNotExistentException {
        Optional<EmployeeDocumentLog> employeeDocumentLog = employeeDocumentLogRepository.findById(id);
        if (!employeeDocumentLog.isEmpty()) {
            return employeeDocumentLog.get();
        }
        throw new EntityNotExistentException(EmployeeDocumentLog.class,id.toString());
    }
    
    public PagedResponse<EmployeeDocumentLog> getEmployeeDocumentLog(EmployeeDocumentLog filter, Paging paging){
        
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<EmployeeDocumentLog> cq = cb.createQuery(EmployeeDocumentLog.class);
        Root<EmployeeDocumentLog> root = cq.from(EmployeeDocumentLog.class);
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
        if(filter.getDescription()!=null){
            predicates.add(cb.like(cb.lower(root.get("description")), "%" + filter.getDescription().toLowerCase()+ "%"));
        }
        if(filter.getFileName()!=null){
            predicates.add(cb.like(cb.lower(root.get("fileName")), "%" + filter.getFileName().toLowerCase()+ "%"));
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
        if(filter.getActive()!=null){
            predicates.add(cb.equal(root.get("active"), filter.getActive()));
        }
        if(filter.getDeleted()!=null){
            predicates.add(cb.equal(root.get("deleted"), filter.getDeleted()));
        }
        if(filter.getUpdateUser()!=null){
            predicates.add(cb.equal(root.get("updateUser"), filter.getUpdateUser()));
        }
        if(filter.getEmployeeDocumentId()!=null){
            predicates.add(cb.equal(root.get("employeeDocumentId"), filter.getEmployeeDocumentId()));
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
        
        TypedQuery<EmployeeDocumentLog> query = entityManager.createQuery(cq);
        
        int iTotal = query.getResultList().size();

        
        
        List<EmployeeDocumentLog> result = query.setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        
        Page<EmployeeDocumentLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<EmployeeDocumentLog>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }

    public EmployeeDocumentLog createEmployeeDocumentLog(EmployeeDocumentLog employeeDocumentLog) throws BusinessLogicException {
        //validateEmployeeDocumentLog(employeeDocumentLog);
        //validateUnique(employeeDocumentLog);
        return employeeDocumentLogRepository.save(employeeDocumentLog);
    }

    private void validateEmployeeDocumentLog(EmployeeDocumentLog employeeDocumentLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(employeeDocumentLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto EmployeeDocumentLog");
        } else if (StringUtils.isEmpty(employeeDocumentLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto EmployeeDocumentLog");
        } 
    }
    
    private void validateUnique(EmployeeDocumentLog employeeDocumentLog) throws ExistentEntityException {
        List<EmployeeDocumentLog> employeeDocumentLoges = employeeDocumentLogRepository.findByName(employeeDocumentLog.getName());
        if (employeeDocumentLoges!=null && !employeeDocumentLoges.isEmpty()) {
            throw new ExistentEntityException(EmployeeDocumentLog.class,"name="+employeeDocumentLog.getName());
        } 
    }

    public EmployeeDocumentLog updateEmployeeDocumentLog(UUID employeeDocumentLogId, EmployeeDocumentLog employeeDocumentLog) throws EntityNotExistentException {
        EmployeeDocumentLog persistedEmployeeDocumentLog = getById(employeeDocumentLogId);
        if (persistedEmployeeDocumentLog != null) {
            persistedEmployeeDocumentLog.setName(employeeDocumentLog.getName());
            return employeeDocumentLogRepository.save(persistedEmployeeDocumentLog);
        } else {
            throw new EntityNotExistentException(EmployeeDocumentLog.class,employeeDocumentLogId.toString());
        }
    }

    public void deleteEmployeeDocumentLog(UUID employeeDocumentLogId) throws EntityNotExistentException {
        EmployeeDocumentLog employeeDocumentLog = getById(employeeDocumentLogId);
        employeeDocumentLog.setDeleted(Boolean.TRUE);
        employeeDocumentLog.setActive(Boolean.FALSE);
        employeeDocumentLogRepository.save(employeeDocumentLog);
    }

    public List<EmployeeDocumentLog> findAll(){
        return employeeDocumentLogRepository.findAll();
    }
    
    public EmployeeDocumentLog getByName(String name){
        return employeeDocumentLogRepository.getByName(name);
    }
    
    public List<EmployeeDocumentLog> findByNameIgnoreCaseContaining(String name){
        return employeeDocumentLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<EmployeeDocumentLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return employeeDocumentLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}