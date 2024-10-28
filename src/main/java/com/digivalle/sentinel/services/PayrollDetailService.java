package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.PayrollDetailLogManager;
import com.digivalle.sentinel.managers.PayrollDetailManager;
import com.digivalle.sentinel.models.PayrollDetail;
import com.digivalle.sentinel.models.PayrollDetailLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class PayrollDetailService {
    private final static Logger logger = LoggerFactory.getLogger(PayrollDetailService.class);

    @Autowired
    private PayrollDetailManager payrollDetailManager;
    
    @Autowired
    private PayrollDetailLogManager payrollDetailLogManager;
    
    
    public PayrollDetail getById(UUID payrollDetailId) throws EntityNotExistentException {
        return payrollDetailManager.getById(payrollDetailId);
    }
    
    public PagedResponse<PayrollDetail> getPayrollDetail(PayrollDetail payrollDetail,   Paging paging) {
        return payrollDetailManager.getPayrollDetail(payrollDetail, paging);
    }
    
    public List<PayrollDetail> findAll() {
        return payrollDetailManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public PayrollDetail createPayrollDetail(PayrollDetail payrollDetail) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        PayrollDetail payrollDetailPersisted = payrollDetailManager.createPayrollDetail(payrollDetail);
        payrollDetailLogManager.createPayrollDetailLog(convertLog(payrollDetailPersisted,null,Definitions.LOG_CREATE));
        return getById(payrollDetailPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public PayrollDetail updatePayrollDetail(UUID payrollDetailId,PayrollDetail payrollDetail) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        PayrollDetail payrollDetailPersisted = payrollDetailManager.updatePayrollDetail(payrollDetailId, payrollDetail);
        payrollDetailLogManager.createPayrollDetailLog(convertLog(payrollDetailPersisted,null,Definitions.LOG_UPDATE));
        return getById(payrollDetailPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deletePayrollDetail(UUID payrollDetailId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        PayrollDetail payrollDetailPersisted = payrollDetailManager.deletePayrollDetail(payrollDetailId, updateUser);
        payrollDetailLogManager.createPayrollDetailLog(convertLog(payrollDetailPersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createPayrollDetails();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createPayrollDetails() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        
    }
    
    public PayrollDetailLog convertLog (PayrollDetail payrollDetail, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(payrollDetail);
        PayrollDetailLog payrollDetailLog = gson.fromJson(tmp,PayrollDetailLog.class);
        payrollDetailLog.setId(null);
        payrollDetailLog.setUpdateDate(null);
        payrollDetailLog.setTransactionId(transactionId);
        payrollDetailLog.setPayrollDetailId(payrollDetail.getId());
        payrollDetailLog.setAction(action);
        payrollDetailLog.setActiveObject(payrollDetail.getActive());
        return payrollDetailLog;
    }
}


