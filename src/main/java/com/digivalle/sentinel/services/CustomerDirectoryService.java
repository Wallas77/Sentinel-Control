package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.CustomerDirectoryLogManager;
import com.digivalle.sentinel.managers.CustomerDirectoryManager;
import com.digivalle.sentinel.models.CustomerDirectory;
import com.digivalle.sentinel.models.CustomerDirectoryLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class CustomerDirectoryService {
    private final static Logger logger = LoggerFactory.getLogger(CustomerDirectoryService.class);

    @Autowired
    private CustomerDirectoryManager customerDirectoryManager;
    
    @Autowired
    private CustomerDirectoryLogManager customerDirectoryLogManager;
    
    
    public CustomerDirectory getById(UUID customerDirectoryId) throws EntityNotExistentException {
        return customerDirectoryManager.getById(customerDirectoryId);
    }
    
    public PagedResponse<CustomerDirectory> getCustomerDirectory(CustomerDirectory customerDirectory,   Paging paging) {
        return customerDirectoryManager.getCustomerDirectory(customerDirectory, paging);
    }
    
    public List<CustomerDirectory> findAll() {
        return customerDirectoryManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public CustomerDirectory createCustomerDirectory(CustomerDirectory customerDirectory) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        CustomerDirectory customerDirectoryPersisted = customerDirectoryManager.createCustomerDirectory(customerDirectory);
        customerDirectoryLogManager.createCustomerDirectoryLog(convertLog(customerDirectoryPersisted,null,Definitions.LOG_CREATE));
        return getById(customerDirectoryPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public CustomerDirectory updateCustomerDirectory(UUID customerDirectoryId,CustomerDirectory customerDirectory) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        CustomerDirectory customerDirectoryPersisted = customerDirectoryManager.updateCustomerDirectory(customerDirectoryId, customerDirectory);
        customerDirectoryLogManager.createCustomerDirectoryLog(convertLog(customerDirectoryPersisted,null,Definitions.LOG_UPDATE));
        return getById(customerDirectoryPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteCustomerDirectory(UUID customerDirectoryId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        CustomerDirectory customerDirectoryPersisted = customerDirectoryManager.deleteCustomerDirectory(customerDirectoryId, updateUser);
        customerDirectoryLogManager.createCustomerDirectoryLog(convertLog(customerDirectoryPersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createCustomerDirectorys();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createCustomerDirectorys() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        List<CustomerDirectory> customerDirectoryes = findAll();
        if(customerDirectoryes.isEmpty()){
            CustomerDirectory customerDirectory = new CustomerDirectory();
            customerDirectory.setName(Definitions.APPLICATION_SENTINEL);
            customerDirectory.setDescription(Definitions.APPLICATION_SENTINEL_DESC);
            customerDirectory.setUpdateUser(Definitions.USER_DEFAULT);
            createCustomerDirectory(customerDirectory);
            
                        logger.info("Las CustomerDirectoryes no existen, inicialización ejecutada");
        } else {
            logger.info("Las CustomerDirectoryes ya existen, inicialización no ejecutada");
        }
    }
    
    public CustomerDirectoryLog convertLog (CustomerDirectory customerDirectory, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(customerDirectory);
        CustomerDirectoryLog customerDirectoryLog = gson.fromJson(tmp,CustomerDirectoryLog.class);
        customerDirectoryLog.setId(null);
        customerDirectoryLog.setUpdateDate(null);
        customerDirectoryLog.setTransactionId(transactionId);
        customerDirectoryLog.setCustomerDirectoryId(customerDirectory.getId());
        customerDirectoryLog.setAction(action);
        customerDirectoryLog.setActiveObject(customerDirectory.getActive());
        return customerDirectoryLog;
    }
}


