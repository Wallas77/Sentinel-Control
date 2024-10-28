package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.SupplierLogManager;
import com.digivalle.sentinel.managers.SupplierManager;
import com.digivalle.sentinel.models.Supplier;
import com.digivalle.sentinel.models.SupplierLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class SupplierService {
    private final static Logger logger = LoggerFactory.getLogger(SupplierService.class);

    @Autowired
    private SupplierManager supplierManager;
    
    @Autowired
    private SupplierLogManager supplierLogManager;
    
    
    public Supplier getById(UUID supplierId) throws EntityNotExistentException {
        return supplierManager.getById(supplierId);
    }
    
    public PagedResponse<Supplier> getSupplier(Supplier supplier,   Paging paging) {
        return supplierManager.getSupplier(supplier, paging);
    }
    
    public List<Supplier> findAll() {
        return supplierManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Supplier createSupplier(Supplier supplier) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        Supplier supplierPersisted = supplierManager.createSupplier(supplier);
        supplierLogManager.createSupplierLog(convertLog(supplierPersisted,null,Definitions.LOG_CREATE));
        return getById(supplierPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Supplier updateSupplier(UUID supplierId,Supplier supplier) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        Supplier supplierPersisted = supplierManager.updateSupplier(supplierId, supplier);
        supplierLogManager.createSupplierLog(convertLog(supplierPersisted,null,Definitions.LOG_UPDATE));
        return getById(supplierPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteSupplier(UUID supplierId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        Supplier supplierPersisted = supplierManager.deleteSupplier(supplierId, updateUser);
        supplierLogManager.createSupplierLog(convertLog(supplierPersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createSuppliers();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createSuppliers() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        
    }
    
    public SupplierLog convertLog (Supplier supplier, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(supplier);
        SupplierLog supplierLog = gson.fromJson(tmp,SupplierLog.class);
        supplierLog.setId(null);
        supplierLog.setUpdateDate(null);
        supplierLog.setTransactionId(transactionId);
        supplierLog.setSupplierId(supplier.getId());
        supplierLog.setAction(action);
        supplierLog.setActiveObject(supplier.getActive());
        return supplierLog;
    }
}


