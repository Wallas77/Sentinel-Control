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
import com.digivalle.sentinel.models.FiscalInfoLog;
import com.digivalle.sentinel.repositories.FiscalInfoLogRepository;
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
public class FiscalInfoLogManager {
    
    @Autowired
    private FiscalInfoLogRepository fiscalInfoLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public FiscalInfoLog getById(UUID id) throws EntityNotExistentException {
        Optional<FiscalInfoLog> fiscalInfoLog = fiscalInfoLogRepository.findById(id);
        if (!fiscalInfoLog.isEmpty()) {
            return fiscalInfoLog.get();
        }
        throw new EntityNotExistentException(FiscalInfoLog.class,id.toString());
    }
    
    public PagedResponse<FiscalInfoLog> getFiscalInfoLog(FiscalInfoLog filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<FiscalInfoLog> cq = cb.createQuery(FiscalInfoLog.class);
        Root<FiscalInfoLog> root = cq.from(FiscalInfoLog.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<FiscalInfoLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<FiscalInfoLog> result = query.getResultList();
        
        Page<FiscalInfoLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(FiscalInfoLog filter, CriteriaBuilder cb, Root<FiscalInfoLog> root) {
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
        
        if(filter.getName()!=null){
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase()+ "%"));
        }
        if(filter.getCity()!=null){
            predicates.add(cb.like(cb.lower(root.get("city")), "%" + filter.getCity().toLowerCase()+ "%"));
        }
        if(filter.getCode()!=null){
            predicates.add(cb.like(cb.lower(root.get("code")), "%" + filter.getCode().toLowerCase()+ "%"));
        }
        if(filter.getColony()!=null){
            predicates.add(cb.like(cb.lower(root.get("colony")), "%" + filter.getColony().toLowerCase()+ "%"));
        }
        if(filter.getCountry()!=null){
            if(filter.getCountry().getCode()!=null){
                predicates.add(cb.like(cb.lower(root.get("country").get("code")), "%" + filter.getCountry().getCode().toLowerCase()+ "%"));
            }
            if(filter.getCountry().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("country").get("name")), "%" + filter.getCountry().getName().toLowerCase()+ "%"));
            }
        }
        if(filter.getCustomer()!=null){
            if(filter.getCustomer().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("customer").get("name")), "%" + filter.getCustomer().getName().toLowerCase()+ "%"));
            }
        }
        if(filter.getExternalNumber()!=null){
            predicates.add(cb.like(cb.lower(root.get("externalNumber")), "%" + filter.getExternalNumber().toLowerCase()+ "%"));
        }
        if(filter.getInternalNumber()!=null){
            predicates.add(cb.like(cb.lower(root.get("internalNumber")), "%" + filter.getInternalNumber().toLowerCase()+ "%"));
        }
        if(filter.getState()!=null){
            predicates.add(cb.like(cb.lower(root.get("state")), "%" + filter.getState().toLowerCase()+ "%"));
        }
        if(filter.getStreet()!=null){
            predicates.add(cb.like(cb.lower(root.get("street")), "%" + filter.getStreet().toLowerCase()+ "%"));
        }
        if(filter.getSuburb()!=null){
            predicates.add(cb.like(cb.lower(root.get("suburb")), "%" + filter.getSuburb().toLowerCase()+ "%"));
        }
        if(filter.getZipCode()!=null){
            predicates.add(cb.like(cb.lower(root.get("zipCode")), "%" + filter.getZipCode().toLowerCase()+ "%"));
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

    private void applySorting(CriteriaQuery<FiscalInfoLog> cq, CriteriaBuilder cb, Root<FiscalInfoLog> root, FiscalInfoLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, FiscalInfoLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<FiscalInfoLog> countRoot = countQuery.from(FiscalInfoLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    

    public FiscalInfoLog createFiscalInfoLog(FiscalInfoLog fiscalInfoLog) throws BusinessLogicException {
        //validateFiscalInfoLog(fiscalInfoLog);
        //validateUnique(fiscalInfoLog);
        return fiscalInfoLogRepository.save(fiscalInfoLog);
    }

    private void validateFiscalInfoLog(FiscalInfoLog fiscalInfoLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(fiscalInfoLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto FiscalInfoLog");
        } else if (StringUtils.isEmpty(fiscalInfoLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto FiscalInfoLog");
        } 
    }
    
    private void validateUnique(FiscalInfoLog fiscalInfoLog) throws ExistentEntityException {
        List<FiscalInfoLog> fiscalInfoLoges = fiscalInfoLogRepository.findByName(fiscalInfoLog.getName());
        if (fiscalInfoLoges!=null && !fiscalInfoLoges.isEmpty()) {
            throw new ExistentEntityException(FiscalInfoLog.class,"name="+fiscalInfoLog.getName());
        } 
    }

    public FiscalInfoLog updateFiscalInfoLog(UUID fiscalInfoLogId, FiscalInfoLog fiscalInfoLog) throws EntityNotExistentException {
        FiscalInfoLog persistedFiscalInfoLog = getById(fiscalInfoLogId);
        if (persistedFiscalInfoLog != null) {
            persistedFiscalInfoLog.setName(fiscalInfoLog.getName());
            return fiscalInfoLogRepository.save(persistedFiscalInfoLog);
        } else {
            throw new EntityNotExistentException(FiscalInfoLog.class,fiscalInfoLogId.toString());
        }
    }

    public void deleteFiscalInfoLog(UUID fiscalInfoLogId) throws EntityNotExistentException {
        FiscalInfoLog fiscalInfoLog = getById(fiscalInfoLogId);
        fiscalInfoLog.setDeleted(Boolean.TRUE);
        fiscalInfoLog.setActive(Boolean.FALSE);
        fiscalInfoLogRepository.save(fiscalInfoLog);
    }

    public List<FiscalInfoLog> findAll(){
        return fiscalInfoLogRepository.findAll();
    }
    
    public FiscalInfoLog getByName(String name){
        return fiscalInfoLogRepository.getByName(name);
    }
    
    public List<FiscalInfoLog> findByNameIgnoreCaseContaining(String name){
        return fiscalInfoLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<FiscalInfoLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return fiscalInfoLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}
