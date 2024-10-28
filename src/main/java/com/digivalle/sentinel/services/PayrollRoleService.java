package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.PayrollRoleLogManager;
import com.digivalle.sentinel.managers.PayrollRoleManager;
import com.digivalle.sentinel.models.Payroll;
import com.digivalle.sentinel.models.PayrollRole;
import com.digivalle.sentinel.models.PayrollRoleLog;
import com.digivalle.sentinel.models.Role;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class PayrollRoleService {
    private final static Logger logger = LoggerFactory.getLogger(PayrollRoleService.class);

    @Autowired
    private PayrollRoleManager payrollRoleManager;
    
    @Autowired
    private PayrollRoleLogManager payrollRoleLogManager;
    
    
    public PayrollRole getById(UUID payrollRoleId) throws EntityNotExistentException {
        return payrollRoleManager.getById(payrollRoleId);
    }
    
    public PagedResponse<PayrollRole> getPayrollRole(PayrollRole payrollRole,   Paging paging) {
        return payrollRoleManager.getPayrollRole(payrollRole, paging);
    }
    
    public List<PayrollRole> findAll() {
        return payrollRoleManager.findAll();
    }
    
    public List<PayrollRole> findByPayrollAndActiveAndDeleted(Payroll payroll, Boolean active, Boolean deleted){
        return payrollRoleManager.findByPayrollAndActiveAndDeleted(payroll, active, deleted);
    }
    
    public List<PayrollRole> findByPayrollAndRoleAndActiveAndDeleted(Payroll payroll, Role role, Boolean active, Boolean deleted){
        return payrollRoleManager.findByPayrollAndRoleAndActiveAndDeleted(payroll,role, active, deleted);
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public PayrollRole createPayrollRole(PayrollRole payrollRole, UUID transactionId) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        PayrollRole payrollRolePersisted = payrollRoleManager.createPayrollRole(payrollRole);
        payrollRoleLogManager.createPayrollRoleLog(convertLog(payrollRolePersisted,transactionId,Definitions.LOG_CREATE));
        return getById(payrollRolePersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public PayrollRole updatePayrollRole(UUID payrollRoleId,PayrollRole payrollRole, UUID transactionId) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        PayrollRole payrollRolePersisted = payrollRoleManager.updatePayrollRole(payrollRoleId, payrollRole);
        payrollRoleLogManager.createPayrollRoleLog(convertLog(payrollRolePersisted,transactionId,Definitions.LOG_UPDATE));
        return getById(payrollRolePersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deletePayrollRole(UUID payrollRoleId, String updateUser, UUID transactionId) throws EntityNotExistentException, BusinessLogicException {
        PayrollRole payrollRolePersisted = payrollRoleManager.deletePayrollRole(payrollRoleId, updateUser);
        payrollRoleLogManager.createPayrollRoleLog(convertLog(payrollRolePersisted,transactionId,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createPayrollRoles();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createPayrollRoles() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        
    }
    
    public PayrollRoleLog convertLog (PayrollRole payrollRole, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(payrollRole);
        PayrollRoleLog payrollRoleLog = gson.fromJson(tmp,PayrollRoleLog.class);
        payrollRoleLog.setId(null);
        payrollRoleLog.setUpdateDate(null);
        payrollRoleLog.setTransactionId(transactionId);
        payrollRoleLog.setPayrollRoleId(payrollRole.getId());
        payrollRoleLog.setAction(action);
        payrollRoleLog.setActiveObject(payrollRole.getActive());
        return payrollRoleLog;
    }
}


