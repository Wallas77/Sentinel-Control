package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.CustomerDirectoryLogManager;
import com.digivalle.sentinel.models.CustomerDirectoryLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class CustomerDirectoryLogService {
    private final static Logger logger = LoggerFactory.getLogger(CustomerDirectoryLogService.class);

    @Autowired
    private CustomerDirectoryLogManager customerDirectoryLogManager;
    
    
    public CustomerDirectoryLog getById(UUID customerDirectoryLogId) throws EntityNotExistentException {
        return customerDirectoryLogManager.getById(customerDirectoryLogId);
    }
    
    public PagedResponse<CustomerDirectoryLog> getCustomerDirectoryLog(CustomerDirectoryLog customerDirectoryLog,   Paging paging) {
        return customerDirectoryLogManager.getCustomerDirectoryLog(customerDirectoryLog, paging);
    }
    
    public List<CustomerDirectoryLog> findAll() {
        return customerDirectoryLogManager.findAll();
    }
    
    public CustomerDirectoryLog createCustomerDirectoryLog(CustomerDirectoryLog customerDirectoryLog) throws BusinessLogicException, ExistentEntityException {
        return customerDirectoryLogManager.createCustomerDirectoryLog(customerDirectoryLog);
    }
    
    public CustomerDirectoryLog updateCustomerDirectoryLog(UUID customerDirectoryLogId,CustomerDirectoryLog customerDirectoryLog) throws BusinessLogicException, EntityNotExistentException {
        return customerDirectoryLogManager.updateCustomerDirectoryLog(customerDirectoryLogId, customerDirectoryLog);
    }
    
    public void deleteCustomerDirectoryLog(UUID customerDirectoryLogId) throws EntityNotExistentException {
        customerDirectoryLogManager.deleteCustomerDirectoryLog(customerDirectoryLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createCustomerDirectoryLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createCustomerDirectoryLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


