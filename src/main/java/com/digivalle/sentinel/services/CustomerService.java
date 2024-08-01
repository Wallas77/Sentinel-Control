package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.CustomerLogManager;
import com.digivalle.sentinel.managers.CustomerManager;
import com.digivalle.sentinel.models.Customer;
import com.digivalle.sentinel.models.CustomerLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class CustomerService {
    private final static Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerManager customerManager;
    
    @Autowired
    private CustomerLogManager customerLogManager;
    
    
    public Customer getById(UUID customerId) throws EntityNotExistentException {
        return customerManager.getById(customerId);
    }
    
    public PagedResponse<Customer> getCustomer(Customer customer,   Paging paging) {
        return customerManager.getCustomer(customer, paging);
    }
    
    public List<Customer> findAll() {
        return customerManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Customer createCustomer(Customer customer) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        Customer customerPersisted = customerManager.createCustomer(customer);
        customerLogManager.createCustomerLog(convertLog(customerPersisted,null,Definitions.LOG_CREATE));
        return getById(customerPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Customer updateCustomer(UUID customerId,Customer customer) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        Customer customerPersisted = customerManager.updateCustomer(customerId, customer);
        customerLogManager.createCustomerLog(convertLog(customerPersisted,null,Definitions.LOG_UPDATE));
        return getById(customerPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteCustomer(UUID customerId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        Customer customerPersisted = customerManager.deleteCustomer(customerId, updateUser);
        customerLogManager.createCustomerLog(convertLog(customerPersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createCustomers();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createCustomers() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        
    }
    
    public CustomerLog convertLog (Customer customer, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(customer);
        CustomerLog customerLog = gson.fromJson(tmp,CustomerLog.class);
        customerLog.setId(null);
        customerLog.setUpdateDate(null);
        customerLog.setTransactionId(transactionId);
        customerLog.setCustomerId(customer.getId());
        customerLog.setAction(action);
        return customerLog;
    }
}


