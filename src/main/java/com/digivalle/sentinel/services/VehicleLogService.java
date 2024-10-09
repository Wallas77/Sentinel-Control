package com.digivalle.sentinel.services;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.VehicleLogManager;
import com.digivalle.sentinel.models.VehicleLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class VehicleLogService {
    private final static Logger logger = LoggerFactory.getLogger(VehicleLogService.class);

    @Autowired
    private VehicleLogManager vehicleLogManager;
    
    
    public VehicleLog getById(UUID vehicleLogId) throws EntityNotExistentException {
        return vehicleLogManager.getById(vehicleLogId);
    }
    
    public PagedResponse<VehicleLog> getVehicleLog(VehicleLog vehicleLog,   Paging paging) {
        return vehicleLogManager.getVehicleLog(vehicleLog, paging);
    }
    
    public List<VehicleLog> findAll() {
        return vehicleLogManager.findAll();
    }
    
    public VehicleLog createVehicleLog(VehicleLog vehicleLog) throws BusinessLogicException, ExistentEntityException {
        return vehicleLogManager.createVehicleLog(vehicleLog);
    }
    
    public VehicleLog updateVehicleLog(UUID vehicleLogId,VehicleLog vehicleLog) throws BusinessLogicException, EntityNotExistentException {
        return vehicleLogManager.updateVehicleLog(vehicleLogId, vehicleLog);
    }
    
    public void deleteVehicleLog(UUID vehicleLogId) throws EntityNotExistentException {
        vehicleLogManager.deleteVehicleLog(vehicleLogId);
    }  
    
    public Boolean initialize() throws EntityNotFoundException, ExistentEntityException {
        try{
            createVehicleLogs();
        } catch (BusinessLogicException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createVehicleLogs() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException {
       
    }
}


