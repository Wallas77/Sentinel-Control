package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.PayrollDetailLogManager;
import com.digivalle.sentinel.models.PayrollDetailLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class PayrollDetailLogService {
    private final static Logger logger = LoggerFactory.getLogger(PayrollDetailLogService.class);

    @Autowired
    private PayrollDetailLogManager payrollDetailLogManager;
    
    
    public PayrollDetailLog getById(UUID payrollDetailLogId) throws EntityNotExistentException {
        return payrollDetailLogManager.getById(payrollDetailLogId);
    }
    
    public PagedResponse<PayrollDetailLog> getPayrollDetailLog(PayrollDetailLog payrollDetailLog,   Paging paging) {
        return payrollDetailLogManager.getPayrollDetailLog(payrollDetailLog, paging);
    }
    
    public List<PayrollDetailLog> findAll() {
        return payrollDetailLogManager.findAll();
    }
    
    public PayrollDetailLog createPayrollDetailLog(PayrollDetailLog payrollDetailLog) throws BusinessLogicException, ExistentEntityException {
        return payrollDetailLogManager.createPayrollDetailLog(payrollDetailLog);
    }
    
    public PayrollDetailLog updatePayrollDetailLog(UUID payrollDetailLogId,PayrollDetailLog payrollDetailLog) throws BusinessLogicException, EntityNotExistentException {
        return payrollDetailLogManager.updatePayrollDetailLog(payrollDetailLogId, payrollDetailLog);
    }
    
    public void deletePayrollDetailLog(UUID payrollDetailLogId) throws EntityNotExistentException {
        payrollDetailLogManager.deletePayrollDetailLog(payrollDetailLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createPayrollDetailLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createPayrollDetailLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


