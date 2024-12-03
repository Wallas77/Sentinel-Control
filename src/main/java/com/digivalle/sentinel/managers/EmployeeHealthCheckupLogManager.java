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

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<EmployeeHealthCheckupLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<EmployeeHealthCheckupLog> result = query.getResultList();
        
        Page<EmployeeHealthCheckupLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(EmployeeHealthCheckupLog filter, CriteriaBuilder cb, Root<EmployeeHealthCheckupLog> root) {
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
        

        return predicates;
    }

    private void applySorting(CriteriaQuery<EmployeeHealthCheckupLog> cq, CriteriaBuilder cb, Root<EmployeeHealthCheckupLog> root, EmployeeHealthCheckupLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, EmployeeHealthCheckupLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<EmployeeHealthCheckupLog> countRoot = countQuery.from(EmployeeHealthCheckupLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
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
