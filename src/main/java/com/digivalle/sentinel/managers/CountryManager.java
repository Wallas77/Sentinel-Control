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
import com.digivalle.sentinel.models.Country;
import com.digivalle.sentinel.repositories.CountryRepository;
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
public class CountryManager {
    
    @Autowired
    private CountryRepository countryRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public Country getById(UUID id) throws EntityNotExistentException {
        Optional<Country> country = countryRepository.findById(id);
        if (!country.isEmpty()) {
            return country.get();
        }
        throw new EntityNotExistentException(Country.class,id.toString());
    }
    
    public PagedResponse<Country> getCountry(Country filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Country> cq = cb.createQuery(Country.class);
        Root<Country> root = cq.from(Country.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<Country> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<Country> result = query.getResultList();
        
        Page<Country> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
   private List<Predicate> buildPredicates(Country filter, CriteriaBuilder cb, Root<Country> root) {
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

        return predicates;
    }

    private void applySorting(CriteriaQuery<Country> cq, CriteriaBuilder cb, Root<Country> root, Country filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, Country filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Country> countRoot = countQuery.from(Country.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(new Predicate[0]));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public Country createCountry(Country country) throws BusinessLogicException, ExistentEntityException {
        validateCountry(country);
        validateUnique(country);
        return countryRepository.save(country);
    }

    private void validateCountry(Country country) throws BusinessLogicException {
        if (StringUtils.isEmpty(country.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto Country");
        } else if (StringUtils.isEmpty(country.getCode())) {
            throw new BusinessLogicException("El campo Code es requerido para el objeto Country");
        } else if (StringUtils.isEmpty(country.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Country");
        } 
    }
    
    private void validateUnique(Country country) throws ExistentEntityException {
        List<Country> countryes = countryRepository.findByName(country.getName());
        if (countryes!=null && !countryes.isEmpty()) {
            throw new ExistentEntityException(Country.class,"name="+country.getName());
        } 
    }

    public Country updateCountry(UUID countryId, Country country) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(country.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Country");
        } 
    
        Country persistedCountry = getById(countryId);
        if (persistedCountry != null) {
            if(country.getName()!=null){
                persistedCountry.setName(country.getName());
            }
            if(country.getCode()!=null){
                persistedCountry.setCode(country.getCode());
            }
            if(country.getActive()!=null){
                persistedCountry.setActive(country.getActive());
            }
            persistedCountry.setUpdateUser(country.getUpdateUser());
            return countryRepository.save(persistedCountry);
        } else {
            throw new EntityNotExistentException(Country.class,countryId.toString());
        }
    }

    public Country deleteCountry(UUID countryId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto Country");
        } 
        Country country = getById(countryId);
        country.setDeleted(Boolean.TRUE);
        country.setActive(Boolean.FALSE);
        return countryRepository.save(country);
    }

    public List<Country> findAll(){
        return countryRepository.findAll();
    }
    
    public Country getByName(String name){
        return countryRepository.getByName(name);
    }
    
    public List<Country> findByNameIgnoreCaseContaining(String name){
        return countryRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<Country> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return countryRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public Country getBySerial(Integer serial) {
        return countryRepository.getBySerial(serial);
    }
}
