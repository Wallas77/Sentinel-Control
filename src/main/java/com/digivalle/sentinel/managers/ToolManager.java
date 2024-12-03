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
import com.digivalle.sentinel.models.Tool;
import com.digivalle.sentinel.repositories.ToolRepository;
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
public class ToolManager {
    
    @Autowired
    private ToolRepository toolRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public Tool getById(UUID id) throws EntityNotExistentException {
        Optional<Tool> tool = toolRepository.findById(id);
        if (!tool.isEmpty()) {
            return tool.get();
        }
        throw new EntityNotExistentException(Tool.class,id.toString());
    }
    
    public PagedResponse<Tool> getTool(Tool filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tool> cq = cb.createQuery(Tool.class);
        Root<Tool> root = cq.from(Tool.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<Tool> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<Tool> result = query.getResultList();
        
        Page<Tool> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(Tool filter, CriteriaBuilder cb, Root<Tool> root) {
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
        if(filter.getCostAmount()!=null && filter.getCostAmount2()!=null){
            predicates.add(cb.between(root.get("costAmount"), filter.getCostAmount(),filter.getCostAmount2()));
        }
        if(filter.getSerial()!=null){
            predicates.add(cb.equal(root.get("serial"), filter.getSerial()));
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

        return predicates;
    }

    private void applySorting(CriteriaQuery<Tool> cq, CriteriaBuilder cb, Root<Tool> root, Tool filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, Tool filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Tool> countRoot = countQuery.from(Tool.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public Tool createTool(Tool tool) throws BusinessLogicException, ExistentEntityException {
        validateTool(tool);
        validateUnique(tool);
        return toolRepository.save(tool);
    }

    private void validateTool(Tool tool) throws BusinessLogicException {
        if (StringUtils.isEmpty(tool.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto Tool");
        } else if (StringUtils.isEmpty(tool.getDescription())) {
            throw new BusinessLogicException("El campo Description es requerido para el objeto Tool");
        } else if (StringUtils.isEmpty(tool.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Tool");
        } else if (tool.getToolType()==null) {
            throw new BusinessLogicException("El campo ToolType es requerido para el objeto Tool");
        } 
        if(tool.getEmployee()!=null && tool.getEmployee().getId()==null){
            tool.setEmployee(null);
        }
    }
    
    private void validateUnique(Tool tool) throws ExistentEntityException {
        List<Tool> tools = toolRepository.findByIdNumberAndToolType(tool.getIdNumber(),tool.getToolType());
        if (tools!=null && !tools.isEmpty()) {
            if(tool.getId()==null){
                throw new ExistentEntityException(Tool.class,"idNumber="+tool.getIdNumber()+", toolType= "+tool.getToolType().getName());
            } else {
                for(Tool toolPersisted: tools){
                    if(!toolPersisted.getId().equals(tool.getId())){
                        throw new ExistentEntityException(Tool.class,"idNumber="+tool.getIdNumber()+", toolType= "+tool.getToolType().getName());
                    }
                }
            }
        } 
    }

    public Tool updateTool(UUID toolId, Tool tool) throws EntityNotExistentException, BusinessLogicException, ExistentEntityException {
        if (StringUtils.isEmpty(tool.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Tool");
        } 
        validateUnique(tool);
        Tool persistedTool = getById(toolId);
        if (persistedTool != null) {
            if(tool.getName()!=null){
                persistedTool.setName(tool.getName());
            }
            if(tool.getDescription()!=null){
                persistedTool.setDescription(tool.getDescription());
            }
            if(tool.getIdNumber()!=null){
                persistedTool.setIdNumber(tool.getIdNumber());
            }
            if(tool.getToolType()!=null){
                persistedTool.setToolType(tool.getToolType());
            }
            if(tool.getEmployee()!=null && tool.getEmployee().getId()!=null){
                persistedTool.setEmployee(tool.getEmployee());
            } else if (tool.getEmployee()!=null && tool.getEmployee().getId()==null){
                persistedTool.setEmployee(null);
            }
            if(tool.getActive()!=null){
                persistedTool.setActive(tool.getActive());
            }
            if(tool.getCostAmount()!=null){
                persistedTool.setCostAmount(tool.getCostAmount());
            }
            persistedTool.setUpdateUser(tool.getUpdateUser());
            return toolRepository.save(persistedTool);
        } else {
            throw new EntityNotExistentException(Tool.class,toolId.toString());
        }
    }

    public Tool deleteTool(UUID toolId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto Tool");
        } 
        Tool tool = getById(toolId);
        tool.setDeleted(Boolean.TRUE);
        tool.setActive(Boolean.FALSE);
        return toolRepository.save(tool);
    }

    public List<Tool> findAll(){
        return toolRepository.findAll();
    }
    
    public Tool getByName(String name){
        return toolRepository.getByName(name);
    }
    
    public List<Tool> findByNameIgnoreCaseContaining(String name){
        return toolRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<Tool> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return toolRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public Tool getBySerial(Integer serial) {
        return toolRepository.getBySerial(serial);
    }
}
