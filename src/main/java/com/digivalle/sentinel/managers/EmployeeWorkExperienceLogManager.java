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
import com.digivalle.sentinel.models.EmployeeWorkExperienceLog;
import com.digivalle.sentinel.repositories.EmployeeWorkExperienceLogRepository;
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

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<EmployeeWorkExperienceLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<EmployeeWorkExperienceLog> result = query.getResultList();
        
        Page<EmployeeWorkExperienceLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(EmployeeWorkExperienceLog filter, CriteriaBuilder cb, Root<EmployeeWorkExperienceLog> root) {
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
        }
        if(filter.getStartDate()!=null && filter.getStartDate2()!=null){
            predicates.add(cb.between(root.get("startDate"), filter.getStartDate(),filter.getStartDate2()));
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

        return predicates;
    }

    private void applySorting(CriteriaQuery<EmployeeWorkExperienceLog> cq, CriteriaBuilder cb, Root<EmployeeWorkExperienceLog> root, EmployeeWorkExperienceLog filter) {
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

    private long countTotal(CriteriaBuilder cb, EmployeeWorkExperienceLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<EmployeeWorkExperienceLog> countRoot = countQuery.from(EmployeeWorkExperienceLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
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
