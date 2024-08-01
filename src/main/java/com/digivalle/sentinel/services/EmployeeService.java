package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.EmployeeLogManager;
import com.digivalle.sentinel.managers.EmployeeManager;
import com.digivalle.sentinel.models.Employee;
import com.digivalle.sentinel.models.EmployeeLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class EmployeeService {
    private final static Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    @Autowired
    private EmployeeManager employeeManager;
    
    @Autowired
    private EmployeeLogManager employeeLogManager;
    
    
    public Employee getById(UUID employeeId) throws EntityNotExistentException {
        return employeeManager.getById(employeeId);
    }
    
    public PagedResponse<Employee> getEmployee(Employee employee,   Paging paging) {
        return employeeManager.getEmployee(employee, paging);
    }
    
    public List<Employee> findAll() {
        return employeeManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Employee createEmployee(Employee employee) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        Employee employeePersisted = employeeManager.createEmployee(employee);
        employeeLogManager.createEmployeeLog(convertLog(employeePersisted,null,Definitions.LOG_CREATE));
        return getById(employeePersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Employee updateEmployee(UUID employeeId,Employee employee) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        Employee employeePersisted = employeeManager.updateEmployee(employeeId, employee);
        employeeLogManager.createEmployeeLog(convertLog(employeePersisted,null,Definitions.LOG_UPDATE));
        return getById(employeePersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteEmployee(UUID employeeId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        Employee employeePersisted = employeeManager.deleteEmployee(employeeId, updateUser);
        employeeLogManager.createEmployeeLog(convertLog(employeePersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createEmployees();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createEmployees() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        
    }
    
    public EmployeeLog convertLog (Employee employee, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(employee);
        EmployeeLog employeeLog = gson.fromJson(tmp,EmployeeLog.class);
        employeeLog.setId(null);
        employeeLog.setUpdateDate(null);
        employeeLog.setTransactionId(transactionId);
        employeeLog.setEmployeeId(employee.getId());
        employeeLog.setAction(action);
        return employeeLog;
    }
}


