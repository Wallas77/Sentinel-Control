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
import com.digivalle.sentinel.models.Contact;
import com.digivalle.sentinel.repositories.ContactRepository;
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
public class ContactManager {
    
    @Autowired
    private ContactRepository contactRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public Contact getById(UUID id) throws EntityNotExistentException {
        Optional<Contact> contact = contactRepository.findById(id);
        if (!contact.isEmpty()) {
            return contact.get();
        }
        throw new EntityNotExistentException(Contact.class,id.toString());
    }
    
    public PagedResponse<Contact> getContact(Contact filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Contact> cq = cb.createQuery(Contact.class);
        Root<Contact> root = cq.from(Contact.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<Contact> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<Contact> result = query.getResultList();
        
        Page<Contact> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(Contact filter, CriteriaBuilder cb, Root<Contact> root) {
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
        

        return predicates;
    }

    private void applySorting(CriteriaQuery<Contact> cq, CriteriaBuilder cb, Root<Contact> root, Contact filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, Contact filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Contact> countRoot = countQuery.from(Contact.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    

    public Contact createContact(Contact contact) throws BusinessLogicException, ExistentEntityException {
        validateContact(contact);
        validateUnique(contact);
        return contactRepository.save(contact);
    }

    private void validateContact(Contact contact) throws BusinessLogicException {
        if (StringUtils.isEmpty(contact.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto Contact");
        } else if (StringUtils.isEmpty(contact.getFirstSurname())) {
            throw new BusinessLogicException("El campo FirstSurname es requerido para el objeto Contact");
        } else if (StringUtils.isEmpty(contact.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Contact");
        } 
    }
    
    private void validateUnique(Contact contact) throws ExistentEntityException {
        List<Contact> contactes = contactRepository.findByName(contact.getName());
        if (contactes!=null && !contactes.isEmpty()) {
            throw new ExistentEntityException(Contact.class,"name="+contact.getName());
        } 
    }

    public Contact updateContact(UUID contactId, Contact contact) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(contact.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Contact");
        } 
    
        Contact persistedContact = getById(contactId);
        if (persistedContact != null) {
            if(contact.getBirthday()!=null){
                persistedContact.setBirthday(contact.getBirthday());
            }
            if(contact.getCustomerDirectory()!=null){
                persistedContact.setCustomerDirectory(contact.getCustomerDirectory());
            }
            
            if(contact.getEmail()!=null){
                persistedContact.setEmail(contact.getEmail());
            }
            
            if(contact.getFirstSurname()!=null){
                persistedContact.setFirstSurname(contact.getFirstSurname());
            }
            
            if(contact.getHomePhone()!=null){
                persistedContact.setHomePhone(contact.getHomePhone());
            }
            
            if(contact.getMobilePhone()!=null){
                persistedContact.setMobilePhone(contact.getMobilePhone());
            }
            if(contact.getName()!=null){
                persistedContact.setName(contact.getName());
            }
            if(contact.getNationality()!=null){
                persistedContact.setNationality(contact.getNationality());
            }
            if(contact.getPhoto()!=null){
                persistedContact.setPhoto(contact.getPhoto());
            }
            if(contact.getPhotoFormat()!=null){
                persistedContact.setPhotoFormat(contact.getPhotoFormat());
            }
            if(contact.getSecondSurname()!=null){
                persistedContact.setSecondSurname(contact.getSecondSurname());
            }
            
            if(contact.getActive()!=null){
                persistedContact.setActive(contact.getActive());
            }
            
            persistedContact.setUpdateUser(contact.getUpdateUser());
            return contactRepository.save(persistedContact);
        } else {
            throw new EntityNotExistentException(Contact.class,contactId.toString());
        }
    }

    public Contact deleteContact(UUID contactId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto Contact");
        } 
        Contact contact = getById(contactId);
        contact.setDeleted(Boolean.TRUE);
        contact.setActive(Boolean.FALSE);
        return contactRepository.save(contact);
    }

    public List<Contact> findAll(){
        return contactRepository.findAll();
    }
    
    public Contact getByName(String name){
        return contactRepository.getByName(name);
    }
    
    public List<Contact> findByNameIgnoreCaseContaining(String name){
        return contactRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<Contact> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return contactRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public Contact getBySerial(Integer serial) {
        return contactRepository.getBySerial(serial);
    }
}
