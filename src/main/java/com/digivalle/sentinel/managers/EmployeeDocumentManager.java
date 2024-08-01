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
import com.digivalle.sentinel.models.EmployeeDocument;
import com.digivalle.sentinel.repositories.EmployeeDocumentRepository;
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
public class EmployeeDocumentManager {
    
    @Autowired
    private EmployeeDocumentRepository employeeDocumentRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public EmployeeDocument getById(UUID id) throws EntityNotExistentException {
        Optional<EmployeeDocument> employeeDocument = employeeDocumentRepository.findById(id);
        if (!employeeDocument.isEmpty()) {
            return employeeDocument.get();
        }
        throw new EntityNotExistentException(EmployeeDocument.class,id.toString());
    }
    
    public PagedResponse<EmployeeDocument> getEmployeeDocument(EmployeeDocument filter, Paging paging){
        
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<EmployeeDocument> cq = cb.createQuery(EmployeeDocument.class);
        Root<EmployeeDocument> root = cq.from(EmployeeDocument.class);
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
        if(filter.getSerial()!=null){
            predicates.add(cb.equal(root.get("serial"), filter.getSerial()));
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
        if(filter.getFileName()!=null){
            predicates.add(cb.like(cb.lower(root.get("fileName")), "%" + filter.getFileName().toLowerCase()+ "%"));
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
        
        TypedQuery<EmployeeDocument> query = entityManager.createQuery(cq);
        
        int iTotal = query.getResultList().size();

        
        
        List<EmployeeDocument> result = query.setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        
        Page<EmployeeDocument> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<EmployeeDocument>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }

    public EmployeeDocument createEmployeeDocument(EmployeeDocument employeeDocument) throws BusinessLogicException, ExistentEntityException {
        validateEmployeeDocument(employeeDocument);
        validateUnique(employeeDocument);
        return employeeDocumentRepository.save(employeeDocument);
    }

    private void validateEmployeeDocument(EmployeeDocument employeeDocument) throws BusinessLogicException {
        if (StringUtils.isEmpty(employeeDocument.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto EmployeeDocument");
        } else if (StringUtils.isEmpty(employeeDocument.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto EmployeeDocument");
        } else if(employeeDocument.getEmployee().getId()==null){
            throw new BusinessLogicException("El campo Employee es requerido para el objeto EmployeeDocument");
        } 
    }
    
    private void validateUnique(EmployeeDocument employeeDocument) throws ExistentEntityException {
        List<EmployeeDocument> employeeDocumentes = employeeDocumentRepository.findByNameAndEmployeeAndDeleted(employeeDocument.getName(), employeeDocument.getEmployee(), Boolean.FALSE);
        if (employeeDocumentes!=null && !employeeDocumentes.isEmpty()) {
            throw new ExistentEntityException(EmployeeDocument.class,"name="+employeeDocument.getName()+", employeeId="+employeeDocument.getEmployee().getId());
        } 
    }

    public EmployeeDocument updateEmployeeDocument(UUID employeeDocumentId, EmployeeDocument employeeDocument) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(employeeDocument.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto EmployeeDocument");
        } 
    
        EmployeeDocument persistedEmployeeDocument = getById(employeeDocumentId);
        if (persistedEmployeeDocument != null) {
            if(employeeDocument.getName()!=null){
                persistedEmployeeDocument.setName(employeeDocument.getName());
            }
            if(employeeDocument.getEmployee()!=null){
                persistedEmployeeDocument.setEmployee(employeeDocument.getEmployee());
            }
            if(employeeDocument.getDescription()!=null){
                persistedEmployeeDocument.setDescription(employeeDocument.getDescription());
            }
            if(employeeDocument.getFile()!=null){
                persistedEmployeeDocument.setFile(employeeDocument.getFile());
            }
            if(employeeDocument.getFileName()!=null){
                persistedEmployeeDocument.setFileName(employeeDocument.getFileName());
            }
            if(employeeDocument.getActive()!=null){
                persistedEmployeeDocument.setActive(employeeDocument.getActive());
            }
            persistedEmployeeDocument.setUpdateUser(employeeDocument.getUpdateUser());
            return employeeDocumentRepository.save(persistedEmployeeDocument);
        } else {
            throw new EntityNotExistentException(EmployeeDocument.class,employeeDocumentId.toString());
        }
    }

    public EmployeeDocument deleteEmployeeDocument(UUID employeeDocumentId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto EmployeeDocument");
        } 
        EmployeeDocument employeeDocument = getById(employeeDocumentId);
        employeeDocument.setDeleted(Boolean.TRUE);
        employeeDocument.setActive(Boolean.FALSE);
        return employeeDocumentRepository.save(employeeDocument);
    }

    public List<EmployeeDocument> findAll(){
        return employeeDocumentRepository.findAll();
    }
    
    public EmployeeDocument getByName(String name){
        return employeeDocumentRepository.getByName(name);
    }
    
    public List<EmployeeDocument> findByNameIgnoreCaseContaining(String name){
        return employeeDocumentRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<EmployeeDocument> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return employeeDocumentRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public EmployeeDocument getBySerial(Integer serial) {
        return employeeDocumentRepository.getBySerial(serial);
    }
}
