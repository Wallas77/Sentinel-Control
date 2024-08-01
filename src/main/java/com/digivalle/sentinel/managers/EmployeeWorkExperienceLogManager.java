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
import com.digivalle.sentinel.models.EmployeeWorkExperienceLog;
import com.digivalle.sentinel.repositories.EmployeeWorkExperienceLogRepository;
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
public class EmployeeWorkExperienceLogManager {
    
    @Autowired
    private EmployeeWorkExperienceLogRepository employeeWorkExperienceLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public EmployeeWorkExperienceLog getById(UUID id) throws EntityNotExistentException {
        Optional<EmployeeWorkExperienceLog> employeeWorkExperienceLog = employeeWorkExperienceLogRepository.findById(id);
        if (!employeeWorkExperienceLog.isEmpty()) {
            return employeeWorkExperienceLog.get();
        }
        throw new EntityNotExistentException(EmployeeWorkExperienceLog.class,id.toString());
    }
    
    public PagedResponse<EmployeeWorkExperienceLog> getEmployeeWorkExperienceLog(EmployeeWorkExperienceLog filter, Paging paging){
        
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<EmployeeWorkExperienceLog> cq = cb.createQuery(EmployeeWorkExperienceLog.class);
        Root<EmployeeWorkExperienceLog> root = cq.from(EmployeeWorkExperienceLog.class);
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
        if(filter.getCompany()!=null){
            predicates.add(cb.like(cb.lower(root.get("company")), "%" + filter.getCompany().toLowerCase()+ "%"));
        }
        if(filter.getDescription()!=null){
            predicates.add(cb.like(cb.lower(root.get("description")), "%" + filter.getDescription().toLowerCase()+ "%"));
        }
        if(filter.getJobTitle()!=null){
            predicates.add(cb.like(cb.lower(root.get("jobTitle")), "%" + filter.getJobTitle().toLowerCase()+ "%"));
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
        if(filter.getActive()!=null){
            predicates.add(cb.equal(root.get("active"), filter.getActive()));
        }
        if(filter.getDeleted()!=null){
            predicates.add(cb.equal(root.get("deleted"), filter.getDeleted()));
        }
        if(filter.getUpdateUser()!=null){
            predicates.add(cb.equal(root.get("updateUser"), filter.getUpdateUser()));
        }
        if(filter.getEmployeeWorkExperienceId()!=null){
            predicates.add(cb.equal(root.get("employeeWorkExperienceId"), filter.getEmployeeWorkExperienceId()));
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
        
        TypedQuery<EmployeeWorkExperienceLog> query = entityManager.createQuery(cq);
        
        int iTotal = query.getResultList().size();

        
        
        List<EmployeeWorkExperienceLog> result = query.setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        
        Page<EmployeeWorkExperienceLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<EmployeeWorkExperienceLog>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }

    public EmployeeWorkExperienceLog createEmployeeWorkExperienceLog(EmployeeWorkExperienceLog employeeWorkExperienceLog) throws BusinessLogicException {
        //validateEmployeeWorkExperienceLog(employeeWorkExperienceLog);
        //validateUnique(employeeWorkExperienceLog);
        return employeeWorkExperienceLogRepository.save(employeeWorkExperienceLog);
    }

    private void validateEmployeeWorkExperienceLog(EmployeeWorkExperienceLog employeeWorkExperienceLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(employeeWorkExperienceLog.getJobTitle())) {
            throw new BusinessLogicException("El campo JobTitle es requerido para el objeto EmployeeWorkExperienceLog");
        } else if (StringUtils.isEmpty(employeeWorkExperienceLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto EmployeeWorkExperienceLog");
        } 
    }
    
    /*private void validateUnique(EmployeeWorkExperienceLog employeeWorkExperienceLog) throws ExistentEntityException {
        List<EmployeeWorkExperienceLog> employeeWorkExperienceLoges = employeeWorkExperienceLogRepository.findByName(employeeWorkExperienceLog.getName());
        if (employeeWorkExperienceLoges!=null && !employeeWorkExperienceLoges.isEmpty()) {
            throw new ExistentEntityException(EmployeeWorkExperienceLog.class,"name="+employeeWorkExperienceLog.getName());
        } 
    }*/

    public EmployeeWorkExperienceLog updateEmployeeWorkExperienceLog(UUID employeeWorkExperienceLogId, EmployeeWorkExperienceLog employeeWorkExperienceLog) throws EntityNotExistentException {
        EmployeeWorkExperienceLog persistedEmployeeWorkExperienceLog = getById(employeeWorkExperienceLogId);
        if (persistedEmployeeWorkExperienceLog != null) {
            
            return employeeWorkExperienceLogRepository.save(persistedEmployeeWorkExperienceLog);
        } else {
            throw new EntityNotExistentException(EmployeeWorkExperienceLog.class,employeeWorkExperienceLogId.toString());
        }
    }

    public void deleteEmployeeWorkExperienceLog(UUID employeeWorkExperienceLogId) throws EntityNotExistentException {
        EmployeeWorkExperienceLog employeeWorkExperienceLog = getById(employeeWorkExperienceLogId);
        employeeWorkExperienceLog.setDeleted(Boolean.TRUE);
        employeeWorkExperienceLog.setActive(Boolean.FALSE);
        employeeWorkExperienceLogRepository.save(employeeWorkExperienceLog);
    }

    public List<EmployeeWorkExperienceLog> findAll(){
        return employeeWorkExperienceLogRepository.findAll();
    }
    
    
    
    
    
}
