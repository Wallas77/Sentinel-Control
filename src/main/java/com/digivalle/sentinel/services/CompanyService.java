package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.CompanyLogManager;
import com.digivalle.sentinel.managers.CompanyManager;
import com.digivalle.sentinel.models.Company;
import com.digivalle.sentinel.models.CompanyLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class CompanyService {
    private final static Logger logger = LoggerFactory.getLogger(CompanyService.class);

    @Autowired
    private CompanyManager companyManager;
    
    @Autowired
    private CompanyLogManager companyLogManager;
    
    
    public Company getById(UUID companyId) throws EntityNotExistentException {
        return companyManager.getById(companyId);
    }
    
    public PagedResponse<Company> getCompany(Company company,   Paging paging) {
        return companyManager.getCompany(company, paging);
    }
    
    public List<Company> findAll() {
        return companyManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Company createCompany(Company company) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        Company companyPersisted = companyManager.createCompany(company);
        companyLogManager.createCompanyLog(convertLog(companyPersisted,null,Definitions.LOG_CREATE));
        return getById(companyPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Company updateCompany(UUID companyId,Company company) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        Company companyPersisted = companyManager.updateCompany(companyId, company);
        companyLogManager.createCompanyLog(convertLog(companyPersisted,null,Definitions.LOG_UPDATE));
        return getById(companyPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteCompany(UUID companyId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        Company companyPersisted = companyManager.deleteCompany(companyId, updateUser);
        companyLogManager.createCompanyLog(convertLog(companyPersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createCompanys();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createCompanys() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        
    }
    
    public CompanyLog convertLog (Company company, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(company);
        CompanyLog companyLog = gson.fromJson(tmp,CompanyLog.class);
        companyLog.setId(null);
        companyLog.setUpdateDate(null);
        companyLog.setTransactionId(transactionId);
        companyLog.setCompanyId(company.getId());
        companyLog.setAction(action);
        companyLog.setActiveObject(company.getActive());
        return companyLog;
    }
}


