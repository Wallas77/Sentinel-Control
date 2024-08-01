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
import com.digivalle.sentinel.models.CompanyLog;
import com.digivalle.sentinel.repositories.CompanyLogRepository;
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
public class CompanyLogManager {
    
    @Autowired
    private CompanyLogRepository companyLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public CompanyLog getById(UUID id) throws EntityNotExistentException {
        Optional<CompanyLog> companyLog = companyLogRepository.findById(id);
        if (!companyLog.isEmpty()) {
            return companyLog.get();
        }
        throw new EntityNotExistentException(CompanyLog.class,id.toString());
    }
    
    public PagedResponse<CompanyLog> getCompanyLog(CompanyLog filter, Paging paging){
        
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<CompanyLog> cq = cb.createQuery(CompanyLog.class);
        Root<CompanyLog> root = cq.from(CompanyLog.class);
        //cq.orderBy(cb.asc(root.get("id")));

        List<Predicate> predicates = new ArrayList<Predicate>();
        cq.orderBy(cb.desc(root.get("creationDate")));
        
        if(filter.getName()!=null){
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase()+ "%"));
        }
        if(filter.getCity()!=null){
            predicates.add(cb.like(cb.lower(root.get("city")), "%" + filter.getCity().toLowerCase()+ "%"));
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
        if(filter.getFiscalInfo()!=null){
            if(filter.getFiscalInfo().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("fiscalInfo").get("name")), "%" + filter.getFiscalInfo().getName().toLowerCase()+ "%"));
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
        
        cq.select(root);
        if(predicates.size()>0){
            cq.where(predicates.toArray(new Predicate[0]));
        }
        
        TypedQuery<CompanyLog> query = entityManager.createQuery(cq);
        
        int iTotal = query.getResultList().size();

        
        
        List<CompanyLog> result = query.setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        
        Page<CompanyLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<CompanyLog>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }

    public CompanyLog createCompanyLog(CompanyLog companyLog) throws BusinessLogicException {
        //validateCompanyLog(companyLog);
        //validateUnique(companyLog);
        return companyLogRepository.save(companyLog);
    }

    private void validateCompanyLog(CompanyLog companyLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(companyLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto CompanyLog");
        } else if (StringUtils.isEmpty(companyLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto CompanyLog");
        } 
    }
    
    private void validateUnique(CompanyLog companyLog) throws ExistentEntityException {
        List<CompanyLog> companyLoges = companyLogRepository.findByName(companyLog.getName());
        if (companyLoges!=null && !companyLoges.isEmpty()) {
            throw new ExistentEntityException(CompanyLog.class,"name="+companyLog.getName());
        } 
    }

    public CompanyLog updateCompanyLog(UUID companyLogId, CompanyLog companyLog) throws EntityNotExistentException {
        CompanyLog persistedCompanyLog = getById(companyLogId);
        if (persistedCompanyLog != null) {
            persistedCompanyLog.setName(companyLog.getName());
            return companyLogRepository.save(persistedCompanyLog);
        } else {
            throw new EntityNotExistentException(CompanyLog.class,companyLogId.toString());
        }
    }

    public void deleteCompanyLog(UUID companyLogId) throws EntityNotExistentException {
        CompanyLog companyLog = getById(companyLogId);
        companyLog.setDeleted(Boolean.TRUE);
        companyLog.setActive(Boolean.FALSE);
        companyLogRepository.save(companyLog);
    }

    public List<CompanyLog> findAll(){
        return companyLogRepository.findAll();
    }
    
    public CompanyLog getByName(String name){
        return companyLogRepository.getByName(name);
    }
    
    public List<CompanyLog> findByNameIgnoreCaseContaining(String name){
        return companyLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<CompanyLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return companyLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}
