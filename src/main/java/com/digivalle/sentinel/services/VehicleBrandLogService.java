package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.VehicleBrandLogManager;
import com.digivalle.sentinel.models.VehicleBrandLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class VehicleBrandLogService {
    private final static Logger logger = LoggerFactory.getLogger(VehicleBrandLogService.class);

    @Autowired
    private VehicleBrandLogManager vehicleBrandLogManager;
    
    
    public VehicleBrandLog getById(UUID vehicleBrandLogId) throws EntityNotExistentException {
        return vehicleBrandLogManager.getById(vehicleBrandLogId);
    }
    
    public PagedResponse<VehicleBrandLog> getVehicleBrandLog(VehicleBrandLog vehicleBrandLog,   Paging paging) {
        return vehicleBrandLogManager.getVehicleBrandLog(vehicleBrandLog, paging);
    }
    
    public List<VehicleBrandLog> findAll() {
        return vehicleBrandLogManager.findAll();
    }
    
    public VehicleBrandLog createVehicleBrandLog(VehicleBrandLog vehicleBrandLog) throws BusinessLogicException, ExistentEntityException {
        return vehicleBrandLogManager.createVehicleBrandLog(vehicleBrandLog);
    }
    
    public VehicleBrandLog updateVehicleBrandLog(UUID vehicleBrandLogId,VehicleBrandLog vehicleBrandLog) throws BusinessLogicException, EntityNotExistentException {
        return vehicleBrandLogManager.updateVehicleBrandLog(vehicleBrandLogId, vehicleBrandLog);
    }
    
    public void deleteVehicleBrandLog(UUID vehicleBrandLogId) throws EntityNotExistentException {
        vehicleBrandLogManager.deleteVehicleBrandLog(vehicleBrandLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createVehicleBrandLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createVehicleBrandLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


