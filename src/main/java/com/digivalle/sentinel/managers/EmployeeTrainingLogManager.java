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
import com.digivalle.sentinel.models.EmployeeTrainingLog;
import com.digivalle.sentinel.repositories.EmployeeTrainingLogRepository;
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
public class EmployeeTrainingLogManager {
    
    @Autowired
    private EmployeeTrainingLogRepository employeeTrainingLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public EmployeeTrainingLog getById(UUID id) throws EntityNotExistentException {
        Optional<EmployeeTrainingLog> employeeTrainingLog = employeeTrainingLogRepository.findById(id);
        if (!employeeTrainingLog.isEmpty()) {
            return employeeTrainingLog.get();
        }
        throw new EntityNotExistentException(EmployeeTrainingLog.class,id.toString());
    }
    
    public PagedResponse<EmployeeTrainingLog> getEmployeeTrainingLog(EmployeeTrainingLog filter, Paging paging){
        
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<EmployeeTrainingLog> cq = cb.createQuery(EmployeeTrainingLog.class);
        Root<EmployeeTrainingLog> root = cq.from(EmployeeTrainingLog.class);
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
        if(filter.getEmployeeTrainingId()!=null){
            predicates.add(cb.equal(root.get("employeeTrainingId"), filter.getEmployeeTrainingId()));
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
        
        TypedQuery<EmployeeTrainingLog> query = entityManager.createQuery(cq);
        
        int iTotal = query.getResultList().size();

        
        
        List<EmployeeTrainingLog> result = query.setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        
        Page<EmployeeTrainingLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<EmployeeTrainingLog>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }

    public EmployeeTrainingLog createEmployeeTrainingLog(EmployeeTrainingLog employeeTrainingLog) throws BusinessLogicException {
        //validateEmployeeTrainingLog(employeeTrainingLog);
        //validateUnique(employeeTrainingLog);
        return employeeTrainingLogRepository.save(employeeTrainingLog);
    }

    private void validateEmployeeTrainingLog(EmployeeTrainingLog employeeTrainingLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(employeeTrainingLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto EmployeeTrainingLog");
        } else if (StringUtils.isEmpty(employeeTrainingLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto EmployeeTrainingLog");
        } 
    }
    
    private void validateUnique(EmployeeTrainingLog employeeTrainingLog) throws ExistentEntityException {
        List<EmployeeTrainingLog> employeeTrainingLoges = employeeTrainingLogRepository.findByName(employeeTrainingLog.getName());
        if (employeeTrainingLoges!=null && !employeeTrainingLoges.isEmpty()) {
            throw new ExistentEntityException(EmployeeTrainingLog.class,"name="+employeeTrainingLog.getName());
        } 
    }

    public EmployeeTrainingLog updateEmployeeTrainingLog(UUID employeeTrainingLogId, EmployeeTrainingLog employeeTrainingLog) throws EntityNotExistentException {
        EmployeeTrainingLog persistedEmployeeTrainingLog = getById(employeeTrainingLogId);
        if (persistedEmployeeTrainingLog != null) {
            persistedEmployeeTrainingLog.setName(employeeTrainingLog.getName());
            return employeeTrainingLogRepository.save(persistedEmployeeTrainingLog);
        } else {
            throw new EntityNotExistentException(EmployeeTrainingLog.class,employeeTrainingLogId.toString());
        }
    }

    public void deleteEmployeeTrainingLog(UUID employeeTrainingLogId) throws EntityNotExistentException {
        EmployeeTrainingLog employeeTrainingLog = getById(employeeTrainingLogId);
        employeeTrainingLog.setDeleted(Boolean.TRUE);
        employeeTrainingLog.setActive(Boolean.FALSE);
        employeeTrainingLogRepository.save(employeeTrainingLog);
    }

    public List<EmployeeTrainingLog> findAll(){
        return employeeTrainingLogRepository.findAll();
    }
    
    public EmployeeTrainingLog getByName(String name){
        return employeeTrainingLogRepository.getByName(name);
    }
    
    public List<EmployeeTrainingLog> findByNameIgnoreCaseContaining(String name){
        return employeeTrainingLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<EmployeeTrainingLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return employeeTrainingLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}
