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
        //cq.orderBy(cb.asc(root.get("id")));

        List<Predicate> predicates = new ArrayList<Predicate>();
        cq.orderBy(cb.desc(root.get("creationDate")));
        if(filter.getCreationDate()!=null && filter.getCreationDate2()!=null){
            predicates.add(cb.between(root.get("creationDate"), filter.getCreationDate(),filter.getCreationDate2()));
        }
        if(filter.getUpdateDate()!=null && filter.getUpdateDate2()!=null){
            predicates.add(cb.between(root.get("updateDate"), filter.getUpdateDate(),filter.getUpdateDate2()));
            cq.orderBy(cb.desc(root.get("updateDate")));
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
        
        cq.select(root);
        if(predicates.size()>0){
            cq.where(predicates.toArray(new Predicate[0]));
        }
        
        TypedQuery<Country> query = entityManager.createQuery(cq);
        
        int iTotal = query.getResultList().size();

        
        
        List<Country> result = query.setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        
        Page<Country> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<Country>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
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