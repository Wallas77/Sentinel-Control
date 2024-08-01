package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.CustomerLogManager;
import com.digivalle.sentinel.models.CustomerLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class CustomerLogService {
    private final static Logger logger = LoggerFactory.getLogger(CustomerLogService.class);

    @Autowired
    private CustomerLogManager customerLogManager;
    
    
    public CustomerLog getById(UUID customerLogId) throws EntityNotExistentException {
        return customerLogManager.getById(customerLogId);
    }
    
    public PagedResponse<CustomerLog> getCustomerLog(CustomerLog customerLog,   Paging paging) {
        return customerLogManager.getCustomerLog(customerLog, paging);
    }
    
    public List<CustomerLog> findAll() {
        return customerLogManager.findAll();
    }
    
    public CustomerLog createCustomerLog(CustomerLog customerLog) throws BusinessLogicException, ExistentEntityException {
        return customerLogManager.createCustomerLog(customerLog);
    }
    
    public CustomerLog updateCustomerLog(UUID customerLogId,CustomerLog customerLog) throws BusinessLogicException, EntityNotExistentException {
        return customerLogManager.updateCustomerLog(customerLogId, customerLog);
    }
    
    public void deleteCustomerLog(UUID customerLogId) throws EntityNotExistentException {
        customerLogManager.deleteCustomerLog(customerLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createCustomerLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createCustomerLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


