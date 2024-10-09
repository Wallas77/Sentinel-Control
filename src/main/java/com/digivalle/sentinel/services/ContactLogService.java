package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.ContactLogManager;
import com.digivalle.sentinel.models.ContactLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ContactLogService {
    private final static Logger logger = LoggerFactory.getLogger(ContactLogService.class);

    @Autowired
    private ContactLogManager contactLogManager;
    
    
    public ContactLog getById(UUID contactLogId) throws EntityNotExistentException {
        return contactLogManager.getById(contactLogId);
    }
    
    public PagedResponse<ContactLog> getContactLog(ContactLog contactLog,   Paging paging) {
        return contactLogManager.getContactLog(contactLog, paging);
    }
    
    public List<ContactLog> findAll() {
        return contactLogManager.findAll();
    }
    
    public ContactLog createContactLog(ContactLog contactLog) throws BusinessLogicException, ExistentEntityException {
        return contactLogManager.createContactLog(contactLog);
    }
    
    public ContactLog updateContactLog(UUID contactLogId,ContactLog contactLog) throws BusinessLogicException, EntityNotExistentException {
        return contactLogManager.updateContactLog(contactLogId, contactLog);
    }
    
    public void deleteContactLog(UUID contactLogId) throws EntityNotExistentException {
        contactLogManager.deleteContactLog(contactLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createContactLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createContactLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


