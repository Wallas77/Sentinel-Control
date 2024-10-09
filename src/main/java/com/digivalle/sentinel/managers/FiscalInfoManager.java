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
import com.digivalle.sentinel.models.FiscalInfo;
import com.digivalle.sentinel.repositories.FiscalInfoRepository;
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
public class FiscalInfoManager {
    
    @Autowired
    private FiscalInfoRepository fiscalInfoRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public FiscalInfo getById(UUID id) throws EntityNotExistentException {
        Optional<FiscalInfo> fiscalInfo = fiscalInfoRepository.findById(id);
        if (!fiscalInfo.isEmpty()) {
            return fiscalInfo.get();
        }
        throw new EntityNotExistentException(FiscalInfo.class,id.toString());
    }
    
    public PagedResponse<FiscalInfo> getFiscalInfo(FiscalInfo filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<FiscalInfo> cq = cb.createQuery(FiscalInfo.class);
        Root<FiscalInfo> root = cq.from(FiscalInfo.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<FiscalInfo> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<FiscalInfo> result = query.getResultList();
        
        Page<FiscalInfo> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(FiscalInfo filter, CriteriaBuilder cb, Root<FiscalInfo> root) {
        List<Predicate> predicates = new ArrayList<>();

        if(filter.getCreationDate()!=null && filter.getCreationDate2()!=null){
            predicates.add(cb.between(root.get("creationDate"), filter.getCreationDate(),filter.getCreationDate2()));
        }
        if(filter.getUpdateDate()!=null && filter.getUpdateDate2()!=null){
            predicates.add(cb.between(root.get("updateDate"), filter.getUpdateDate(),filter.getUpdateDate2()));
        }
        if(filter.getSerial()!=null){
            predicates.add(cb.equal(root.get("serial"), filter.getSerial()));
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

    private void applySorting(CriteriaQuery<FiscalInfo> cq, CriteriaBuilder cb, Root<FiscalInfo> root, FiscalInfo filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, FiscalInfo filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<FiscalInfo> countRoot = countQuery.from(FiscalInfo.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    

    public FiscalInfo createFiscalInfo(FiscalInfo fiscalInfo) throws BusinessLogicException, ExistentEntityException {
        validateFiscalInfo(fiscalInfo);
        validateUnique(fiscalInfo);
        return fiscalInfoRepository.save(fiscalInfo);
    }

    private void validateFiscalInfo(FiscalInfo fiscalInfo) throws BusinessLogicException {
        if (StringUtils.isEmpty(fiscalInfo.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto FiscalInfo");
        } else if (StringUtils.isEmpty(fiscalInfo.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto FiscalInfo");
        } 
    }
    
    private void validateUnique(FiscalInfo fiscalInfo) throws ExistentEntityException {
        List<FiscalInfo> fiscalInfoes = fiscalInfoRepository.findByName(fiscalInfo.getName());
        if (fiscalInfoes!=null && !fiscalInfoes.isEmpty()) {
            throw new ExistentEntityException(FiscalInfo.class,"name="+fiscalInfo.getName());
        } 
    }

    public FiscalInfo updateFiscalInfo(UUID fiscalInfoId, FiscalInfo fiscalInfo) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(fiscalInfo.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto FiscalInfo");
        } 
    
        FiscalInfo persistedFiscalInfo = getById(fiscalInfoId);
        if (persistedFiscalInfo != null) {
            if(fiscalInfo.getCity()!=null){
                persistedFiscalInfo.setCity(fiscalInfo.getCity());
            }
            if(fiscalInfo.getCode()!=null){
                persistedFiscalInfo.setCode(fiscalInfo.getCode());
            }
            if(fiscalInfo.getColony()!=null){
                persistedFiscalInfo.setColony(fiscalInfo.getColony());
            }
            if(fiscalInfo.getCountry()!=null){
                persistedFiscalInfo.setCountry(fiscalInfo.getCountry());
            }
            if(fiscalInfo.getExternalNumber()!=null){
                persistedFiscalInfo.setExternalNumber(fiscalInfo.getExternalNumber());
            }
            if(fiscalInfo.getInternalNumber()!=null){
                persistedFiscalInfo.setInternalNumber(fiscalInfo.getInternalNumber());
            }
            if(fiscalInfo.getName()!=null){
                persistedFiscalInfo.setName(fiscalInfo.getName());
            }
            if(fiscalInfo.getState()!=null){
                persistedFiscalInfo.setState(fiscalInfo.getState());
            }
            if(fiscalInfo.getStreet()!=null){
                persistedFiscalInfo.setStreet(fiscalInfo.getStreet());
            }
            if(fiscalInfo.getSuburb()!=null){
                persistedFiscalInfo.setSuburb(fiscalInfo.getSuburb());
            }
            if(fiscalInfo.getZipCode()!=null){
                persistedFiscalInfo.setZipCode(fiscalInfo.getZipCode());
            }
            if(fiscalInfo.getActive()!=null){
                persistedFiscalInfo.setActive(fiscalInfo.getActive());
            }
            persistedFiscalInfo.setUpdateUser(fiscalInfo.getUpdateUser());
            return fiscalInfoRepository.save(persistedFiscalInfo);
        } else {
            throw new EntityNotExistentException(FiscalInfo.class,fiscalInfoId.toString());
        }
    }

    public FiscalInfo deleteFiscalInfo(UUID fiscalInfoId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto FiscalInfo");
        } 
        FiscalInfo fiscalInfo = getById(fiscalInfoId);
        fiscalInfo.setDeleted(Boolean.TRUE);
        fiscalInfo.setActive(Boolean.FALSE);
        return fiscalInfoRepository.save(fiscalInfo);
    }

    public List<FiscalInfo> findAll(){
        return fiscalInfoRepository.findAll();
    }
    
    public FiscalInfo getByName(String name){
        return fiscalInfoRepository.getByName(name);
    }
    
    public List<FiscalInfo> findByNameIgnoreCaseContaining(String name){
        return fiscalInfoRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<FiscalInfo> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return fiscalInfoRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public FiscalInfo getBySerial(Integer serial) {
        return fiscalInfoRepository.getBySerial(serial);
    }
}
