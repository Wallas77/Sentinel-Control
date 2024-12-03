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
import com.digivalle.sentinel.models.ToolLog;
import com.digivalle.sentinel.repositories.ToolLogRepository;
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
public class ToolLogManager {
    
    @Autowired
    private ToolLogRepository toolLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public ToolLog getById(UUID id) throws EntityNotExistentException {
        Optional<ToolLog> toolLog = toolLogRepository.findById(id);
        if (!toolLog.isEmpty()) {
            return toolLog.get();
        }
        throw new EntityNotExistentException(ToolLog.class,id.toString());
    }
    
    public PagedResponse<ToolLog> getToolLog(ToolLog filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ToolLog> cq = cb.createQuery(ToolLog.class);
        Root<ToolLog> root = cq.from(ToolLog.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<ToolLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<ToolLog> result = query.getResultList();
        
        Page<ToolLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(ToolLog filter, CriteriaBuilder cb, Root<ToolLog> root) {
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
        
        if(filter.getIdNumber()!=null){
            predicates.add(cb.like(cb.lower(root.get("idNumber")), "%" + filter.getIdNumber().toLowerCase()+ "%"));
        }
        if(filter.getName()!=null){
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase()+ "%"));
        }
        if(filter.getDescription()!=null){
            predicates.add(cb.like(cb.lower(root.get("description")), "%" + filter.getDescription().toLowerCase()+ "%"));
        }
        if(filter.getCostAmount()!=null && filter.getCostAmount2()!=null){
            predicates.add(cb.between(root.get("costAmount"), filter.getCostAmount(),filter.getCostAmount2()));
        }
        if(filter.getEmployee()!=null){
            if(filter.getEmployee().getId()!=null){
                predicates.add(cb.equal(root.get("employee").get("id"), filter.getEmployee().getId()));
            }
            if(filter.getEmployee().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("employee").get("name")), "%" + filter.getEmployee().getName().toLowerCase()+ "%"));
            }
            if(filter.getEmployee().getFirstSurname()!=null){
                predicates.add(cb.like(cb.lower(root.get("employee").get("firstSurname")), "%" + filter.getEmployee().getFirstSurname().toLowerCase()+ "%"));
            }
            if(filter.getEmployee().getSecondSurname()!=null){
                predicates.add(cb.like(cb.lower(root.get("employee").get("secondSurname")), "%" + filter.getEmployee().getSecondSurname().toLowerCase()+ "%"));
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
        if(filter.getToolType()!=null){
            if(filter.getToolType().getId()!=null){
                predicates.add(cb.equal(root.get("toolType").get("id"), filter.getToolType().getId()));
            }
            if(filter.getToolType().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("toolType").get("name")), "%" + filter.getToolType().getName().toLowerCase()+ "%"));
            }
        }
        if(filter.getToolId()!=null){
            predicates.add(cb.equal(root.get("toolId"), filter.getToolId()));
        }
        if(filter.getTransactionId()!=null){
            predicates.add(cb.equal(root.get("transactionId"), filter.getTransactionId()));
        }
        if(filter.getAction()!=null){
            predicates.add(cb.equal(root.get("action"), filter.getAction()));
        }

        return predicates;
    }

    private void applySorting(CriteriaQuery<ToolLog> cq, CriteriaBuilder cb, Root<ToolLog> root, ToolLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, ToolLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ToolLog> countRoot = countQuery.from(ToolLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public ToolLog createToolLog(ToolLog toolLog) throws BusinessLogicException {
        //validateToolLog(toolLog);
        //validateUnique(toolLog);
        return toolLogRepository.save(toolLog);
    }

    private void validateToolLog(ToolLog toolLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(toolLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto ToolLog");
        } else if (StringUtils.isEmpty(toolLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto ToolLog");
        } 
    }
    
    private void validateUnique(ToolLog toolLog) throws ExistentEntityException {
        List<ToolLog> toolLoges = toolLogRepository.findByName(toolLog.getName());
        if (toolLoges!=null && !toolLoges.isEmpty()) {
            throw new ExistentEntityException(ToolLog.class,"name="+toolLog.getName());
        } 
    }

    public ToolLog updateToolLog(UUID toolLogId, ToolLog toolLog) throws EntityNotExistentException {
        ToolLog persistedToolLog = getById(toolLogId);
        if (persistedToolLog != null) {
            persistedToolLog.setName(toolLog.getName());
            return toolLogRepository.save(persistedToolLog);
        } else {
            throw new EntityNotExistentException(ToolLog.class,toolLogId.toString());
        }
    }

    public void deleteToolLog(UUID toolLogId) throws EntityNotExistentException {
        ToolLog toolLog = getById(toolLogId);
        toolLog.setDeleted(Boolean.TRUE);
        toolLog.setActive(Boolean.FALSE);
        toolLogRepository.save(toolLog);
    }

    public List<ToolLog> findAll(){
        return toolLogRepository.findAll();
    }
    
    public ToolLog getByName(String name){
        return toolLogRepository.getByName(name);
    }
    
    public List<ToolLog> findByNameIgnoreCaseContaining(String name){
        return toolLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<ToolLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return toolLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}
