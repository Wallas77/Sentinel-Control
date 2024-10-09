package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.EmployeeDocumentLogManager;
import com.digivalle.sentinel.managers.EmployeeDocumentManager;
import com.digivalle.sentinel.models.EmployeeDocument;
import com.digivalle.sentinel.models.EmployeeDocumentLog;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class EmployeeDocumentService {
    private final static Logger logger = LoggerFactory.getLogger(EmployeeDocumentService.class);

    @Autowired
    private EmployeeDocumentManager employeeDocumentManager;
    
    @Autowired
    private EmployeeDocumentLogManager employeeDocumentLogManager;
    
    
    public EmployeeDocument getById(UUID employeeDocumentId) throws EntityNotExistentException {
        return employeeDocumentManager.getById(employeeDocumentId);
    }
    
    public PagedResponse<EmployeeDocument> getEmployeeDocument(EmployeeDocument employeeDocument,   Paging paging) {
        return employeeDocumentManager.getEmployeeDocument(employeeDocument, paging);
    }
    
    public List<EmployeeDocument> findAll() {
        return employeeDocumentManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public EmployeeDocument createEmployeeDocument(EmployeeDocument employeeDocument) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException, IOException {
        EmployeeDocument employeeDocumentPersisted = employeeDocumentManager.createEmployeeDocument(employeeDocument);
        employeeDocumentLogManager.createEmployeeDocumentLog(convertLog(employeeDocumentPersisted,null,Definitions.LOG_CREATE));
        return getById(employeeDocumentPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public EmployeeDocument updateEmployeeDocument(UUID employeeDocumentId,EmployeeDocument employeeDocument) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        EmployeeDocument employeeDocumentPersisted = employeeDocumentManager.updateEmployeeDocument(employeeDocumentId, employeeDocument);
        employeeDocumentLogManager.createEmployeeDocumentLog(convertLog(employeeDocumentPersisted,null,Definitions.LOG_UPDATE));
        return getById(employeeDocumentPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteEmployeeDocument(UUID employeeDocumentId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        EmployeeDocument employeeDocumentPersisted = employeeDocumentManager.deleteEmployeeDocument(employeeDocumentId, updateUser);
        employeeDocumentLogManager.createEmployeeDocumentLog(convertLog(employeeDocumentPersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createEmployeeDocuments();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createEmployeeDocuments() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        
    }
    
    public EmployeeDocumentLog convertLog (EmployeeDocument employeeDocument, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(employeeDocument);
        EmployeeDocumentLog employeeDocumentLog = gson.fromJson(tmp,EmployeeDocumentLog.class);
        employeeDocumentLog.setId(null);
        employeeDocumentLog.setUpdateDate(null);
        employeeDocumentLog.setTransactionId(transactionId);
        employeeDocumentLog.setEmployeeDocumentId(employeeDocument.getId());
        employeeDocumentLog.setAction(action);
        return employeeDocumentLog;
    }
}


