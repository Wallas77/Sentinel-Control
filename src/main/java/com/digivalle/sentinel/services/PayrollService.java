package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.PayrollLogManager;
import com.digivalle.sentinel.managers.PayrollManager;
import com.digivalle.sentinel.models.Payroll;
import com.digivalle.sentinel.models.PayrollLog;
import com.digivalle.sentinel.models.PayrollRole;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class PayrollService {
    private final static Logger logger = LoggerFactory.getLogger(PayrollService.class);

    @Autowired
    private PayrollManager payrollManager;
    
    @Autowired
    private PayrollLogManager payrollLogManager;
    
    @Autowired
    private PayrollRoleService payrollRoleService;
    
    
    public Payroll getById(UUID payrollId) throws EntityNotExistentException {
        return payrollManager.getById(payrollId);
    }
    
    public PagedResponse<Payroll> getPayroll(Payroll payroll,   Paging paging) {
        return payrollManager.getPayroll(payroll, paging);
    }
    
    public List<Payroll> findAll() {
        return payrollManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Payroll createPayroll(Payroll payroll) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        UUID transactionId = UUID.randomUUID();
        List<PayrollRole> payrollRoles = payroll.getPayrollRoles();
        payroll.setPayrollRoles(null);
        Payroll payrollPersisted = payrollManager.createPayroll(payroll);
        if(payrollRoles!=null){
            for(PayrollRole payrollRole: payrollRoles){
                payrollRole.setPayroll(payrollPersisted);
                payrollRole.setUpdateUser(payroll.getUpdateUser());
                payrollRoleService.createPayrollRole(payrollRole,transactionId);
            }
        }
        
        payrollLogManager.createPayrollLog(convertLog(payrollPersisted,transactionId,Definitions.LOG_CREATE));
        return getById(payrollPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Payroll updatePayroll(UUID payrollId,Payroll payroll) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        UUID transactionId = UUID.randomUUID();
        List<PayrollRole> payrollRoles = payroll.getPayrollRoles();
        payroll.setPayrollRoles(null);
        Payroll payrollPersisted = payrollManager.updatePayroll(payrollId, payroll);
        if(payrollRoles!=null){
             for(PayrollRole payrollRole: payrollRoles){
                payrollRole.setPayroll(payrollPersisted);
                PayrollRole payrollRoleList = payrollPersisted.getPayrollRoles().stream().filter(obj -> obj.getRole().getName().equals(payrollRole.getRole().getName())).findFirst().orElse(null);
                if(payrollRoleList==null && payrollRoleList.getId()==null){
                    payrollRoleService.createPayrollRole(payrollRole,transactionId);
                } else {
                    payrollRoleService.updatePayrollRole(payrollRoleList.getId(),payrollRole,transactionId);
                }
            }
        }
        payrollLogManager.createPayrollLog(convertLog(payrollPersisted,transactionId,Definitions.LOG_UPDATE));
        return getById(payrollPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deletePayroll(UUID payrollId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        UUID transactionId = UUID.randomUUID();
        
        Payroll payrollPersisted = payrollManager.deletePayroll(payrollId, updateUser);
        List<PayrollRole> payrollRoles = payrollRoleService.findByPayrollAndActiveAndDeleted(payrollPersisted, Boolean.FALSE, Boolean.TRUE);
        for(PayrollRole payrollRole: payrollRoles){
            payrollRoleService.deletePayrollRole(payrollRole.getId(), updateUser,transactionId);
        }
        payrollLogManager.createPayrollLog(convertLog(payrollPersisted,transactionId,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createPayrolls();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createPayrolls() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        
    }
    
    public PayrollLog convertLog (Payroll payroll, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(payroll);
        PayrollLog payrollLog = gson.fromJson(tmp,PayrollLog.class);
        payrollLog.setId(null);
        payrollLog.setUpdateDate(null);
        payrollLog.setTransactionId(transactionId);
        payrollLog.setPayrollId(payroll.getId());
        payrollLog.setAction(action);
        payrollLog.setActiveObject(payroll.getActive());
        return payrollLog;
    }
}


