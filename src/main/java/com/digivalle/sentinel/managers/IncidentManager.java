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
import com.digivalle.sentinel.models.Incident;
import com.digivalle.sentinel.repositories.IncidentRepository;
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
public class IncidentManager {
    
    @Autowired
    private IncidentRepository incidentRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public Incident getById(UUID id) throws EntityNotExistentException {
        Optional<Incident> incident = incidentRepository.findById(id);
        if (!incident.isEmpty()) {
            return incident.get();
        }
        throw new EntityNotExistentException(Incident.class,id.toString());
    }
    
    public PagedResponse<Incident> getIncident(Incident filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Incident> cq = cb.createQuery(Incident.class);
        Root<Incident> root = cq.from(Incident.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<Incident> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<Incident> result = query.getResultList();
        
        Page<Incident> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(Incident filter, CriteriaBuilder cb, Root<Incident> root) {
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
        if(filter.getEmployee()!=null){
            if(filter.getEmployee().getId()!=null){
                predicates.add(cb.equal(root.get("employee").get("id"), filter.getEmployee().getId()));
            }
            if(filter.getEmployee().getCode()!=null){
                predicates.add(cb.like(cb.lower(root.get("employee").get("code")), "%" + filter.getEmployee().getCode().toLowerCase()+ "%"));
            }
            if(filter.getEmployee().getFirstSurname()!=null){
                predicates.add(cb.like(cb.lower(root.get("employee").get("name")), "%" + filter.getEmployee().getName().toLowerCase()+ "%"));
            }
            if(filter.getEmployee().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("employee").get("firstSurname")), "%" + filter.getEmployee().getFirstSurname().toLowerCase()+ "%"));
            }
        }
        if(filter.getIncidentType()!=null){
            if(filter.getIncidentType().getId()!=null){
                predicates.add(cb.equal(root.get("incidentType").get("id"), filter.getIncidentType().getId()));
            }
            if(filter.getIncidentType().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("incidentType").get("name")), "%" + filter.getIncidentType().getName().toLowerCase()+ "%"));
            }
        }
        if(filter.getService()!=null){
            if(filter.getService().getId()!=null){
                predicates.add(cb.equal(root.get("service").get("id"), filter.getService().getId()));
            }
            if(filter.getService().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("service").get("name")), "%" + filter.getService().getName().toLowerCase()+ "%"));
            }
        }
        if(filter.getNotes()!=null){
            predicates.add(cb.like(cb.lower(root.get("notes")), "%" + filter.getNotes().toLowerCase()+ "%"));
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

    private void applySorting(CriteriaQuery<Incident> cq, CriteriaBuilder cb, Root<Incident> root, Incident filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, Incident filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Incident> countRoot = countQuery.from(Incident.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public Incident createIncident(Incident incident) throws BusinessLogicException, ExistentEntityException {
        validateIncident(incident);
        //validateUnique(incident);
        return incidentRepository.save(incident);
    }

    private void validateIncident(Incident incident) throws BusinessLogicException {
        if (incident.getEmployee()==null) {
            throw new BusinessLogicException("El campo Employee es requerido para el objeto Incident");
        } else if (incident.getIncidentType()==null) {
            throw new BusinessLogicException("El campo IncidentType es requerido para el objeto Incident");
        } else if (incident.getService()==null) {
            throw new BusinessLogicException("El campo Service es requerido para el objeto Incident");
        } 
    }
    
    private void validateUnique(Incident incident) throws ExistentEntityException {
        List<Incident> incidentes = incidentRepository.findByServiceAndIncidentTypeAndEmployeeAndDeleted(incident.getService(), incident.getIncidentType(),incident.getEmployee(),Boolean.FALSE);
        if (incidentes!=null && !incidentes.isEmpty()) {
            throw new ExistentEntityException(Incident.class,"Service="+incident.getService().getName()+", IncidentType="+incident.getIncidentType().getName()+", Employee="+incident.getEmployee().getCode());
        } 
    }

    public Incident updateIncident(UUID incidentId, Incident incident) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(incident.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Incident");
        } 
    
        Incident persistedIncident = getById(incidentId);
        if (persistedIncident != null) {
            if(incident.getEmployee()!=null){
                persistedIncident.setEmployee(incident.getEmployee());
            }
            if(incident.getIncidentType()!=null){
                persistedIncident.setIncidentType(incident.getIncidentType());
            }
            if(incident.getNotes()!=null){
                persistedIncident.setNotes(incident.getNotes());
            }
            if(incident.getService()!=null){
                persistedIncident.setService(incident.getService());
            }
            if(incident.getActive()!=null){
                persistedIncident.setActive(incident.getActive());
            }
            persistedIncident.setUpdateUser(incident.getUpdateUser());
            return incidentRepository.save(persistedIncident);
        } else {
            throw new EntityNotExistentException(Incident.class,incidentId.toString());
        }
    }

    public Incident deleteIncident(UUID incidentId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto Incident");
        } 
        Incident incident = getById(incidentId);
        incident.setDeleted(Boolean.TRUE);
        incident.setActive(Boolean.FALSE);
        return incidentRepository.save(incident);
    }

    public List<Incident> findAll(){
        return incidentRepository.findAll();
    }
    
    public Incident getByNotes(String notes){
        return incidentRepository.getByNotes(notes);
    }
    
    public List<Incident> findByNotesIgnoreCaseContaining(String notes){
        return incidentRepository.findByNotesIgnoreCaseContaining(notes);
    }
    
    public List<Incident> findByNotesIgnoreCaseContainingAndDeleted(String notes,Boolean deleted){
        return incidentRepository.findByNotesIgnoreCaseContainingAndDeleted(notes,deleted);
    }
    
    public Incident getBySerial(Integer serial) {
        return incidentRepository.getBySerial(serial);
    }
}
