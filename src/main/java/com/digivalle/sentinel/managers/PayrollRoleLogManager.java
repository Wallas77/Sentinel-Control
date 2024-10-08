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
import com.digivalle.sentinel.models.Payroll;
import com.digivalle.sentinel.models.PayrollRole;
import com.digivalle.sentinel.models.PayrollRoleLog;
import com.digivalle.sentinel.models.Role;
import com.digivalle.sentinel.repositories.PayrollRoleLogRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
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
public class PayrollRoleLogManager {
    
    @Autowired
    private PayrollRoleLogRepository payrollRoleLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public PayrollRoleLog getById(UUID id) throws EntityNotExistentException {
        Optional<PayrollRoleLog> payrollRoleLog = payrollRoleLogRepository.findById(id);
        if (!payrollRoleLog.isEmpty()) {
            return payrollRoleLog.get();
        }
        throw new EntityNotExistentException(PayrollRoleLog.class,id.toString());
    }
    
    public PagedResponse<PayrollRoleLog> getPayrollRoleLog(PayrollRoleLog filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PayrollRoleLog> cq = cb.createQuery(PayrollRoleLog.class);
        Root<PayrollRoleLog> root = cq.from(PayrollRoleLog.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<PayrollRoleLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<PayrollRoleLog> result = query.getResultList();
        
        Page<PayrollRoleLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(PayrollRoleLog filter, CriteriaBuilder cb, Root<PayrollRoleLog> root) {
        List<Predicate> predicates = new ArrayList<>();

        if(filter.getCreationDate()!=null && filter.getCreationDate2()!=null){
            predicates.add(cb.between(root.get("creationDate"), filter.getCreationDate(),filter.getCreationDate2()));
        }
        if(filter.getUpdateDate()!=null && filter.getUpdateDate2()!=null){
            predicates.add(cb.between(root.get("updateDate"), filter.getUpdateDate(),filter.getUpdateDate2()));
        }
        if(filter.getPayroll()!=null){
            if(filter.getPayroll().getId()!=null){
                predicates.add(cb.equal(root.get("payroll").get("active"), filter.getPayroll().getId()));
            }
            if(filter.getPayroll().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("payroll").get("name")), "%" + filter.getPayroll().getName().toLowerCase()+ "%"));
            }
        }
        if(filter.getRole()!=null){
            if(filter.getRole().getId()!=null){
                predicates.add(cb.equal(root.get("role").get("active"), filter.getRole().getId()));
            }
            if(filter.getRole().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("role").get("name")), "%" + filter.getRole().getName().toLowerCase()+ "%"));
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

        if(filter.getPayrollRoleId()!=null){
            predicates.add(cb.equal(root.get("payrollRoleId"), filter.getPayrollRoleId()));
        }
        if(filter.getTransactionId()!=null){
            predicates.add(cb.equal(root.get("transactionId"), filter.getTransactionId()));
        }
        if(filter.getAction()!=null){
            predicates.add(cb.equal(root.get("action"), filter.getAction()));
        }

        return predicates;
    }

    private void applySorting(CriteriaQuery<PayrollRoleLog> cq, CriteriaBuilder cb, Root<PayrollRoleLog> root, PayrollRoleLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, PayrollRoleLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<PayrollRoleLog> countRoot = countQuery.from(PayrollRoleLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public PayrollRoleLog createPayrollRoleLog(PayrollRoleLog payrollRoleLog) throws BusinessLogicException {
        //validatePayrollRoleLog(payrollRoleLog);
        //validateUnique(payrollRoleLog);
        return payrollRoleLogRepository.save(payrollRoleLog);
    }

    private void validatePayrollRoleLog(PayrollRoleLog payrollRoleLog) throws BusinessLogicException {
        if (payrollRoleLog.getPayroll()==null) {
            throw new BusinessLogicException("El campo Payroll es requerido para el objeto PayrollRoleLog");
        } else if (payrollRoleLog.getRole()==null) {
            throw new BusinessLogicException("El campo Role es requerido para el objeto PayrollRoleLog");
        }  else if (StringUtils.isEmpty(payrollRoleLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto PayrollRoleLog");
        } 
    }
    
    

    public PayrollRoleLog updatePayrollRoleLog(UUID payrollRoleLogId, PayrollRoleLog payrollRoleLog) throws EntityNotExistentException {
        PayrollRoleLog persistedPayrollRoleLog = getById(payrollRoleLogId);
        if (persistedPayrollRoleLog != null) {
            persistedPayrollRoleLog.setPayroll(payrollRoleLog.getPayroll());
            persistedPayrollRoleLog.setRole(payrollRoleLog.getRole());
            return payrollRoleLogRepository.save(persistedPayrollRoleLog);
        } else {
            throw new EntityNotExistentException(PayrollRoleLog.class,payrollRoleLogId.toString());
        }
    }

    public void deletePayrollRoleLog(UUID payrollRoleLogId) throws EntityNotExistentException {
        PayrollRoleLog payrollRoleLog = getById(payrollRoleLogId);
        payrollRoleLog.setDeleted(Boolean.TRUE);
        payrollRoleLog.setActive(Boolean.FALSE);
        payrollRoleLogRepository.save(payrollRoleLog);
    }

    public List<PayrollRoleLog> findAll(){
        return payrollRoleLogRepository.findAll();
    }
    
    
    
}
