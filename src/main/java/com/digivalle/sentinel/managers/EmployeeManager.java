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
import com.digivalle.sentinel.models.Employee;
import com.digivalle.sentinel.repositories.EmployeeRepository;
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
public class EmployeeManager {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public Employee getById(UUID id) throws EntityNotExistentException {
        Optional<Employee> employee = employeeRepository.findById(id);
        if (!employee.isEmpty()) {
            return employee.get();
        }
        throw new EntityNotExistentException(Employee.class,id.toString());
    }
    
    public PagedResponse<Employee> getEmployee(Employee filter, Paging paging){
        
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<Employee> cq = cb.createQuery(Employee.class);
        Root<Employee> root = cq.from(Employee.class);
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
        
        if(filter.getBirthday()!=null){
            predicates.add(cb.equal(root.get("birthday"), filter.getBirthday()));
        }
        if(filter.getBloodType()!=null){
            predicates.add(cb.equal(root.get("bloodType"), filter.getBloodType()));
        }
        if(filter.getBodyComplexion()!=null){
            predicates.add(cb.equal(root.get("bodyComplexion"), filter.getBodyComplexion()));
        }
        if(filter.getCode()!=null){
            predicates.add(cb.like(cb.lower(root.get("code")), "%" + filter.getCode().toLowerCase()+ "%"));
        }
        if(filter.getCity()!=null){
            predicates.add(cb.like(cb.lower(root.get("city")), "%" + filter.getCity().toLowerCase()+ "%"));
        }
        if(filter.getColony()!=null){
            predicates.add(cb.like(cb.lower(root.get("colony")), "%" + filter.getColony().toLowerCase()+ "%"));
        }
        if(filter.getCountry()!=null){
            if(filter.getCountry().getId()!=null){
                predicates.add(cb.equal(root.get("country").get("id"), filter.getCountry().getId()));
            }
            if(filter.getCountry().getCode()!=null){
                predicates.add(cb.like(cb.lower(root.get("country").get("code")), "%" + filter.getCountry().getCode().toLowerCase()+ "%"));
            }
            if(filter.getCountry().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("country").get("name")), "%" + filter.getCountry().getName().toLowerCase()+ "%"));
            }
        }
        
        
        if(filter.getEmail()!=null){
            predicates.add(cb.like(cb.lower(root.get("email")), "%" + filter.getEmail().toLowerCase()+ "%"));
        }
        if(filter.getExternalNumber()!=null){
            predicates.add(cb.like(cb.lower(root.get("externalNumber")), "%" + filter.getExternalNumber().toLowerCase()+ "%"));
        }
        if(filter.getEyesColor()!=null){
            predicates.add(cb.equal(root.get("eyesColor"), filter.getEyesColor()));
        }
        if(filter.getFirstSurname()!=null){
            predicates.add(cb.like(cb.lower(root.get("firstSurname")), "%" + filter.getFirstSurname().toLowerCase()+ "%"));
        }
        if(filter.getHairColor()!=null){
            predicates.add(cb.equal(root.get("hairColor"), filter.getHairColor()));
        }
        if(filter.getHeight()!=null && filter.getHeight2()!=null){
            predicates.add(cb.between(root.get("height"), filter.getHeight(),filter.getHeight2()));
            cq.orderBy(cb.desc(root.get("height")));
        }
        if(filter.getHomePhone()!=null){
            predicates.add(cb.like(cb.lower(root.get("homePhone")), "%" + filter.getHomePhone().toLowerCase()+ "%"));
        }
        if(filter.getInternalNumber()!=null){
            predicates.add(cb.like(cb.lower(root.get("internalNumber")), "%" + filter.getInternalNumber().toLowerCase()+ "%"));
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
        if(filter.getSalaryAmount()!=null && filter.getSalaryAmount2()!=null){
            predicates.add(cb.between(root.get("salaryAmount"), filter.getSalaryAmount(),filter.getSalaryAmount2()));
            cq.orderBy(cb.desc(root.get("salaryAmount")));
        }
        if(filter.getEmergencyContactName()!=null){
            predicates.add(cb.like(cb.lower(root.get("emergencyContactName")), "%" + filter.getEmergencyContactName().toLowerCase()+ "%"));
        }
        if(filter.getEmergencyContactPhone()!=null){
            predicates.add(cb.like(cb.lower(root.get("emergencyContactPhone")), "%" + filter.getEmergencyContactPhone().toLowerCase()+ "%"));
        }
        if(filter.getSecondSurname()!=null){
            predicates.add(cb.like(cb.lower(root.get("secondSurname")), "%" + filter.getSecondSurname().toLowerCase()+ "%"));
        }
        if(filter.getSkinColor()!=null){
            predicates.add(cb.equal(root.get("skinColor"), filter.getSkinColor()));
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
        if(filter.getWeight()!=null && filter.getWeight2()!=null){
            predicates.add(cb.between(root.get("weight"), filter.getWeight(),filter.getWeight2()));
            cq.orderBy(cb.desc(root.get("weight")));
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
        if(filter.getStartContractDate()!=null && filter.getStartContractDate2()!=null){
            predicates.add(cb.between(root.get("startContractDate"), filter.getStartContractDate(),filter.getStartContractDate2()));
            cq.orderBy(cb.desc(root.get("startContractDate")));
        }
        if(filter.getEndContractDate()!=null && filter.getEndContractDate2()!=null){
            predicates.add(cb.between(root.get("endContractDate"), filter.getEndContractDate(),filter.getEndContractDate2()));
            cq.orderBy(cb.desc(root.get("endContractDate")));
        }
        
        cq.select(root);
        if(predicates.size()>0){
            cq.where(predicates.toArray(new Predicate[0]));
        }
        
        TypedQuery<Employee> query = entityManager.createQuery(cq);
        
        int iTotal = query.getResultList().size();

        
        
        List<Employee> result = query.setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        
        Page<Employee> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<Employee>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }

    public Employee createEmployee(Employee employee) throws BusinessLogicException, ExistentEntityException {
        validateEmployee(employee);
        validateUnique(employee);
        return employeeRepository.save(employee);
    }

    private void validateEmployee(Employee employee) throws BusinessLogicException {
        if (StringUtils.isEmpty(employee.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto Employee");
        } else if (StringUtils.isEmpty(employee.getFirstSurname())) {
            throw new BusinessLogicException("El campo FirstSurname es requerido para el objeto Employee");
        } else if (StringUtils.isEmpty(employee.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Employee");
        } 
    }
    
    private void validateUnique(Employee employee) throws ExistentEntityException {
        List<Employee> employeees = employeeRepository.findByName(employee.getName());
        if (employeees!=null && !employeees.isEmpty()) {
            throw new ExistentEntityException(Employee.class,"name="+employee.getName());
        } 
    }

    public Employee updateEmployee(UUID employeeId, Employee employee) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(employee.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Employee");
        } 
    
        Employee persistedEmployee = getById(employeeId);
        if (persistedEmployee != null) {
            if(employee.getBirthday()!=null){
                persistedEmployee.setBirthday(employee.getBirthday());
            }
            if(employee.getBloodType()!=null){
                persistedEmployee.setBloodType(employee.getBloodType());
            }
            if(employee.getBodyComplexion()!=null){
                persistedEmployee.setBodyComplexion(employee.getBodyComplexion());
            }
            if(employee.getCity()!=null){
                persistedEmployee.setCity(employee.getCity());
            }
            if(employee.getCode()!=null){
                persistedEmployee.setCode(employee.getCode());
            }
            if(employee.getColony()!=null){
                persistedEmployee.setColony(employee.getColony());
            }
            if(employee.getCountry()!=null){
                persistedEmployee.setCountry(employee.getCountry());
            }
            if(employee.getEmail()!=null){
                persistedEmployee.setEmail(employee.getEmail());
            }
            if(employee.getExternalNumber()!=null){
                persistedEmployee.setExternalNumber(employee.getExternalNumber());
            }
            if(employee.getEyesColor()!=null){
                persistedEmployee.setEyesColor(employee.getEyesColor());
            }
            if(employee.getFirstSurname()!=null){
                persistedEmployee.setFirstSurname(employee.getFirstSurname());
            }
            if(employee.getHairColor()!=null){
                persistedEmployee.setHairColor(employee.getHairColor());
            }
            if(employee.getHeight()!=null){
                persistedEmployee.setHeight(employee.getHeight());
            }
            if(employee.getHomePhone()!=null){
                persistedEmployee.setHomePhone(employee.getHomePhone());
            }
            if(employee.getInternalNumber()!=null){
                persistedEmployee.setInternalNumber(employee.getInternalNumber());
            }
            if(employee.getMobilePhone()!=null){
                persistedEmployee.setMobilePhone(employee.getMobilePhone());
            }
            if(employee.getName()!=null){
                persistedEmployee.setName(employee.getName());
            }
            if(employee.getNationality()!=null){
                persistedEmployee.setNationality(employee.getNationality());
            }
            if(employee.getPhoto()!=null){
                persistedEmployee.setPhoto(employee.getPhoto());
            }
            if(employee.getSecondSurname()!=null){
                persistedEmployee.setSecondSurname(employee.getSecondSurname());
            }
            if(employee.getSkinColor()!=null){
                persistedEmployee.setSkinColor(employee.getSkinColor());
            }
            if(employee.getState()!=null){
                persistedEmployee.setState(employee.getState());
            }
            if(employee.getStreet()!=null){
                persistedEmployee.setStreet(employee.getStreet());
            }
            if(employee.getSuburb()!=null){
                persistedEmployee.setSuburb(employee.getSuburb());
            }
            if(employee.getWeight()!=null){
                persistedEmployee.setWeight(employee.getWeight());
            }
            if(employee.getZipCode()!=null){
                persistedEmployee.setZipCode(employee.getZipCode());
            }
            if(employee.getActive()!=null){
                persistedEmployee.setActive(employee.getActive());
            }
            if(employee.getSalaryAmount()!=null){
                persistedEmployee.setSalaryAmount(employee.getSalaryAmount());
            }
            if(employee.getEmergencyContactName()!=null){
                persistedEmployee.setEmergencyContactName(employee.getEmergencyContactName());
            }
            if(employee.getEmergencyContactPhone()!=null){
                persistedEmployee.setEmergencyContactPhone(employee.getEmergencyContactPhone());
            }
            if(employee.getStartContractDate()!=null){
                persistedEmployee.setStartContractDate(employee.getStartContractDate());
            }
            if(employee.getEndContractDate()!=null){
                persistedEmployee.setEndContractDate(employee.getEndContractDate());
            }
            persistedEmployee.setUpdateUser(employee.getUpdateUser());
            return employeeRepository.save(persistedEmployee);
        } else {
            throw new EntityNotExistentException(Employee.class,employeeId.toString());
        }
    }

    public Employee deleteEmployee(UUID employeeId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto Employee");
        } 
        Employee employee = getById(employeeId);
        employee.setDeleted(Boolean.TRUE);
        employee.setActive(Boolean.FALSE);
        return employeeRepository.save(employee);
    }

    public List<Employee> findAll(){
        return employeeRepository.findAll();
    }
    
    public Employee getByName(String name){
        return employeeRepository.getByName(name);
    }
    
    public List<Employee> findByNameIgnoreCaseContaining(String name){
        return employeeRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<Employee> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return employeeRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public Employee getBySerial(Integer serial) {
        return employeeRepository.getBySerial(serial);
    }
}
