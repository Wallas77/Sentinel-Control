package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.BranchLogManager;
import com.digivalle.sentinel.models.BranchLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class BranchLogService {
    private final static Logger logger = LoggerFactory.getLogger(BranchLogService.class);

    @Autowired
    private BranchLogManager branchLogManager;
    
    
    public BranchLog getById(UUID branchLogId) throws EntityNotExistentException {
        return branchLogManager.getById(branchLogId);
    }
    
    public PagedResponse<BranchLog> getBranchLog(BranchLog branchLog,   Paging paging) {
        return branchLogManager.getBranchLog(branchLog, paging);
    }
    
    public List<BranchLog> findAll() {
        return branchLogManager.findAll();
    }
    
    public BranchLog createBranchLog(BranchLog branchLog) throws BusinessLogicException, ExistentEntityException {
        return branchLogManager.createBranchLog(branchLog);
    }
    
    public BranchLog updateBranchLog(UUID branchLogId,BranchLog branchLog) throws BusinessLogicException, EntityNotExistentException {
        return branchLogManager.updateBranchLog(branchLogId, branchLog);
    }
    
    public void deleteBranchLog(UUID branchLogId) throws EntityNotExistentException {
        branchLogManager.deleteBranchLog(branchLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createBranchLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createBranchLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


