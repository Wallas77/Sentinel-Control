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
import com.digivalle.sentinel.models.PayrollDetailLog;
import com.digivalle.sentinel.repositories.PayrollDetailLogRepository;
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
public class PayrollDetailLogManager {
    
    @Autowired
    private PayrollDetailLogRepository payrollDetailLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public PayrollDetailLog getById(UUID id) throws EntityNotExistentException {
        Optional<PayrollDetailLog> payrollDetailLog = payrollDetailLogRepository.findById(id);
        if (!payrollDetailLog.isEmpty()) {
            return payrollDetailLog.get();
        }
        throw new EntityNotExistentException(PayrollDetailLog.class,id.toString());
    }
    
    public PagedResponse<PayrollDetailLog> getPayrollDetailLog(PayrollDetailLog filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PayrollDetailLog> cq = cb.createQuery(PayrollDetailLog.class);
        Root<PayrollDetailLog> root = cq.from(PayrollDetailLog.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<PayrollDetailLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<PayrollDetailLog> result = query.getResultList();
        
        Page<PayrollDetailLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(PayrollDetailLog filter, CriteriaBuilder cb, Root<PayrollDetailLog> root) {
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
        if(filter.getActivitiesBonusAmount()!=null && filter.getActivitiesBonusAmount2()!=null){
            predicates.add(cb.between(root.get("activitiesBonusAmount"), filter.getActivitiesBonusAmount(),filter.getActivitiesBonusAmount2()));
        }
        if(filter.getDaysWorked()!=null && filter.getDaysWorked2()!=null){
            predicates.add(cb.between(root.get("daysWorked"), filter.getDaysWorked(),filter.getDaysWorked2()));
        }
        if(filter.getEmployee()!=null){
            if(filter.getEmployee().getId()!=null){
                predicates.add(cb.equal(root.get("employee").get("id"), filter.getEmployee().getId()));
            }
            if(filter.getEmployee().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("employee").get("name")), "%" + filter.getEmployee().getName().toLowerCase()+ "%"));
            }
        }
        if(filter.getStartDate()!=null){
            predicates.add(cb.equal(root.get("startDate"), filter.getStartDate()));
        }
        if(filter.getEndDate()!=null){
            predicates.add(cb.equal(root.get("endDate"), filter.getEndDate()));
        }
        if(filter.getIncidentsAmount()!=null && filter.getIncidentsAmount2()!=null){
            predicates.add(cb.between(root.get("incidentsAmount"), filter.getIncidentsAmount(),filter.getIncidentsAmount2()));
        }
        if(filter.getPayRollSalary()!=null && filter.getPayRollSalary2()!=null){
            predicates.add(cb.between(root.get("payRollSalary"), filter.getPayRollSalary(),filter.getPayRollSalary2()));
        }
        if(filter.getPayrollDays()!=null && filter.getPayrollDays2()!=null){
            predicates.add(cb.between(root.get("payrollDays"), filter.getPayrollDays(),filter.getPayrollDays2()));
        }
        if(filter.getSalaryAmount()!=null && filter.getSalaryAmount2()!=null){
            predicates.add(cb.between(root.get("salaryAmount"), filter.getSalaryAmount(),filter.getSalaryAmount2()));
        }
        if(filter.getSalaryPerDay()!=null && filter.getSalaryPerDay2()!=null){
            predicates.add(cb.between(root.get("salaryPerDay"), filter.getSalaryPerDay(),filter.getSalaryPerDay2()));
        }
        if(filter.getTotalPayRollSalary()!=null && filter.getTotalPayRollSalary2()!=null){
            predicates.add(cb.between(root.get("totalPayRollSalary"), filter.getTotalPayRollSalary(),filter.getTotalPayRollSalary2()));
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
        if(filter.getPayrollDetailId()!=null){
            predicates.add(cb.equal(root.get("payrollDetailId"), filter.getPayrollDetailId()));
        }
        if(filter.getTransactionId()!=null){
            predicates.add(cb.equal(root.get("transactionId"), filter.getTransactionId()));
        }
        if(filter.getAction()!=null){
            predicates.add(cb.equal(root.get("action"), filter.getAction()));
        }

        return predicates;
    }

    private void applySorting(CriteriaQuery<PayrollDetailLog> cq, CriteriaBuilder cb, Root<PayrollDetailLog> root, PayrollDetailLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, PayrollDetailLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<PayrollDetailLog> countRoot = countQuery.from(PayrollDetailLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public PayrollDetailLog createPayrollDetailLog(PayrollDetailLog payrollDetailLog) throws BusinessLogicException {
        //validatePayrollDetailLog(payrollDetailLog);
        //validateUnique(payrollDetailLog);
        return payrollDetailLogRepository.save(payrollDetailLog);
    }

    private void validatePayrollDetailLog(PayrollDetailLog payrollDetailLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(payrollDetailLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto PayrollDetailLog");
        } else if (StringUtils.isEmpty(payrollDetailLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto PayrollDetailLog");
        } 
    }
    
    private void validateUnique(PayrollDetailLog payrollDetailLog) throws ExistentEntityException {
        List<PayrollDetailLog> payrollDetailLoges = payrollDetailLogRepository.findByName(payrollDetailLog.getName());
        if (payrollDetailLoges!=null && !payrollDetailLoges.isEmpty()) {
            throw new ExistentEntityException(PayrollDetailLog.class,"name="+payrollDetailLog.getName());
        } 
    }

    public PayrollDetailLog updatePayrollDetailLog(UUID payrollDetailLogId, PayrollDetailLog payrollDetailLog) throws EntityNotExistentException {
        PayrollDetailLog persistedPayrollDetailLog = getById(payrollDetailLogId);
        if (persistedPayrollDetailLog != null) {
            persistedPayrollDetailLog.setName(payrollDetailLog.getName());
            return payrollDetailLogRepository.save(persistedPayrollDetailLog);
        } else {
            throw new EntityNotExistentException(PayrollDetailLog.class,payrollDetailLogId.toString());
        }
    }

    public void deletePayrollDetailLog(UUID payrollDetailLogId) throws EntityNotExistentException {
        PayrollDetailLog payrollDetailLog = getById(payrollDetailLogId);
        payrollDetailLog.setDeleted(Boolean.TRUE);
        payrollDetailLog.setActive(Boolean.FALSE);
        payrollDetailLogRepository.save(payrollDetailLog);
    }

    public List<PayrollDetailLog> findAll(){
        return payrollDetailLogRepository.findAll();
    }
    
    public PayrollDetailLog getByName(String name){
        return payrollDetailLogRepository.getByName(name);
    }
    
    public List<PayrollDetailLog> findByNameIgnoreCaseContaining(String name){
        return payrollDetailLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<PayrollDetailLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return payrollDetailLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}
