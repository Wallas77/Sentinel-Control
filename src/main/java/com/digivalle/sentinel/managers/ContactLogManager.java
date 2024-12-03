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
import com.digivalle.sentinel.models.ContactLog;
import com.digivalle.sentinel.repositories.ContactLogRepository;
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
public class ContactLogManager {
    
    @Autowired
    private ContactLogRepository contactLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public ContactLog getById(UUID id) throws EntityNotExistentException {
        Optional<ContactLog> contactLog = contactLogRepository.findById(id);
        if (!contactLog.isEmpty()) {
            return contactLog.get();
        }
        throw new EntityNotExistentException(ContactLog.class,id.toString());
    }
    
    public PagedResponse<ContactLog> getContactLog(ContactLog filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ContactLog> cq = cb.createQuery(ContactLog.class);
        Root<ContactLog> root = cq.from(ContactLog.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<ContactLog> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<ContactLog> result = query.getResultList();
        
        Page<ContactLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(ContactLog filter, CriteriaBuilder cb, Root<ContactLog> root) {
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
        if(filter.getContactType()!=null){
            predicates.add(cb.equal(root.get("contactType"), filter.getContactType()));
        }
        
        if(filter.getBirthday()!=null){
            predicates.add(cb.equal(root.get("birthday"), filter.getBirthday()));
        }
        
        if(filter.getCustomerDirectory()!=null){
            if(filter.getCustomerDirectory().getId()!=null){
                predicates.add(cb.equal(root.get("customerDirectory").get("id"), filter.getCustomerDirectory().getId()));
            }
            if(filter.getCustomerDirectory().getDescription()!=null){
                predicates.add(cb.like(cb.lower(root.get("customerDirectory").get("description")), "%" + filter.getCustomerDirectory().getDescription().toLowerCase()+ "%"));
            }
            if(filter.getCustomerDirectory().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("customerDirectory").get("name")), "%" + filter.getCustomerDirectory().getName().toLowerCase()+ "%"));
            }
        }
        
        
        if(filter.getEmail()!=null){
            predicates.add(cb.like(cb.lower(root.get("email")), "%" + filter.getEmail().toLowerCase()+ "%"));
        }
        
        if(filter.getFirstSurname()!=null){
            predicates.add(cb.like(cb.lower(root.get("firstSurname")), "%" + filter.getFirstSurname().toLowerCase()+ "%"));
        }
        
        if(filter.getHomePhone()!=null){
            predicates.add(cb.like(cb.lower(root.get("homePhone")), "%" + filter.getHomePhone().toLowerCase()+ "%"));
        }
        
        if(filter.getMobilePhone()!=null){
            predicates.add(cb.like(cb.lower(root.get("mobilePhone")), "%" + filter.getMobilePhone().toLowerCase()+ "%"));
        }
        if(filter.getName()!=null){
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase()+ "%"));
        }
        if(filter.getNationality()!=null){
            predicates.add(cb.like(cb.lower(root.get("nationality")), "%" + filter.getNationality().toLowerCase()+ "%"));
        }
        
        if(filter.getSecondSurname()!=null){
            predicates.add(cb.like(cb.lower(root.get("secondSurname")), "%" + filter.getSecondSurname().toLowerCase()+ "%"));
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
        if(filter.getContactId()!=null){
            predicates.add(cb.equal(root.get("contactId"), filter.getContactId()));
        }
        if(filter.getTransactionId()!=null){
            predicates.add(cb.equal(root.get("transactionId"), filter.getTransactionId()));
        }
        if(filter.getAction()!=null){
            predicates.add(cb.equal(root.get("action"), filter.getAction()));
        }

        return predicates;
    }

    private void applySorting(CriteriaQuery<ContactLog> cq, CriteriaBuilder cb, Root<ContactLog> root, ContactLog filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, ContactLog filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ContactLog> countRoot = countQuery.from(ContactLog.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    

    public ContactLog createContactLog(ContactLog contactLog) throws BusinessLogicException {
        //validateContactLog(contactLog);
        //validateUnique(contactLog);
        return contactLogRepository.save(contactLog);
    }

    private void validateContactLog(ContactLog contactLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(contactLog.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto ContactLog");
        } else if (StringUtils.isEmpty(contactLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto ContactLog");
        } 
    }
    
    private void validateUnique(ContactLog contactLog) throws ExistentEntityException {
        List<ContactLog> contactLoges = contactLogRepository.findByName(contactLog.getName());
        if (contactLoges!=null && !contactLoges.isEmpty()) {
            throw new ExistentEntityException(ContactLog.class,"name="+contactLog.getName());
        } 
    }

    public ContactLog updateContactLog(UUID contactLogId, ContactLog contactLog) throws EntityNotExistentException {
        ContactLog persistedContactLog = getById(contactLogId);
        if (persistedContactLog != null) {
            persistedContactLog.setName(contactLog.getName());
            return contactLogRepository.save(persistedContactLog);
        } else {
            throw new EntityNotExistentException(ContactLog.class,contactLogId.toString());
        }
    }

    public void deleteContactLog(UUID contactLogId) throws EntityNotExistentException {
        ContactLog contactLog = getById(contactLogId);
        contactLog.setDeleted(Boolean.TRUE);
        contactLog.setActive(Boolean.FALSE);
        contactLogRepository.save(contactLog);
    }

    public List<ContactLog> findAll(){
        return contactLogRepository.findAll();
    }
    
    public ContactLog getByName(String name){
        return contactLogRepository.getByName(name);
    }
    
    public List<ContactLog> findByNameIgnoreCaseContaining(String name){
        return contactLogRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<ContactLog> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return contactLogRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
}
