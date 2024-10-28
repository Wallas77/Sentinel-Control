package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.IncidentLogManager;
import com.digivalle.sentinel.managers.IncidentManager;
import com.digivalle.sentinel.models.Incident;
import com.digivalle.sentinel.models.IncidentLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class IncidentService {
    private final static Logger logger = LoggerFactory.getLogger(IncidentService.class);

    @Autowired
    private IncidentManager incidentManager;
    
    @Autowired
    private IncidentLogManager incidentLogManager;
    
    
    public Incident getById(UUID incidentId) throws EntityNotExistentException {
        return incidentManager.getById(incidentId);
    }
    
    public PagedResponse<Incident> getIncident(Incident incident,   Paging paging) {
        return incidentManager.getIncident(incident, paging);
    }
    
    public List<Incident> findAll() {
        return incidentManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Incident createIncident(Incident incident) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        Incident incidentPersisted = incidentManager.createIncident(incident);
        incidentLogManager.createIncidentLog(convertLog(incidentPersisted,null,Definitions.LOG_CREATE));
        return getById(incidentPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public Incident updateIncident(UUID incidentId,Incident incident) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        Incident incidentPersisted = incidentManager.updateIncident(incidentId, incident);
        incidentLogManager.createIncidentLog(convertLog(incidentPersisted,null,Definitions.LOG_UPDATE));
        return getById(incidentPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteIncident(UUID incidentId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        Incident incidentPersisted = incidentManager.deleteIncident(incidentId, updateUser);
        incidentLogManager.createIncidentLog(convertLog(incidentPersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createIncidents();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createIncidents() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        
    }
    
    public IncidentLog convertLog (Incident incident, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(incident);
        IncidentLog incidentLog = gson.fromJson(tmp,IncidentLog.class);
        incidentLog.setId(null);
        incidentLog.setUpdateDate(null);
        incidentLog.setTransactionId(transactionId);
        incidentLog.setIncidentId(incident.getId());
        incidentLog.setAction(action);
        incidentLog.setActiveObject(incident.getActive());
        return incidentLog;
    }
}


