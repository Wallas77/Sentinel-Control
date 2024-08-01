package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.CountryLogManager;
import com.digivalle.sentinel.models.CountryLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class CountryLogService {
    private final static Logger logger = LoggerFactory.getLogger(CountryLogService.class);

    @Autowired
    private CountryLogManager countryLogManager;
    
    
    public CountryLog getById(UUID countryLogId) throws EntityNotExistentException {
        return countryLogManager.getById(countryLogId);
    }
    
    public PagedResponse<CountryLog> getCountryLog(CountryLog countryLog,   Paging paging) {
        return countryLogManager.getCountryLog(countryLog, paging);
    }
    
    public List<CountryLog> findAll() {
        return countryLogManager.findAll();
    }
    
    public CountryLog createCountryLog(CountryLog countryLog) throws BusinessLogicException, ExistentEntityException {
        return countryLogManager.createCountryLog(countryLog);
    }
    
    public CountryLog updateCountryLog(UUID countryLogId,CountryLog countryLog) throws BusinessLogicException, EntityNotExistentException {
        return countryLogManager.updateCountryLog(countryLogId, countryLog);
    }
    
    public void deleteCountryLog(UUID countryLogId) throws EntityNotExistentException {
        countryLogManager.deleteCountryLog(countryLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createCountryLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createCountryLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


