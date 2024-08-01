package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.CompanyLogManager;
import com.digivalle.sentinel.models.CompanyLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class CompanyLogService {
    private final static Logger logger = LoggerFactory.getLogger(CompanyLogService.class);

    @Autowired
    private CompanyLogManager companyLogManager;
    
    
    public CompanyLog getById(UUID companyLogId) throws EntityNotExistentException {
        return companyLogManager.getById(companyLogId);
    }
    
    public PagedResponse<CompanyLog> getCompanyLog(CompanyLog companyLog,   Paging paging) {
        return companyLogManager.getCompanyLog(companyLog, paging);
    }
    
    public List<CompanyLog> findAll() {
        return companyLogManager.findAll();
    }
    
    public CompanyLog createCompanyLog(CompanyLog companyLog) throws BusinessLogicException, ExistentEntityException {
        return companyLogManager.createCompanyLog(companyLog);
    }
    
    public CompanyLog updateCompanyLog(UUID companyLogId,CompanyLog companyLog) throws BusinessLogicException, EntityNotExistentException {
        return companyLogManager.updateCompanyLog(companyLogId, companyLog);
    }
    
    public void deleteCompanyLog(UUID companyLogId) throws EntityNotExistentException {
        companyLogManager.deleteCompanyLog(companyLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createCompanyLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createCompanyLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


