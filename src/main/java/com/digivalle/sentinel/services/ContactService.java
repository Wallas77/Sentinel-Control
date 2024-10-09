package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.ContactLogManager;
import com.digivalle.sentinel.managers.ContactManager;
import com.digivalle.sentinel.models.Contact;
import com.digivalle.sentinel.models.ContactLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class ContactService {
    private final static Logger logger = LoggerFactory.getLogger(ContactService.class);

    @Autowired
    private ContactManager contactManager;
    
    @Autowired
    private ContactLogManager contactLogManager;
    
    
    public Contact getById(UUID contactId) throws EntityNotExistentException {
        return contactManager.getById(contactId);
    }
    
    public PagedResponse<Contact> getContact(Contact contact,   Paging paging) {
        return contactManager.getContact(contact, paging);
    }
    
    public List<Contact> findAll() {
        return contactManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Contact createContact(Contact contact) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        Contact contactPersisted = contactManager.createContact(contact);
        contactLogManager.createContactLog(convertLog(contactPersisted,null,Definitions.LOG_CREATE));
        return getById(contactPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Contact updateContact(UUID contactId,Contact contact) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        Contact contactPersisted = contactManager.updateContact(contactId, contact);
        contactLogManager.createContactLog(convertLog(contactPersisted,null,Definitions.LOG_UPDATE));
        return getById(contactPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteContact(UUID contactId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        Contact contactPersisted = contactManager.deleteContact(contactId, updateUser);
        contactLogManager.createContactLog(convertLog(contactPersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createContacts();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createContacts() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        
    }
    
    public ContactLog convertLog (Contact contact, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(contact);
        ContactLog contactLog = gson.fromJson(tmp,ContactLog.class);
        contactLog.setId(null);
        contactLog.setUpdateDate(null);
        contactLog.setTransactionId(transactionId);
        contactLog.setContactId(contact.getId());
        contactLog.setAction(action);
        return contactLog;
    }
}


