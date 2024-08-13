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
import com.digivalle.sentinel.models.Branch;
import com.digivalle.sentinel.repositories.BranchRepository;
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
public class BranchManager {
    
    @Autowired
    private BranchRepository branchRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public Branch getById(UUID id) throws EntityNotExistentException {
        Optional<Branch> branch = branchRepository.findById(id);
        if (!branch.isEmpty()) {
            return branch.get();
        }
        throw new EntityNotExistentException(Branch.class,id.toString());
    }
    
    public PagedResponse<Branch> getBranch(Branch filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Branch> cq = cb.createQuery(Branch.class);
        Root<Branch> root = cq.from(Branch.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<Branch> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<Branch> result = query.getResultList();
        
        Page<Branch> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(Branch filter, CriteriaBuilder cb, Root<Branch> root) {
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

    private void applySorting(CriteriaQuery<Branch> cq, CriteriaBuilder cb, Root<Branch> root, Branch filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, Branch filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Branch> countRoot = countQuery.from(Branch.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    

    public Branch createBranch(Branch branch) throws BusinessLogicException, ExistentEntityException {
        validateBranch(branch);
        validateUnique(branch);
        return branchRepository.save(branch);
    }

    private void validateBranch(Branch branch) throws BusinessLogicException {
        if (StringUtils.isEmpty(branch.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto Branch");
        } else if (branch.getCustomer()==null) {
            throw new BusinessLogicException("El campo Customer es requerido para el objeto Branch");
        } else if (StringUtils.isEmpty(branch.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Branch");
        } 
    }
    
    private void validateUnique(Branch branch) throws ExistentEntityException {
        List<Branch> branches = branchRepository.findByName(branch.getName());
        if (branches!=null && !branches.isEmpty()) {
            throw new ExistentEntityException(Branch.class,"name="+branch.getName());
        } 
    }

    public Branch updateBranch(UUID branchId, Branch branch) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(branch.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Branch");
        } 
    
        Branch persistedBranch = getById(branchId);
        if (persistedBranch != null) {
            if(branch.getCity()!=null){
                persistedBranch.setCity(branch.getCity());
            }
            if(branch.getCode()!=null){
                persistedBranch.setCode(branch.getCode());
            }
            if(branch.getColony()!=null){
                persistedBranch.setColony(branch.getColony());
            }
            if(branch.getCountry()!=null){
                persistedBranch.setCountry(branch.getCountry());
            }
            if(branch.getCustomer()!=null){
                persistedBranch.setCustomer(branch.getCustomer());
            }
            if(branch.getExternalNumber()!=null){
                persistedBranch.setExternalNumber(branch.getExternalNumber());
            }
            if(branch.getInternalNumber()!=null){
                persistedBranch.setInternalNumber(branch.getInternalNumber());
            }
            if(branch.getName()!=null){
                persistedBranch.setName(branch.getName());
            }
            if(branch.getState()!=null){
                persistedBranch.setState(branch.getState());
            }
            if(branch.getStreet()!=null){
                persistedBranch.setStreet(branch.getStreet());
            }
            if(branch.getSuburb()!=null){
                persistedBranch.setSuburb(branch.getSuburb());
            }
            if(branch.getZipCode()!=null){
                persistedBranch.setZipCode(branch.getZipCode());
            }
            if(branch.getActive()!=null){
                persistedBranch.setActive(branch.getActive());
            }
            persistedBranch.setUpdateUser(branch.getUpdateUser());
            return branchRepository.save(persistedBranch);
        } else {
            throw new EntityNotExistentException(Branch.class,branchId.toString());
        }
    }

    public Branch deleteBranch(UUID branchId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto Branch");
        } 
        Branch branch = getById(branchId);
        branch.setDeleted(Boolean.TRUE);
        branch.setActive(Boolean.FALSE);
        return branchRepository.save(branch);
    }

    public List<Branch> findAll(){
        return branchRepository.findAll();
    }
    
    public Branch getByName(String name){
        return branchRepository.getByName(name);
    }
    
    public List<Branch> findByNameIgnoreCaseContaining(String name){
        return branchRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<Branch> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return branchRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public Branch getBySerial(Integer serial) {
        return branchRepository.getBySerial(serial);
    }
}
