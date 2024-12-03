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
import com.digivalle.sentinel.models.ServiceAttendance;
import com.digivalle.sentinel.repositories.ServiceAttendanceRepository;
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
import java.util.Date;
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
public class ServiceAttendanceManager {
    
    @Autowired
    private ServiceAttendanceRepository serviceAttendanceRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public ServiceAttendance getById(UUID id) throws EntityNotExistentException {
        Optional<ServiceAttendance> serviceAttendance = serviceAttendanceRepository.findById(id);
        if (!serviceAttendance.isEmpty()) {
            return serviceAttendance.get();
        }
        throw new EntityNotExistentException(ServiceAttendance.class,id.toString());
    }
    
    public PagedResponse<ServiceAttendance> getServiceAttendance(ServiceAttendance filter, Paging paging){
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<ServiceAttendance> cq = cb.createQuery(ServiceAttendance.class);
        Root<ServiceAttendance> root = cq.from(ServiceAttendance.class);

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<ServiceAttendance> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<ServiceAttendance> result = query.getResultList();
        
        Page<ServiceAttendance> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(ServiceAttendance filter, CriteriaBuilder cb, Root<ServiceAttendance> root) {
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
        if(filter.getStartDate()!=null && filter.getStartDate2()!=null){
            Calendar cal = Calendar.getInstance();
            // Obtener la zona horaria actual
            ZonedDateTime now = ZonedDateTime.now();
            // Obtener el desplazamiento (offset) en horas
            ZoneOffset offset = now.getOffset();
            cal.setTime(filter.getStartDate());
            cal.add(Calendar.HOUR_OF_DAY, (offset.getTotalSeconds()*-1) / 3600);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            filter.setStartDate(cal.getTime());
            
            cal = Calendar.getInstance();
            cal.setTime(filter.getStartDate2());
            cal.add(Calendar.HOUR_OF_DAY, (offset.getTotalSeconds()*-1) / 3600);
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            filter.setStartDate2(cal.getTime());
            
            predicates.add(cb.between(root.get("startDate"), filter.getStartDate(),filter.getStartDate2()));
        }
        if(filter.getEndDate()!=null && filter.getEndDate2()!=null){
            predicates.add(cb.between(root.get("endDate"), filter.getEndDate(),filter.getEndDate2()));
        }
        
        if(filter.getEmployee()!=null){
            if(filter.getEmployee().getId()!=null){
                predicates.add(cb.equal(root.get("employee").get("id"), filter.getEmployee().getId()));
            }
            if(filter.getEmployee().getSerial()!=null){
                predicates.add(cb.equal(root.get("employee").get("serial"), filter.getEmployee().getSerial()));
            }
            if(filter.getEmployee().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("employee").get("name")), "%" + filter.getEmployee().getName().toLowerCase()+ "%"));
            }
            if(filter.getEmployee().getFirstSurname()!=null){
                predicates.add(cb.like(cb.lower(root.get("employee").get("firstSurname")), "%" + filter.getEmployee().getFirstSurname().toLowerCase()+ "%"));
            }
        }
        if(filter.getRole()!=null){
            if(filter.getRole().getId()!=null){
                predicates.add(cb.equal(root.get("role").get("id"), filter.getRole().getId()));
            }
            if(filter.getRole().getSerial()!=null){
                predicates.add(cb.equal(root.get("role").get("serial"), filter.getRole().getSerial()));
            }
            if(filter.getRole().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("role").get("name")), "%" + filter.getRole().getName().toLowerCase()+ "%"));
            }
        }
        if(filter.getService()!=null){
            if(filter.getService().getId()!=null){
                predicates.add(cb.equal(root.get("service").get("id"), filter.getService().getId()));
            }
            if(filter.getService().getSerial()!=null){
                predicates.add(cb.equal(root.get("service").get("serial"), filter.getService().getSerial()));
            }
            if(filter.getService().getName()!=null){
                predicates.add(cb.like(cb.lower(root.get("service").get("name")), "%" + filter.getService().getName().toLowerCase()+ "%"));
            }
        }
        if(filter.getSalaryParDayAmount()!=null && filter.getSalaryParDayAmount2()!=null){
            predicates.add(cb.between(root.get("salaryParDayAmount"), filter.getSalaryParDayAmount(),filter.getSalaryParDayAmount2()));
        }
        if(filter.getHoursPerDay()!=null){
            predicates.add(cb.equal(root.get("hoursPerDay"), filter.getHoursPerDay()));
        }
        if(filter.getStartDate()!=null && filter.getStartDate2()!=null){
            predicates.add(cb.between(root.get("startDate"), filter.getStartDate(),filter.getStartDate2()));
        }
        if(filter.getEndDate()!=null && filter.getEndDate2()!=null){
            predicates.add(cb.between(root.get("endDate"), filter.getEndDate(),filter.getEndDate2()));
        }
        if(filter.getRealStartDate()!=null && filter.getRealStartDate2()!=null){
            predicates.add(cb.between(root.get("realStartDate"), filter.getRealStartDate(),filter.getRealStartDate2()));
        }
        if(filter.getRealEndDate()!=null && filter.getRealEndDate2()!=null){
            predicates.add(cb.between(root.get("realEndDate"), filter.getRealEndDate(),filter.getRealEndDate2()));
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
        if(filter.getServiceAssignment()!=null){
            if(filter.getServiceAssignment().getId()!=null){
                predicates.add(cb.equal(root.get("serviceAssignment").get("id"), filter.getServiceAssignment().getId()));
            }
            if(filter.getServiceAssignment().getSerial()!=null){
                predicates.add(cb.equal(root.get("serviceAssignment").get("serial"), filter.getServiceAssignment().getSerial()));
            }
            
        }
        if (filter.getEmployee().getUser() != null) {
                if (filter.getEmployee().getUser().getId() != null) {
                    predicates.add(cb.equal(root.get("employee").get("user").get("id"), filter.getEmployee().getUser().getId()));
                }
                if (filter.getEmployee().getUser().getEmail() != null) {
                    predicates.add(cb.like(cb.lower(root.get("employee").get("user").get("email")), "%" + filter.getEmployee().getUser().getEmail().toLowerCase() + "%"));
                }
            }

        return predicates;
    }

    private void applySorting(CriteriaQuery<ServiceAttendance> cq, CriteriaBuilder cb, Root<ServiceAttendance> root, ServiceAttendance filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, ServiceAttendance filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<ServiceAttendance> countRoot = countQuery.from(ServiceAttendance.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
    }

    
    public ServiceAttendance createServiceAttendance(ServiceAttendance serviceAttendance) throws BusinessLogicException, ExistentEntityException {
        validateServiceAttendance(serviceAttendance);
        validateUnique(serviceAttendance);
        return serviceAttendanceRepository.save(serviceAttendance);
    }

    private void validateServiceAttendance(ServiceAttendance serviceAttendance) throws BusinessLogicException {
        if (serviceAttendance.getRole()==null) {
            throw new BusinessLogicException("El campo Role es requerido para el objeto ServiceAttendance");
        } else if (StringUtils.isEmpty(serviceAttendance.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto ServiceAttendance");
        } else if (serviceAttendance.getService()==null) {
            throw new BusinessLogicException("El campo Service es requerido para el objeto ServiceAttendance");
        }
    }
    
    private void validateUnique(ServiceAttendance serviceAttendance) throws ExistentEntityException {
        List<ServiceAttendance> serviceAttendancees = serviceAttendanceRepository.findFiltered(serviceAttendance.getEmployee().getId(), serviceAttendance.getStartDate(),serviceAttendance.getEndDate(),serviceAttendance.getStartDate(),serviceAttendance.getEndDate(),Boolean.TRUE, Boolean.FALSE);
        if (serviceAttendancees!=null && !serviceAttendancees.isEmpty()) {
            //throw new ExistentEntityException(ServiceAttendance.class,"service="+serviceAttendance.getService().getName()+" role="+serviceAttendance.getRole().getName()+" employee="+serviceAttendance.getEmployee().getName() +" startDate="+serviceAttendance.getStartDate());
            String message = " Conflicto con Servicio="+serviceAttendancees.get(0).getService().getName()+" role="+serviceAttendancees.get(0).getRole().getName()+" employee="+serviceAttendancees.get(0).getEmployee().getCode()+" - "+serviceAttendancees.get(0).getEmployee().getName() +" "+serviceAttendancees.get(0).getEmployee().getFirstSurname()+" startDate="+serviceAttendancees.get(0).getStartDate()+" endDate="+serviceAttendancees.get(0).getEndDate();
            throw new BusinessLogicException(message);
        } 
    }

    public ServiceAttendance updateServiceAttendance(UUID serviceAttendanceId, ServiceAttendance serviceAttendance) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(serviceAttendance.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto ServiceAttendance");
        } 
        if(serviceAttendance.getEmployeeReplacement()!=null){
            if(serviceAttendance.getEmployeeReplacement().getId()==serviceAttendance.getEmployee().getId()){
                throw new BusinessLogicException("El Empleado remplazo no puede ser el mismo que el Empleado asignado");
            }
        }
        if(serviceAttendance.getStartDate().before(new Date())){
            throw new BusinessLogicException("No se pueden modificar registros en pasado");
        }
    
        ServiceAttendance persistedServiceAttendance = getById(serviceAttendanceId);
        if (persistedServiceAttendance != null) {
            if(serviceAttendance.getEmployee()!=null && serviceAttendance.getEmployee().getId()!=null){
                persistedServiceAttendance.setEmployee(serviceAttendance.getEmployee());
            } else {
                persistedServiceAttendance.setEmployee(null);
            }
            if(serviceAttendance.getRole()!=null){
                persistedServiceAttendance.setRole(serviceAttendance.getRole());
            }
            if(serviceAttendance.getService()!=null){
                persistedServiceAttendance.setService(serviceAttendance.getService());
            }
            if(serviceAttendance.getSalaryParDayAmount()!=null){
                persistedServiceAttendance.setSalaryParDayAmount(serviceAttendance.getSalaryParDayAmount());
            }
            if(serviceAttendance.getHoursPerDay()!=null){
                persistedServiceAttendance.setHoursPerDay(serviceAttendance.getHoursPerDay());
                Calendar cal = Calendar.getInstance();
                if(serviceAttendance.getStartDate()!=null){
                    cal.setTime(serviceAttendance.getStartDate());
                } else {
                    cal.setTime(persistedServiceAttendance.getStartDate());
                }
                cal.add(Calendar.HOUR_OF_DAY, serviceAttendance.getHoursPerDay());
                serviceAttendance.setEndDate(cal.getTime());
            }
            if(serviceAttendance.getStartDate()!=null){
                persistedServiceAttendance.setStartDate(serviceAttendance.getStartDate());
            }
            if(serviceAttendance.getEndDate()!=null){
                persistedServiceAttendance.setEndDate(serviceAttendance.getEndDate());
            }
            if(serviceAttendance.getRealStartDate()!=null){
                persistedServiceAttendance.setRealStartDate(serviceAttendance.getRealStartDate());
            }
            if(serviceAttendance.getRealEndDate()!=null){
                persistedServiceAttendance.setRealEndDate(serviceAttendance.getRealEndDate());
            }
            if(serviceAttendance.getServiceAssignment()!=null){
                persistedServiceAttendance.setServiceAssignment(serviceAttendance.getServiceAssignment());
            }
            if(serviceAttendance.getEmployeeReplacement()!=null && serviceAttendance.getEmployeeReplacement().getId()!=null){
                persistedServiceAttendance.setEmployeeReplacement(serviceAttendance.getEmployeeReplacement());
            }
            if(serviceAttendance.getActive()!=null){
                persistedServiceAttendance.setActive(serviceAttendance.getActive());
            }
            persistedServiceAttendance.setUpdateUser(serviceAttendance.getUpdateUser());
            return serviceAttendanceRepository.save(persistedServiceAttendance);
        } else {
            throw new EntityNotExistentException(ServiceAttendance.class,serviceAttendanceId.toString());
        }
    }

    public ServiceAttendance deleteServiceAttendance(UUID serviceAttendanceId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(updateUser)) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para actualizar el objeto ServiceAttendance");
        } 
        ServiceAttendance serviceAttendance = getById(serviceAttendanceId);
        serviceAttendance.setDeleted(Boolean.TRUE);
        serviceAttendance.setActive(Boolean.FALSE);
        return serviceAttendanceRepository.save(serviceAttendance);
    }

    public void deleteByServiceAssignmentId(UUID serviceAssignmentId){
        serviceAttendanceRepository.deleteByServiceAssignmentId(serviceAssignmentId);
    }
    
    public void deleteByServiceAssignmentIdAndEmployeeId(UUID serviceAssignmentId, UUID employeeId){
        serviceAttendanceRepository.deleteByServiceAssignmentIdAndEmployeeId(serviceAssignmentId, employeeId);
    }
    
    public List<ServiceAttendance> findAll(){
        return serviceAttendanceRepository.findAll();
    }
    
    
}
