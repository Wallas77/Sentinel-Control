package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.PayrollRoleLogManager;
import com.digivalle.sentinel.models.PayrollRoleLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class PayrollRoleLogService {
    private final static Logger logger = LoggerFactory.getLogger(PayrollRoleLogService.class);

    @Autowired
    private PayrollRoleLogManager payrollRoleLogManager;
    
    
    public PayrollRoleLog getById(UUID payrollRoleLogId) throws EntityNotExistentException {
        return payrollRoleLogManager.getById(payrollRoleLogId);
    }
    
    public PagedResponse<PayrollRoleLog> getPayrollRoleLog(PayrollRoleLog payrollRoleLog,   Paging paging) {
        return payrollRoleLogManager.getPayrollRoleLog(payrollRoleLog, paging);
    }
    
    public List<PayrollRoleLog> findAll() {
        return payrollRoleLogManager.findAll();
    }
    
    public PayrollRoleLog createPayrollRoleLog(PayrollRoleLog payrollRoleLog) throws BusinessLogicException, ExistentEntityException {
        return payrollRoleLogManager.createPayrollRoleLog(payrollRoleLog);
    }
    
    public PayrollRoleLog updatePayrollRoleLog(UUID payrollRoleLogId,PayrollRoleLog payrollRoleLog) throws BusinessLogicException, EntityNotExistentException {
        return payrollRoleLogManager.updatePayrollRoleLog(payrollRoleLogId, payrollRoleLog);
    }
    
    public void deletePayrollRoleLog(UUID payrollRoleLogId) throws EntityNotExistentException {
        payrollRoleLogManager.deletePayrollRoleLog(payrollRoleLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createPayrollRoleLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createPayrollRoleLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


