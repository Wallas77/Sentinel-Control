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
import com.digivalle.sentinel.models.CountryLog;
import com.digivalle.sentinel.repositories.CountryLogRepository;
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
public class CountryLogManager {
    
    @Autowired
    private CountryLogRepository countryLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public CountryLog getById(UUID id) throws EntityNotExistentException {
        Optional<CountryLog> countryLog = countryLogRepository.findById(id);
        if (!countryLog.isEmpty()) {
            return countryLog.get();
        }
        throw new EntityNotExistentException(CountryLog.class,id.toString());
    }
    
    public PagedResponse<CountryLog> getCountryLog(CountryLog filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<CountryLog> cq = cb.createQuery(CountryLog.class);
        Root<CountryLog> root = cq.from(CountryLog.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<CountryLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<CountryLog> result = query.getResultList();
        
        Page<CountryLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(CountryLog filter, CriteriaBuilder cb, Root<CountryLog> root) {
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
        if(filter.getCode()!=null){
            predicates.add(cb.like(cb.lower(root.get("code")), "%" + filter.getCode().toLowerCase()+ "%"));
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
        if(filter.getCountryId()!=null){
            predicates.add(cb.equal(root.get("countryId"), filter.getCountryId()));
        }
        if(filter.getTransactionId()!=null){
            predicates.add(cb.equal(root.get("transactionId"), filter.getTransactionId()));
        }
        if(filter.getAction()!=null){
            predicates.add(cb.equal(root.get("action"), filter.getAction()));
        }

        return predicates;
    }

    private void applySorting(CriteriaQuery<CountryLog> cq, CriteriaBuilder cb, Root<CountryLog> root, CountryLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, CountryLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<CountryLog> countRoot = countQuery.from(CountryLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public CountryLog createCountryLog(CountryLog countryLog) throws BusinessLogicException {
        //validateCountryLog(countryLog);
        //validateUnique(countryLog);
        return countryLogRepository.save(countryLog);
    }

    private void validateCountryLog(CountryLog countryLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(countryLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto CountryLog");
        } else if (StringUtils.isEmpty(countryLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto CountryLog");
        } 
    }
    
    private void validateUnique(CountryLog countryLog) throws ExistentEntityException {
        List<CountryLog> countryLoges = countryLogRepository.findByName(countryLog.getName());
        if (countryLoges!=null && !countryLoges.isEmpty()) {
            throw new ExistentEntityException(CountryLog.class,"name="+countryLog.getName());
        } 
    }

    public CountryLog updateCountryLog(UUID countryLogId, CountryLog countryLog) throws EntityNotExistentException {
        CountryLog persistedCountryLog = getById(countryLogId);
        if (persistedCountryLog != null) {
            persistedCountryLog.setName(countryLog.getName());
            return countryLogRepository.save(persistedCountryLog);
        } else {
            throw new EntityNotExistentException(CountryLog.class,countryLogId.toString());
        }
    }

    public void deleteCountryLog(UUID countryLogId) throws EntityNotExistentException {
        CountryLog countryLog = getById(countryLogId);
        countryLog.setDeleted(Boolean.TRUE);
        countryLog.setActive(Boolean.FALSE);
        countryLogRepository.save(countryLog);
    }

    public List<CountryLog> findAll(){
        return countryLogRepository.findAll();
    }
    
    public CountryLog getByName(String name){
        return countryLogRepository.getByName(name);
    }
    
    public List<CountryLog> findByNameIgnoreCaseContaining(String name){
        return countryLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<CountryLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return countryLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}
