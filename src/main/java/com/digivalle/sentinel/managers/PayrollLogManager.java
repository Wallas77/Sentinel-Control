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
import com.digivalle.sentinel.models.PayrollLog;
import com.digivalle.sentinel.repositories.PayrollLogRepository;
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
public class PayrollLogManager {
    
    @Autowired
    private PayrollLogRepository payrollLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public PayrollLog getById(UUID id) throws EntityNotExistentException {
        Optional<PayrollLog> payrollLog = payrollLogRepository.findById(id);
        if (!payrollLog.isEmpty()) {
            return payrollLog.get();
        }
        throw new EntityNotExistentException(PayrollLog.class,id.toString());
    }
    
    public PagedResponse<PayrollLog> getPayrollLog(PayrollLog filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PayrollLog> cq = cb.createQuery(PayrollLog.class);
        Root<PayrollLog> root = cq.from(PayrollLog.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<PayrollLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<PayrollLog> result = query.getResultList();
        
        Page<PayrollLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(PayrollLog filter, CriteriaBuilder cb, Root<PayrollLog> root) {
        List<Predicate> predicates = new ArrayList<>();

        if(filter.getCreationDate()!=null && filter.getCreationDate2()!=null){
            predicates.add(cb.between(root.get("creationDate"), filter.getCreationDate(),filter.getCreationDate2()));
        }
        if(filter.getUpdateDate()!=null && filter.getUpdateDate2()!=null){
            predicates.add(cb.between(root.get("updateDate"), filter.getUpdateDate(),filter.getUpdateDate2()));
        }
        
        if(filter.getName()!=null){
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase()+ "%"));
        }
        if(filter.getStartDate()!=null && filter.getEndDate()!=null){
             predicates.add(cb.between(root.get("startDate"), filter.getStartDate(),filter.getEndDate()));
             predicates.add(cb.between(root.get("endDate"), filter.getStartDate(),filter.getEndDate()));
        }
        if(filter.getPayrollStatusEnum()!=null){
            predicates.add(cb.equal(root.get("payrollStatusEnum"), filter.getPayrollStatusEnum()));
        }
        if(filter.getScheduledDate()!=null && filter.getScheduledDate2()!=null){
            predicates.add(cb.between(root.get("scheduledDate"), filter.getScheduledDate(),filter.getScheduledDate2()));
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

    private void applySorting(CriteriaQuery<PayrollLog> cq, CriteriaBuilder cb, Root<PayrollLog> root, PayrollLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, PayrollLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<PayrollLog> countRoot = countQuery.from(PayrollLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public PayrollLog createPayrollLog(PayrollLog payrollLog) throws BusinessLogicException {
        //validatePayrollLog(payrollLog);
        //validateUnique(payrollLog);
        return payrollLogRepository.save(payrollLog);
    }

    private void validatePayrollLog(PayrollLog payrollLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(payrollLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto PayrollLog");
        } else if (StringUtils.isEmpty(payrollLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto PayrollLog");
        } 
    }
    
    private void validateUnique(PayrollLog payrollLog) throws ExistentEntityException {
        List<PayrollLog> payrollLoges = payrollLogRepository.findByName(payrollLog.getName());
        if (payrollLoges!=null && !payrollLoges.isEmpty()) {
            throw new ExistentEntityException(PayrollLog.class,"name="+payrollLog.getName());
        } 
    }

    public PayrollLog updatePayrollLog(UUID payrollLogId, PayrollLog payrollLog) throws EntityNotExistentException {
        PayrollLog persistedPayrollLog = getById(payrollLogId);
        if (persistedPayrollLog != null) {
            persistedPayrollLog.setName(payrollLog.getName());
            return payrollLogRepository.save(persistedPayrollLog);
        } else {
            throw new EntityNotExistentException(PayrollLog.class,payrollLogId.toString());
        }
    }

    public void deletePayrollLog(UUID payrollLogId) throws EntityNotExistentException {
        PayrollLog payrollLog = getById(payrollLogId);
        payrollLog.setDeleted(Boolean.TRUE);
        payrollLog.setActive(Boolean.FALSE);
        payrollLogRepository.save(payrollLog);
    }

    public List<PayrollLog> findAll(){
        return payrollLogRepository.findAll();
    }
    
    public PayrollLog getByName(String name){
        return payrollLogRepository.getByName(name);
    }
    
    public List<PayrollLog> findByNameIgnoreCaseContaining(String name){
        return payrollLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<PayrollLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return payrollLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}
