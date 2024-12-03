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
import com.digivalle.sentinel.models.Service;
import com.digivalle.sentinel.repositories.ServiceRepository;
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
public class ServiceManager {
    
    @Autowired
    private ServiceRepository ServiceRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public Service getById(UUID id) throws EntityNotExistentException {
        Optional<Service> Service = ServiceRepository.findById(id);
        if (!Service.isEmpty()) {
            return Service.get();
        }
        throw new EntityNotExistentException(Service.class,id.toString());
    }
    
    public PagedResponse<Service> getService(Service filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Service> cq = cb.createQuery(Service.class);
        Root<Service> root = cq.from(Service.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<Service> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<Service> result = query.getResultList();
        
        Page<Service> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(Service filter, CriteriaBuilder cb, Root<Service> root) {
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
        if(filter.getCustomer()!=null){
            if(filter.getCustomer().getId()!=null){
                predicates.add(cb.equal(root.get("customer").get("id"), filter.getCustomer().getId()));
            }
            if(filter.getCustomer().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("customer").get("name")), "%" + filter.getCustomer().getName().toLowerCase()+ "%"));
            }
        }
        if(filter.getBranch()!=null){
            if(filter.getBranch().getId()!=null){
                predicates.add(cb.equal(root.get("branch").get("id"), filter.getBranch().getId()));
            }
            if(filter.getBranch().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("branch").get("name")), "%" + filter.getBranch().getName().toLowerCase()+ "%"));
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

        return predicates;
    }

    private void applySorting(CriteriaQuery<Service> cq, CriteriaBuilder cb, Root<Service> root, Service filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, Service filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Service> countRoot = countQuery.from(Service.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public Service createService(Service Service) throws BusinessLogicException, ExistentEntityException {
        validateService(Service);
        validateUnique(Service);
        return ServiceRepository.save(Service);
    }

    private void validateService(Service service) throws BusinessLogicException {
        if (StringUtils.isEmpty(service.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto Service");
        } else if (StringUtils.isEmpty(service.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Service");
        } else if(service.getStartContractDate()==null){
            throw new BusinessLogicException("El campo StartContractDate es requerido para el objeto Service");
        } else if(service.getEndContractDate()==null){
            throw new BusinessLogicException("El campo EndContractDate es requerido para el objeto Service");
        }
        if(service.getBranch()!=null && service.getBranch().getId()==null){
            service.setBranch(null);
        }
    }
    
    private void validateUnique(Service Service) throws ExistentEntityException {
        List<Service> Servicees = ServiceRepository.findByName(Service.getName());
        if (Servicees!=null && !Servicees.isEmpty()) {
            throw new ExistentEntityException(Service.class,"name="+Service.getName());
        } 
    }

    public Service updateService(UUID ServiceId, Service service) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(service.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Service");
        } 
        if(service.getBranch()!=null && service.getBranch().getId()==null){
            service.setBranch(null);
        }
        Service persistedService = getById(ServiceId);
        if (persistedService != null) {
            if(service.getName()!=null){
                persistedService.setName(service.getName());
            }
            if(service.getCustomer()!=null){
                persistedService.setCustomer(service.getCustomer());
            }
            
            persistedService.setBranch(service.getBranch());
            
            if(service.getEndContractDate()!=null){
                persistedService.setEndContractDate(service.getEndContractDate());
            }
            if(service.getStartContractDate()!=null){
                persistedService.setStartContractDate(service.getStartContractDate());
            }
            if(service.getActive()!=null){
                persistedService.setActive(service.getActive());
            }
            persistedService.setUpdateUser(service.getUpdateUser());
            return ServiceRepository.save(persistedService);
        } else {
            throw new EntityNotExistentException(Service.class,ServiceId.toString());
        }
    }

    public Service deleteService(UUID ServiceId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto Service");
        } 
        Service Service = getById(ServiceId);
        Service.setDeleted(Boolean.TRUE);
        Service.setActive(Boolean.FALSE);
        return ServiceRepository.save(Service);
    }

    public List<Service> findAll(){
        return ServiceRepository.findAll();
    }
    
    public Service getByName(String name){
        return ServiceRepository.getByName(name);
    }
    
    public List<Service> findByNameIgnoreCaseContaining(String name){
        return ServiceRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<Service> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return ServiceRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public Service getBySerial(Integer serial) {
        return ServiceRepository.getBySerial(serial);
    }
}
