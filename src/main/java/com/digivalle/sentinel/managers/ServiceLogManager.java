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
import com.digivalle.sentinel.models.ServiceLog;
import com.digivalle.sentinel.repositories.ServiceLogRepository;
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
public class ServiceLogManager {
    
    @Autowired
    private ServiceLogRepository ServiceLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public ServiceLog getById(UUID id) throws EntityNotExistentException {
        Optional<ServiceLog> ServiceLog = ServiceLogRepository.findById(id);
        if (!ServiceLog.isEmpty()) {
            return ServiceLog.get();
        }
        throw new EntityNotExistentException(ServiceLog.class,id.toString());
    }
    
    public PagedResponse<ServiceLog> getServiceLog(ServiceLog filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ServiceLog> cq = cb.createQuery(ServiceLog.class);
        Root<ServiceLog> root = cq.from(ServiceLog.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<ServiceLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<ServiceLog> result = query.getResultList();
        
        Page<ServiceLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(ServiceLog filter, CriteriaBuilder cb, Root<ServiceLog> root) {
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
        if(filter.getCustomer()!=null){
            if(filter.getCustomer().getId()!=null){
                predicates.add(cb.equal(root.get("customer").get("id"), filter.getCustomer().getId()));
            }
            if(filter.getCustomer().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("customer").get("name")), "%" + filter.getCustomer().getName().toLowerCase()+ "%"));
            }
        }
        if(filter.getStartContractDate()!=null && filter.getStartContractDate2()!=null){
            predicates.add(cb.between(root.get("startContractDate"), filter.getStartContractDate(),filter.getStartContractDate2()));
        }
        if(filter.getEndContractDate()!=null && filter.getEndContractDate2()!=null){
            predicates.add(cb.between(root.get("endContractDate"), filter.getEndContractDate(),filter.getEndContractDate2()));
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
        if(filter.getServiceId()!=null){
            predicates.add(cb.equal(root.get("ServiceId"), filter.getServiceId()));
        }
        if(filter.getTransactionId()!=null){
            predicates.add(cb.equal(root.get("transactionId"), filter.getTransactionId()));
        }
        if(filter.getAction()!=null){
            predicates.add(cb.equal(root.get("action"), filter.getAction()));
        }

        return predicates;
    }

    private void applySorting(CriteriaQuery<ServiceLog> cq, CriteriaBuilder cb, Root<ServiceLog> root, ServiceLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, ServiceLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ServiceLog> countRoot = countQuery.from(ServiceLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public ServiceLog createServiceLog(ServiceLog ServiceLog) throws BusinessLogicException {
        //validateServiceLog(ServiceLog);
        //validateUnique(ServiceLog);
        return ServiceLogRepository.save(ServiceLog);
    }

    private void validateServiceLog(ServiceLog ServiceLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(ServiceLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto ServiceLog");
        } else if (StringUtils.isEmpty(ServiceLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto ServiceLog");
        } 
    }
    
    private void validateUnique(ServiceLog ServiceLog) throws ExistentEntityException {
        List<ServiceLog> ServiceLoges = ServiceLogRepository.findByName(ServiceLog.getName());
        if (ServiceLoges!=null && !ServiceLoges.isEmpty()) {
            throw new ExistentEntityException(ServiceLog.class,"name="+ServiceLog.getName());
        } 
    }

    public ServiceLog updateServiceLog(UUID ServiceLogId, ServiceLog ServiceLog) throws EntityNotExistentException {
        ServiceLog persistedServiceLog = getById(ServiceLogId);
        if (persistedServiceLog != null) {
            persistedServiceLog.setName(ServiceLog.getName());
            return ServiceLogRepository.save(persistedServiceLog);
        } else {
            throw new EntityNotExistentException(ServiceLog.class,ServiceLogId.toString());
        }
    }

    public void deleteServiceLog(UUID ServiceLogId) throws EntityNotExistentException {
        ServiceLog ServiceLog = getById(ServiceLogId);
        ServiceLog.setDeleted(Boolean.TRUE);
        ServiceLog.setActive(Boolean.FALSE);
        ServiceLogRepository.save(ServiceLog);
    }

    public List<ServiceLog> findAll(){
        return ServiceLogRepository.findAll();
    }
    
    public ServiceLog getByName(String name){
        return ServiceLogRepository.getByName(name);
    }
    
    public List<ServiceLog> findByNameIgnoreCaseContaining(String name){
        return ServiceLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<ServiceLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return ServiceLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}
