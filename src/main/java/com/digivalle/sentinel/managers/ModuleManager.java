/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.managers;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.models.Module;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.repositories.ModuleRepository;
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
public class ModuleManager {
    
    @Autowired
    private ModuleRepository moduleRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public Module getById(UUID id) throws EntityNotExistentException {
        Optional<Module> module = moduleRepository.findById(id);
        if (!module.isEmpty()) {
            return module.get();
        }
        throw new EntityNotExistentException(Module.class,id.toString());
    }
    
    public PagedResponse<Module> getModule(Module filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Module> cq = cb.createQuery(Module.class);
        Root<Module> root = cq.from(Module.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<Module> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<Module> result = query.getResultList();
        
        Page<Module> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(Module filter, CriteriaBuilder cb, Root<Module> root) {
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
        if(filter.getActive()!=null){
            predicates.add(cb.equal(root.get("active"), filter.getActive()));
        }
        if(filter.getDeleted()!=null){
            predicates.add(cb.equal(root.get("deleted"), filter.getDeleted()));
        }
        if(filter.getUpdateUser()!=null){
            predicates.add(cb.equal(root.get("updateUser"), filter.getUpdateUser()));
        }
        if(filter.getApplication()!=null && filter.getApplication().getId()!=null){
            predicates.add(cb.equal(root.get("application").get("id"), filter.getApplication().getId()));
        }
        if(filter.getApplication()!=null && filter.getApplication().getName()!=null){
            predicates.add(cb.like(cb.lower(root.get("application").get("name")), "%" + filter.getApplication().getName().toLowerCase()+ "%"));
        }
        if(filter.getApplication()!=null && filter.getApplication().getDescription()!=null){
            predicates.add(cb.like(cb.lower(root.get("application").get("description")), "%" + filter.getApplication().getDescription().toLowerCase()+ "%"));
        }
        if(filter.getApplication()!=null && filter.getApplication().getActive()!=null){
            predicates.add(cb.equal(root.get("application").get("active"), filter.getApplication().getActive()));
        }
        return predicates;
    }

    private void applySorting(CriteriaQuery<Module> cq, CriteriaBuilder cb, Root<Module> root, Module filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, Module filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Module> countRoot = countQuery.from(Module.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    public Module createModule(Module module) throws BusinessLogicException, ExistentEntityException {
        validateModule(module);
        validateUnique(module);
        return moduleRepository.save(module);
    }

    private void validateModule(Module module) throws BusinessLogicException {
        if (StringUtils.isEmpty(module.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto Module");
        } else if (StringUtils.isEmpty(module.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Module");
        } 
    }
    
    private void validateUnique(Module module) throws ExistentEntityException {
        List<Module> modulees = moduleRepository.getByName(module.getName());
        if (modulees!=null && !modulees.isEmpty()) {
            throw new ExistentEntityException(Module.class,"name="+module.getName());
        } 
    }

    public Module updateModule(UUID moduleId, Module module) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(module.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Module");
        } 
        Module persistedModule = getById(moduleId);
        if (persistedModule != null) {
            if(module.getName()!=null){
                persistedModule.setName(module.getName());
            }
            if(module.getActive()!=null){
                persistedModule.setActive(module.getActive());
            }
            if(module.getApplication()!=null){
                persistedModule.setApplication(module.getApplication());
            }
            persistedModule.setUpdateUser(module.getUpdateUser());
            return moduleRepository.save(persistedModule);
        } else {
            throw new EntityNotExistentException(Module.class,moduleId.toString());
        }
    }

    public Module deleteModule(UUID moduleId) throws EntityNotExistentException {
        Module module = getById(moduleId);
        module.setDeleted(Boolean.TRUE);
        module.setActive(Boolean.FALSE);
        return moduleRepository.save(module);
    }

    public List<Module> findAll(){
        return moduleRepository.findAll();
    }
    
    public List<Module> getByName(String name){
        return moduleRepository.getByName(name);
    }
    
    public List<Module> findByNameIgnoreCaseContaining(String name){
        return moduleRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<Module> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return moduleRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public Module getBySerial(Integer serial) {
        return moduleRepository.getBySerial(serial);
    }
}
