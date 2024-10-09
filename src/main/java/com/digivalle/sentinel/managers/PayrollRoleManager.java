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
import com.digivalle.sentinel.models.Role;
import com.digivalle.sentinel.repositories.PayrollRoleRepository;
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
public class PayrollRoleManager {
    
    @Autowired
    private PayrollRoleRepository payrollRoleRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public PayrollRole getById(UUID id) throws EntityNotExistentException {
        Optional<PayrollRole> payrollRole = payrollRoleRepository.findById(id);
        if (!payrollRole.isEmpty()) {
            return payrollRole.get();
        }
        throw new EntityNotExistentException(PayrollRole.class,id.toString());
    }
    
    public PagedResponse<PayrollRole> getPayrollRole(PayrollRole filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<PayrollRole> cq = cb.createQuery(PayrollRole.class);
        Root<PayrollRole> root = cq.from(PayrollRole.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<PayrollRole> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<PayrollRole> result = query.getResultList();
        
        Page<PayrollRole> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(PayrollRole filter, CriteriaBuilder cb, Root<PayrollRole> root) {
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

        return predicates;
    }

    private void applySorting(CriteriaQuery<PayrollRole> cq, CriteriaBuilder cb, Root<PayrollRole> root, PayrollRole filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, PayrollRole filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<PayrollRole> countRoot = countQuery.from(PayrollRole.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public PayrollRole createPayrollRole(PayrollRole payrollRole) throws BusinessLogicException, ExistentEntityException {
        validatePayrollRole(payrollRole);
        validateUnique(payrollRole);
        return payrollRoleRepository.save(payrollRole);
    }

    private void validatePayrollRole(PayrollRole payrollRole) throws BusinessLogicException {
        if (payrollRole.getPayroll()==null) {
            throw new BusinessLogicException("El campo Payroll es requerido para el objeto PayrollRole");
        } else if (payrollRole.getRole()==null) {
            throw new BusinessLogicException("El campo Role es requerido para el objeto PayrollRole");
        }  else if (StringUtils.isEmpty(payrollRole.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto PayrollRole");
        } 
    }
    
    private void validateUnique(PayrollRole payrollRole) throws ExistentEntityException {
        List<PayrollRole> payrollRolees = payrollRoleRepository.findByPayrollAndRoleAndActiveAndDeleted(payrollRole.getPayroll(),payrollRole.getRole(),Boolean.TRUE,Boolean.FALSE);
        if (payrollRolees!=null && !payrollRolees.isEmpty()) {
            throw new ExistentEntityException(PayrollRole.class,"payrole="+payrollRole.getPayroll().getName()+", role="+payrollRole.getRole().getName());
        } 
    }

    public PayrollRole updatePayrollRole(UUID payrollRoleId, PayrollRole payrollRole) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(payrollRole.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto PayrollRole");
        } 
    
        PayrollRole persistedPayrollRole = getById(payrollRoleId);
        if (persistedPayrollRole != null) {
            if(payrollRole.getPayroll()!=null){
                persistedPayrollRole.setPayroll(payrollRole.getPayroll());
            }
            if(payrollRole.getRole()!=null){
                persistedPayrollRole.setRole(payrollRole.getRole());
            }
            if(payrollRole.getActive()!=null){
                persistedPayrollRole.setActive(payrollRole.getActive());
            }
            persistedPayrollRole.setUpdateUser(payrollRole.getUpdateUser());
            return payrollRoleRepository.save(persistedPayrollRole);
        } else {
            throw new EntityNotExistentException(PayrollRole.class,payrollRoleId.toString());
        }
    }

    public PayrollRole deletePayrollRole(UUID payrollRoleId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto PayrollRole");
        } 
        PayrollRole payrollRole = getById(payrollRoleId);
        payrollRole.setDeleted(Boolean.TRUE);
        payrollRole.setActive(Boolean.FALSE);
        return payrollRoleRepository.save(payrollRole);
    }

    public List<PayrollRole> findAll(){
        return payrollRoleRepository.findAll();
    }
    
    public List<PayrollRole> findByPayrollAndActiveAndDeleted(Payroll payroll, Boolean active, Boolean deleted){
        return payrollRoleRepository.findByPayrollAndActiveAndDeleted(payroll, active, deleted);
    }
    
    public List<PayrollRole> findByPayrollAndRoleAndActiveAndDeleted(Payroll payroll, Role role, Boolean active, Boolean deleted){
        return payrollRoleRepository.findByPayrollAndRoleAndActiveAndDeleted(payroll,role, active, deleted);
    }
    
    
}
