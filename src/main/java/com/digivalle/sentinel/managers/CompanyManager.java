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
import com.digivalle.sentinel.models.Company;
import com.digivalle.sentinel.repositories.CompanyRepository;
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
public class CompanyManager {
    
    @Autowired
    private CompanyRepository companyRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public Company getById(UUID id) throws EntityNotExistentException {
        Optional<Company> company = companyRepository.findById(id);
        if (!company.isEmpty()) {
            return company.get();
        }
        throw new EntityNotExistentException(Company.class,id.toString());
    }
    
    public PagedResponse<Company> getCompany(Company filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Company> cq = cb.createQuery(Company.class);
        Root<Company> root = cq.from(Company.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<Company> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<Company> result = query.getResultList();
        
        Page<Company> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(Company filter, CriteriaBuilder cb, Root<Company> root) {
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
        

        return predicates;
    }

    private void applySorting(CriteriaQuery<Company> cq, CriteriaBuilder cb, Root<Company> root, Company filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, Company filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Company> countRoot = countQuery.from(Company.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    

    public Company createCompany(Company company) throws BusinessLogicException, ExistentEntityException {
        validateCompany(company);
        validateUnique(company);
        return companyRepository.save(company);
    }

    private void validateCompany(Company company) throws BusinessLogicException {
        if (StringUtils.isEmpty(company.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto Company");
        } else if (StringUtils.isEmpty(company.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Company");
        } 
    }
    
    private void validateUnique(Company company) throws ExistentEntityException {
        List<Company> companyes = companyRepository.findByName(company.getName());
        if (companyes!=null && !companyes.isEmpty()) {
            throw new ExistentEntityException(Company.class,"name="+company.getName());
        } 
    }

    public Company updateCompany(UUID companyId, Company company) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(company.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Company");
        } 
    
        Company persistedCompany = getById(companyId);
        if (persistedCompany != null) {
            if(company.getCity()!=null){
                persistedCompany.setCity(company.getCity());
            }
            
            if(company.getColony()!=null){
                persistedCompany.setColony(company.getColony());
            }
            if(company.getCountry()!=null){
                persistedCompany.setCountry(company.getCountry());
            }
            if(company.getFiscalInfo()!=null){
                persistedCompany.setFiscalInfo(company.getFiscalInfo());
            }
            if(company.getExternalNumber()!=null){
                persistedCompany.setExternalNumber(company.getExternalNumber());
            }
            if(company.getInternalNumber()!=null){
                persistedCompany.setInternalNumber(company.getInternalNumber());
            }
            if(company.getName()!=null){
                persistedCompany.setName(company.getName());
            }
            if(company.getState()!=null){
                persistedCompany.setState(company.getState());
            }
            if(company.getStreet()!=null){
                persistedCompany.setStreet(company.getStreet());
            }
            if(company.getSuburb()!=null){
                persistedCompany.setSuburb(company.getSuburb());
            }
            if(company.getZipCode()!=null){
                persistedCompany.setZipCode(company.getZipCode());
            }
            if(company.getActive()!=null){
                persistedCompany.setActive(company.getActive());
            }
            persistedCompany.setUpdateUser(company.getUpdateUser());
            return companyRepository.save(persistedCompany);
        } else {
            throw new EntityNotExistentException(Company.class,companyId.toString());
        }
    }

    public Company deleteCompany(UUID companyId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto Company");
        } 
        Company company = getById(companyId);
        company.setDeleted(Boolean.TRUE);
        company.setActive(Boolean.FALSE);
        return companyRepository.save(company);
    }

    public List<Company> findAll(){
        return companyRepository.findAll();
    }
    
    public Company getByName(String name){
        return companyRepository.getByName(name);
    }
    
    public List<Company> findByNameIgnoreCaseContaining(String name){
        return companyRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<Company> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return companyRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public Company getBySerial(Integer serial) {
        return companyRepository.getBySerial(serial);
    }
}
