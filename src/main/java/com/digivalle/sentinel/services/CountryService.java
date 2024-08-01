package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.CountryLogManager;
import com.digivalle.sentinel.managers.CountryManager;
import com.digivalle.sentinel.models.Country;
import com.digivalle.sentinel.models.CountryLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class CountryService {
    private final static Logger logger = LoggerFactory.getLogger(CountryService.class);

    @Autowired
    private CountryManager countryManager;
    
    @Autowired
    private CountryLogManager countryLogManager;
    
    
    public Country getById(UUID countryId) throws EntityNotExistentException {
        return countryManager.getById(countryId);
    }
    
    public PagedResponse<Country> getCountry(Country country,   Paging paging) {
        return countryManager.getCountry(country, paging);
    }
    
    public List<Country> findAll() {
        return countryManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Country createCountry(Country country) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        Country countryPersisted = countryManager.createCountry(country);
        countryLogManager.createCountryLog(convertLog(countryPersisted,null,Definitions.LOG_CREATE));
        return getById(countryPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Country updateCountry(UUID countryId,Country country) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        Country countryPersisted = countryManager.updateCountry(countryId, country);
        countryLogManager.createCountryLog(convertLog(countryPersisted,null,Definitions.LOG_UPDATE));
        return getById(countryPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteCountry(UUID countryId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        Country countryPersisted = countryManager.deleteCountry(countryId, updateUser);
        countryLogManager.createCountryLog(convertLog(countryPersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createCountries();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createCountries() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        List<Country> countries = findAll();
        if(countries.isEmpty()){
            Country country = new Country();
            country.setName("México");
            country.setCode("MX");
            country.setUpdateUser(Definitions.USER_DEFAULT);
            createCountry(country);
            
            logger.info("Los Countries no existen, inicialización ejecutada");
        } else {
            logger.info("Los Countries ya existen, inicialización no ejecutada");
        }
    }
    
    public CountryLog convertLog (Country country, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(country);
        CountryLog countryLog = gson.fromJson(tmp,CountryLog.class);
        countryLog.setId(null);
        countryLog.setUpdateDate(null);
        countryLog.setTransactionId(transactionId);
        countryLog.setCountryId(country.getId());
        countryLog.setAction(action);
        return countryLog;
    }
}


