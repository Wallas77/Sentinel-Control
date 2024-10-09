package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.PayrollLogManager;
import com.digivalle.sentinel.models.PayrollLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class PayrollLogService {
    private final static Logger logger = LoggerFactory.getLogger(PayrollLogService.class);

    @Autowired
    private PayrollLogManager payrollLogManager;
    
    
    public PayrollLog getById(UUID payrollLogId) throws EntityNotExistentException {
        return payrollLogManager.getById(payrollLogId);
    }
    
    public PagedResponse<PayrollLog> getPayrollLog(PayrollLog payrollLog,   Paging paging) {
        return payrollLogManager.getPayrollLog(payrollLog, paging);
    }
    
    public List<PayrollLog> findAll() {
        return payrollLogManager.findAll();
    }
    
    public PayrollLog createPayrollLog(PayrollLog payrollLog) throws BusinessLogicException, ExistentEntityException {
        return payrollLogManager.createPayrollLog(payrollLog);
    }
    
    public PayrollLog updatePayrollLog(UUID payrollLogId,PayrollLog payrollLog) throws BusinessLogicException, EntityNotExistentException {
        return payrollLogManager.updatePayrollLog(payrollLogId, payrollLog);
    }
    
    public void deletePayrollLog(UUID payrollLogId) throws EntityNotExistentException {
        payrollLogManager.deletePayrollLog(payrollLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createPayrollLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createPayrollLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


