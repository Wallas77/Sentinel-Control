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
import com.digivalle.sentinel.repositories.PayrollRepository;
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
public class PayrollManager {
    
    @Autowired
    private PayrollRepository payrollRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public Payroll getById(UUID id) throws EntityNotExistentException {
        Optional<Payroll> payroll = payrollRepository.findById(id);
        if (!payroll.isEmpty()) {
            return payroll.get();
        }
        throw new EntityNotExistentException(Payroll.class,id.toString());
    }
    
    public PagedResponse<Payroll> getPayroll(Payroll filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Payroll> cq = cb.createQuery(Payroll.class);
        Root<Payroll> root = cq.from(Payroll.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<Payroll> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<Payroll> result = query.getResultList();
        
        Page<Payroll> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(Payroll filter, CriteriaBuilder cb, Root<Payroll> root) {
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
        if(filter.getSerial()!=null){
            predicates.add(cb.equal(root.get("serial"), filter.getSerial()));
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

    private void applySorting(CriteriaQuery<Payroll> cq, CriteriaBuilder cb, Root<Payroll> root, Payroll filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, Payroll filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Payroll> countRoot = countQuery.from(Payroll.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public Payroll createPayroll(Payroll payroll) throws BusinessLogicException, ExistentEntityException {
        validatePayroll(payroll);
        validateUnique(payroll);
        return payrollRepository.save(payroll);
    }

    private void validatePayroll(Payroll payroll) throws BusinessLogicException {
        if (StringUtils.isEmpty(payroll.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto Payroll");
        } else if (StringUtils.isEmpty(payroll.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Payroll");
        } else if (payroll.getPayrollRoles()==null) {
            throw new BusinessLogicException("El campo PayrollRoles es requerido para el objeto Payroll");
        } 
    }
    
    private void validateUnique(Payroll payroll) throws ExistentEntityException {
        List<Payroll> payrolles = payrollRepository.findByName(payroll.getName());
        if (payrolles!=null && !payrolles.isEmpty()) {
            throw new ExistentEntityException(Payroll.class,"name="+payroll.getName());
        } 
    }

    public Payroll updatePayroll(UUID payrollId, Payroll payroll) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(payroll.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Payroll");
        } 
    
        Payroll persistedPayroll = getById(payrollId);
        if (persistedPayroll != null) {
            if(payroll.getName()!=null){
                persistedPayroll.setName(payroll.getName());
            }
            if(payroll.getEndDate()!=null){
                persistedPayroll.setEndDate(payroll.getEndDate());
            }
            if(payroll.getPayrollRoles()!=null){
                persistedPayroll.setPayrollRoles(payroll.getPayrollRoles());
            }
            if(payroll.getPayrollStatusEnum()!=null){
                persistedPayroll.setPayrollStatusEnum(payroll.getPayrollStatusEnum());
            }
            if(payroll.getScheduledDate()!=null){
                persistedPayroll.setScheduledDate(payroll.getScheduledDate());
            }
            if(payroll.getStartDate()!=null){
                persistedPayroll.setStartDate(payroll.getStartDate());
            }
            if(payroll.getActive()!=null){
                persistedPayroll.setActive(payroll.getActive());
            }
            persistedPayroll.setUpdateUser(payroll.getUpdateUser());
            return payrollRepository.save(persistedPayroll);
        } else {
            throw new EntityNotExistentException(Payroll.class,payrollId.toString());
        }
    }

    public Payroll deletePayroll(UUID payrollId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto Payroll");
        } 
        Payroll payroll = getById(payrollId);
        payroll.setDeleted(Boolean.TRUE);
        payroll.setActive(Boolean.FALSE);
        return payrollRepository.save(payroll);
    }

    public List<Payroll> findAll(){
        return payrollRepository.findAll();
    }
    
    public Payroll getByName(String name){
        return payrollRepository.getByName(name);
    }
    
    public List<Payroll> findByNameIgnoreCaseContaining(String name){
        return payrollRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<Payroll> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return payrollRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public Payroll getBySerial(Integer serial) {
        return payrollRepository.getBySerial(serial);
    }
}
