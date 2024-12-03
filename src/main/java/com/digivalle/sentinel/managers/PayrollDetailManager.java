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
import com.digivalle.sentinel.models.PayrollDetail;
import com.digivalle.sentinel.repositories.PayrollDetailRepository;
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
public class PayrollDetailManager {
    
    @Autowired
    private PayrollDetailRepository payrollDetailRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public PayrollDetail getById(UUID id) throws EntityNotExistentException {
        Optional<PayrollDetail> payrollDetail = payrollDetailRepository.findById(id);
        if (!payrollDetail.isEmpty()) {
            return payrollDetail.get();
        }
        throw new EntityNotExistentException(PayrollDetail.class,id.toString());
    }
    
    public PagedResponse<PayrollDetail> getPayrollDetail(PayrollDetail filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PayrollDetail> cq = cb.createQuery(PayrollDetail.class);
        Root<PayrollDetail> root = cq.from(PayrollDetail.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<PayrollDetail> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<PayrollDetail> result = query.getResultList();
        
        Page<PayrollDetail> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(PayrollDetail filter, CriteriaBuilder cb, Root<PayrollDetail> root) {
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

        return predicates;
    }

    private void applySorting(CriteriaQuery<PayrollDetail> cq, CriteriaBuilder cb, Root<PayrollDetail> root, PayrollDetail filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, PayrollDetail filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<PayrollDetail> countRoot = countQuery.from(PayrollDetail.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public PayrollDetail createPayrollDetail(PayrollDetail payrollDetail) throws BusinessLogicException, ExistentEntityException {
        validatePayrollDetail(payrollDetail);
        validateUnique(payrollDetail);
        return payrollDetailRepository.save(payrollDetail);
    }

    private void validatePayrollDetail(PayrollDetail payrollDetail) throws BusinessLogicException {
        if (StringUtils.isEmpty(payrollDetail.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto PayrollDetail");
        } else if (StringUtils.isEmpty(payrollDetail.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto PayrollDetail");
        } else if (payrollDetail.getEmployee()==null) {
            throw new BusinessLogicException("El campo Employee es requerido para el objeto PayrollDetail");
        } 
    }
    
    private void validateUnique(PayrollDetail payrollDetail) throws ExistentEntityException {
        List<PayrollDetail> payrollDetailes = payrollDetailRepository.findByName(payrollDetail.getName());
        if (payrollDetailes!=null && !payrollDetailes.isEmpty()) {
            throw new ExistentEntityException(PayrollDetail.class,"name="+payrollDetail.getName());
        } 
    }

    public PayrollDetail updatePayrollDetail(UUID payrollDetailId, PayrollDetail payrollDetail) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(payrollDetail.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto PayrollDetail");
        } 
    
        PayrollDetail persistedPayrollDetail = getById(payrollDetailId);
        if (persistedPayrollDetail != null) {
            if(payrollDetail.getActivitiesBonusAmount()!=null){
                persistedPayrollDetail.setActivitiesBonusAmount(payrollDetail.getActivitiesBonusAmount());
            }
            if(payrollDetail.getDaysWorked()!=null){
                persistedPayrollDetail.setDaysWorked(payrollDetail.getDaysWorked());
            }
            if(payrollDetail.getEmployee()!=null){
                persistedPayrollDetail.setEmployee(payrollDetail.getEmployee());
            }
            if(payrollDetail.getEndDate()!=null){
                persistedPayrollDetail.setEndDate(payrollDetail.getEndDate());
            }
            if(payrollDetail.getIncidentsAmount()!=null){
                persistedPayrollDetail.setIncidentsAmount(payrollDetail.getIncidentsAmount());
            }
            if(payrollDetail.getName()!=null){
                persistedPayrollDetail.setName(payrollDetail.getName());
            }
            if(payrollDetail.getPayRollSalary()!=null){
                persistedPayrollDetail.setPayRollSalary(payrollDetail.getPayRollSalary());
            }
            if(payrollDetail.getPayrollDays()!=null){
                persistedPayrollDetail.setPayrollDays(payrollDetail.getPayrollDays());
            }
            if(payrollDetail.getSalaryAmount()!=null){
                persistedPayrollDetail.setSalaryAmount(payrollDetail.getSalaryAmount());
            }
            if(payrollDetail.getSalaryPerDay()!=null){
                persistedPayrollDetail.setSalaryPerDay(payrollDetail.getSalaryPerDay());
            }
            if(payrollDetail.getStartDate()!=null){
                persistedPayrollDetail.setStartDate(payrollDetail.getStartDate());
            }
            if(payrollDetail.getTotalPayRollSalary()!=null){
                persistedPayrollDetail.setTotalPayRollSalary(payrollDetail.getTotalPayRollSalary());
            }
            if(payrollDetail.getActive()!=null){
                persistedPayrollDetail.setActive(payrollDetail.getActive());
            }
            persistedPayrollDetail.setUpdateUser(payrollDetail.getUpdateUser());
            return payrollDetailRepository.save(persistedPayrollDetail);
        } else {
            throw new EntityNotExistentException(PayrollDetail.class,payrollDetailId.toString());
        }
    }

    public PayrollDetail deletePayrollDetail(UUID payrollDetailId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto PayrollDetail");
        } 
        PayrollDetail payrollDetail = getById(payrollDetailId);
        payrollDetail.setDeleted(Boolean.TRUE);
        payrollDetail.setActive(Boolean.FALSE);
        return payrollDetailRepository.save(payrollDetail);
    }

    public List<PayrollDetail> findAll(){
        return payrollDetailRepository.findAll();
    }
    
    public PayrollDetail getByName(String name){
        return payrollDetailRepository.getByName(name);
    }
    
    public List<PayrollDetail> findByNameIgnoreCaseContaining(String name){
        return payrollDetailRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<PayrollDetail> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return payrollDetailRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public PayrollDetail getBySerial(Integer serial) {
        return payrollDetailRepository.getBySerial(serial);
    }
}
