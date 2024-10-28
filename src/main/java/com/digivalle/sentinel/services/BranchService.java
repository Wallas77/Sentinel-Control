package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.BranchLogManager;
import com.digivalle.sentinel.managers.BranchManager;
import com.digivalle.sentinel.models.Branch;
import com.digivalle.sentinel.models.BranchLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class BranchService {
    private final static Logger logger = LoggerFactory.getLogger(BranchService.class);

    @Autowired
    private BranchManager branchManager;
    
    @Autowired
    private BranchLogManager branchLogManager;
    
    
    public Branch getById(UUID branchId) throws EntityNotExistentException {
        return branchManager.getById(branchId);
    }
    
    public PagedResponse<Branch> getBranch(Branch branch,   Paging paging) {
        return branchManager.getBranch(branch, paging);
    }
    
    public List<Branch> findAll() {
        return branchManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Branch createBranch(Branch branch) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        Branch branchPersisted = branchManager.createBranch(branch);
        branchLogManager.createBranchLog(convertLog(branchPersisted,null,Definitions.LOG_CREATE));
        return getById(branchPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Branch updateBranch(UUID branchId,Branch branch) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        Branch branchPersisted = branchManager.updateBranch(branchId, branch);
        branchLogManager.createBranchLog(convertLog(branchPersisted,null,Definitions.LOG_UPDATE));
        return getById(branchPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteBranch(UUID branchId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        Branch branchPersisted = branchManager.deleteBranch(branchId, updateUser);
        branchLogManager.createBranchLog(convertLog(branchPersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createBranchs();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createBranchs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        
    }
    
    public BranchLog convertLog (Branch branch, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(branch);
        BranchLog branchLog = gson.fromJson(tmp,BranchLog.class);
        branchLog.setId(null);
        branchLog.setUpdateDate(null);
        branchLog.setTransactionId(transactionId);
        branchLog.setBranchId(branch.getId());
        branchLog.setAction(action);
        branchLog.setActiveObject(branch.getActive());
        return branchLog;
    }
}


