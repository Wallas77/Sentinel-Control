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
import com.digivalle.sentinel.models.RoleLog;
import com.digivalle.sentinel.repositories.RoleLogRepository;
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
public class RoleLogManager {
    
    @Autowired
    private RoleLogRepository roleLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public RoleLog getById(UUID id) throws EntityNotExistentException {
        Optional<RoleLog> roleLog = roleLogRepository.findById(id);
        if (!roleLog.isEmpty()) {
            return roleLog.get();
        }
        throw new EntityNotExistentException(RoleLog.class,id.toString());
    }
    
    public PagedResponse<RoleLog> getRoleLog(RoleLog filter, Paging paging){
        
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<RoleLog> cq = cb.createQuery(RoleLog.class);
        Root<RoleLog> root = cq.from(RoleLog.class);
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
        if(filter.getRoleId()!=null){
            predicates.add(cb.equal(root.get("roleId"), filter.getRoleId()));
        }
        if(filter.getTransactionId()!=null){
            predicates.add(cb.equal(root.get("transactionId"), filter.getTransactionId()));
        }
        if(filter.getAction()!=null){
            predicates.add(cb.equal(root.get("action"), filter.getAction()));
        }
        if(filter.getCustomer()!=null){
            if(filter.getCustomer().getId()!=null){
                predicates.add(cb.equal(root.get("customer").get("id"), filter.getCustomer().getId()));
            }
            if(filter.getCustomer().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("customer").get("name")), "%" + filter.getCustomer().getName().toLowerCase()+ "%"));
            }
        }
        
        cq.select(root);
        if(predicates.size()>0){
            cq.where(predicates.toArray(new Predicate[0]));
        }
        
        TypedQuery<RoleLog> query = entityManager.createQuery(cq);
        
        int iTotal = query.getResultList().size();

        
        
        List<RoleLog> result = query.setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        
        Page<RoleLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<RoleLog>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }

    public RoleLog createRoleLog(RoleLog roleLog) throws BusinessLogicException {
        //validateRoleLog(roleLog);
        //validateUnique(roleLog);
        return roleLogRepository.save(roleLog);
    }

    private void validateRoleLog(RoleLog roleLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(roleLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto RoleLog");
        } else if (StringUtils.isEmpty(roleLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto RoleLog");
        } 
    }
    
    private void validateUnique(RoleLog roleLog) throws ExistentEntityException {
        List<RoleLog> roleLoges = roleLogRepository.findByName(roleLog.getName());
        if (roleLoges!=null && !roleLoges.isEmpty()) {
            throw new ExistentEntityException(RoleLog.class,"name="+roleLog.getName());
        } 
    }

    public RoleLog updateRoleLog(UUID roleLogId, RoleLog roleLog) throws EntityNotExistentException {
        RoleLog persistedRoleLog = getById(roleLogId);
        if (persistedRoleLog != null) {
            persistedRoleLog.setName(roleLog.getName());
            return roleLogRepository.save(persistedRoleLog);
        } else {
            throw new EntityNotExistentException(RoleLog.class,roleLogId.toString());
        }
    }

    public void deleteRoleLog(UUID roleLogId) throws EntityNotExistentException {
        RoleLog roleLog = getById(roleLogId);
        roleLog.setDeleted(Boolean.TRUE);
        roleLog.setActive(Boolean.FALSE);
        roleLogRepository.save(roleLog);
    }

    public List<RoleLog> findAll(){
        return roleLogRepository.findAll();
    }
    
    public RoleLog getByName(String name){
        return roleLogRepository.getByName(name);
    }
    
    public List<RoleLog> findByNameIgnoreCaseContaining(String name){
        return roleLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<RoleLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return roleLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}