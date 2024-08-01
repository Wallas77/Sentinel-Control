package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.FiscalInfoLogManager;
import com.digivalle.sentinel.managers.FiscalInfoManager;
import com.digivalle.sentinel.models.FiscalInfo;
import com.digivalle.sentinel.models.FiscalInfoLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class FiscalInfoService {
    private final static Logger logger = LoggerFactory.getLogger(FiscalInfoService.class);

    @Autowired
    private FiscalInfoManager fiscalInfoManager;
    
    @Autowired
    private FiscalInfoLogManager fiscalInfoLogManager;
    
    
    public FiscalInfo getById(UUID fiscalInfoId) throws EntityNotExistentException {
        return fiscalInfoManager.getById(fiscalInfoId);
    }
    
    public PagedResponse<FiscalInfo> getFiscalInfo(FiscalInfo fiscalInfo,   Paging paging) {
        return fiscalInfoManager.getFiscalInfo(fiscalInfo, paging);
    }
    
    public List<FiscalInfo> findAll() {
        return fiscalInfoManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public FiscalInfo createFiscalInfo(FiscalInfo fiscalInfo) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        FiscalInfo fiscalInfoPersisted = fiscalInfoManager.createFiscalInfo(fiscalInfo);
        fiscalInfoLogManager.createFiscalInfoLog(convertLog(fiscalInfoPersisted,null,Definitions.LOG_CREATE));
        return getById(fiscalInfoPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public FiscalInfo updateFiscalInfo(UUID fiscalInfoId,FiscalInfo fiscalInfo) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        FiscalInfo fiscalInfoPersisted = fiscalInfoManager.updateFiscalInfo(fiscalInfoId, fiscalInfo);
        fiscalInfoLogManager.createFiscalInfoLog(convertLog(fiscalInfoPersisted,null,Definitions.LOG_UPDATE));
        return getById(fiscalInfoPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteFiscalInfo(UUID fiscalInfoId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        FiscalInfo fiscalInfoPersisted = fiscalInfoManager.deleteFiscalInfo(fiscalInfoId, updateUser);
        fiscalInfoLogManager.createFiscalInfoLog(convertLog(fiscalInfoPersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createFiscalInfos();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createFiscalInfos() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        
    }
    
    public FiscalInfoLog convertLog (FiscalInfo fiscalInfo, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(fiscalInfo);
        FiscalInfoLog fiscalInfoLog = gson.fromJson(tmp,FiscalInfoLog.class);
        fiscalInfoLog.setId(null);
        fiscalInfoLog.setUpdateDate(null);
        fiscalInfoLog.setTransactionId(transactionId);
        fiscalInfoLog.setFiscalInfoId(fiscalInfo.getId());
        fiscalInfoLog.setAction(action);
        return fiscalInfoLog;
    }
}


