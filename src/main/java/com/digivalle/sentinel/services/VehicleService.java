package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.VehicleLogManager;
import com.digivalle.sentinel.managers.VehicleManager;
import com.digivalle.sentinel.models.Vehicle;
import com.digivalle.sentinel.models.VehicleLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class VehicleService {
    private final static Logger logger = LoggerFactory.getLogger(VehicleService.class);

    @Autowired
    private VehicleManager vehicleManager;
    
    @Autowired
    private VehicleLogManager vehicleLogManager;
    
    
    public Vehicle getById(UUID vehicleId) throws EntityNotExistentException {
        return vehicleManager.getById(vehicleId);
    }
    
    public PagedResponse<Vehicle> getVehicle(Vehicle vehicle,   Paging paging) {
        return vehicleManager.getVehicle(vehicle, paging);
    }
    
    public List<Vehicle> findAll() {
        return vehicleManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Vehicle createVehicle(Vehicle vehicle) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        Vehicle vehiclePersisted = vehicleManager.createVehicle(vehicle);
        vehicleLogManager.createVehicleLog(convertLog(vehiclePersisted,null,Definitions.LOG_CREATE));
        return getById(vehiclePersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Vehicle updateVehicle(UUID vehicleId,Vehicle vehicle) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        Vehicle vehiclePersisted = vehicleManager.updateVehicle(vehicleId, vehicle);
        vehicleLogManager.createVehicleLog(convertLog(vehiclePersisted,null,Definitions.LOG_UPDATE));
        return getById(vehiclePersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteVehicle(UUID vehicleId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        Vehicle vehiclePersisted = vehicleManager.deleteVehicle(vehicleId, updateUser);
        vehicleLogManager.createVehicleLog(convertLog(vehiclePersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createVehicles();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createVehicles() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        
    }
    
    public VehicleLog convertLog (Vehicle vehicle, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(vehicle);
        VehicleLog vehicleLog = gson.fromJson(tmp,VehicleLog.class);
        vehicleLog.setId(null);
        vehicleLog.setUpdateDate(null);
        vehicleLog.setTransactionId(transactionId);
        vehicleLog.setVehicleId(vehicle.getId());
        vehicleLog.setAction(action);
        vehicleLog.setActiveObject(vehicle.getActive());
        return vehicleLog;
    }
}


