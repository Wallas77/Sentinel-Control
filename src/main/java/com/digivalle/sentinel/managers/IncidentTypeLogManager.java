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
import com.digivalle.sentinel.models.IncidentTypeLog;
import com.digivalle.sentinel.repositories.IncidentTypeLogRepository;
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
public class IncidentTypeLogManager {
    
    @Autowired
    private IncidentTypeLogRepository incidentTypeLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public IncidentTypeLog getById(UUID id) throws EntityNotExistentException {
        Optional<IncidentTypeLog> incidentTypeLog = incidentTypeLogRepository.findById(id);
        if (!incidentTypeLog.isEmpty()) {
            return incidentTypeLog.get();
        }
        throw new EntityNotExistentException(IncidentTypeLog.class,id.toString());
    }
    
    public PagedResponse<IncidentTypeLog> getIncidentTypeLog(IncidentTypeLog filter, Paging paging){
        
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<IncidentTypeLog> cq = cb.createQuery(IncidentTypeLog.class);
        Root<IncidentTypeLog> root = cq.from(IncidentTypeLog.class);
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
        if(filter.getDescription()!=null){
            predicates.add(cb.like(cb.lower(root.get("description")), "%" + filter.getDescription().toLowerCase()+ "%"));
        }
        if(filter.getCustomer()!=null){
            if(filter.getCustomer().getId()!=null){
                predicates.add(cb.equal(root.get("customer").get("id"), filter.getCustomer().getId()));
            }
            if(filter.getCustomer().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("customer").get("name")), "%" + filter.getCustomer().getName().toLowerCase()+ "%"));
            }
        }
        if(filter.getBranch()!=null){
            if(filter.getBranch().getId()!=null){
                predicates.add(cb.equal(root.get("branch").get("id"), filter.getBranch().getId()));
            }
            if(filter.getBranch().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("branch").get("name")), "%" + filter.getBranch().getName().toLowerCase()+ "%"));
            }
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
        
        TypedQuery<IncidentTypeLog> query = entityManager.createQuery(cq);
        
        int iTotal = query.getResultList().size();

        
        
        List<IncidentTypeLog> result = query.setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        
        Page<IncidentTypeLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<IncidentTypeLog>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }

    public IncidentTypeLog createIncidentTypeLog(IncidentTypeLog incidentTypeLog) throws BusinessLogicException {
        //validateIncidentTypeLog(incidentTypeLog);
        //validateUnique(incidentTypeLog);
        return incidentTypeLogRepository.save(incidentTypeLog);
    }

    private void validateIncidentTypeLog(IncidentTypeLog incidentTypeLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(incidentTypeLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto IncidentTypeLog");
        } else if (StringUtils.isEmpty(incidentTypeLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto IncidentTypeLog");
        } 
    }
    
    private void validateUnique(IncidentTypeLog incidentTypeLog) throws ExistentEntityException {
        List<IncidentTypeLog> incidentTypeLoges = incidentTypeLogRepository.findByName(incidentTypeLog.getName());
        if (incidentTypeLoges!=null && !incidentTypeLoges.isEmpty()) {
            throw new ExistentEntityException(IncidentTypeLog.class,"name="+incidentTypeLog.getName());
        } 
    }

    public IncidentTypeLog updateIncidentTypeLog(UUID incidentTypeLogId, IncidentTypeLog incidentTypeLog) throws EntityNotExistentException {
        IncidentTypeLog persistedIncidentTypeLog = getById(incidentTypeLogId);
        if (persistedIncidentTypeLog != null) {
            persistedIncidentTypeLog.setName(incidentTypeLog.getName());
            return incidentTypeLogRepository.save(persistedIncidentTypeLog);
        } else {
            throw new EntityNotExistentException(IncidentTypeLog.class,incidentTypeLogId.toString());
        }
    }

    public void deleteIncidentTypeLog(UUID incidentTypeLogId) throws EntityNotExistentException {
        IncidentTypeLog incidentTypeLog = getById(incidentTypeLogId);
        incidentTypeLog.setDeleted(Boolean.TRUE);
        incidentTypeLog.setActive(Boolean.FALSE);
        incidentTypeLogRepository.save(incidentTypeLog);
    }

    public List<IncidentTypeLog> findAll(){
        return incidentTypeLogRepository.findAll();
    }
    
    public IncidentTypeLog getByName(String name){
        return incidentTypeLogRepository.getByName(name);
    }
    
    public List<IncidentTypeLog> findByNameIgnoreCaseContaining(String name){
        return incidentTypeLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<IncidentTypeLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return incidentTypeLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}
