package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.SupplierLogManager;
import com.digivalle.sentinel.models.SupplierLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class SupplierLogService {
    private final static Logger logger = LoggerFactory.getLogger(SupplierLogService.class);

    @Autowired
    private SupplierLogManager supplierLogManager;
    
    
    public SupplierLog getById(UUID supplierLogId) throws EntityNotExistentException {
        return supplierLogManager.getById(supplierLogId);
    }
    
    public PagedResponse<SupplierLog> getSupplierLog(SupplierLog supplierLog,   Paging paging) {
        return supplierLogManager.getSupplierLog(supplierLog, paging);
    }
    
    public List<SupplierLog> findAll() {
        return supplierLogManager.findAll();
    }
    
    public SupplierLog createSupplierLog(SupplierLog supplierLog) throws BusinessLogicException, ExistentEntityException {
        return supplierLogManager.createSupplierLog(supplierLog);
    }
    
    public SupplierLog updateSupplierLog(UUID supplierLogId,SupplierLog supplierLog) throws BusinessLogicException, EntityNotExistentException {
        return supplierLogManager.updateSupplierLog(supplierLogId, supplierLog);
    }
    
    public void deleteSupplierLog(UUID supplierLogId) throws EntityNotExistentException {
        supplierLogManager.deleteSupplierLog(supplierLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createSupplierLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createSupplierLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


