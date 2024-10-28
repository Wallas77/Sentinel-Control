package com.digivalle.sentinel.services;



import com.google.gson.Gson;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.managers.VehicleBrandLogManager;
import com.digivalle.sentinel.managers.VehicleBrandManager;
import com.digivalle.sentinel.models.VehicleBrand;
import com.digivalle.sentinel.models.VehicleBrandLog;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class VehicleBrandService {
    private final static Logger logger = LoggerFactory.getLogger(VehicleBrandService.class);

    @Autowired
    private VehicleBrandManager vehicleBrandManager;
    
    @Autowired
    private VehicleBrandLogManager vehicleBrandLogManager;
    
    
    public VehicleBrand getById(UUID vehicleBrandId) throws EntityNotExistentException {
        return vehicleBrandManager.getById(vehicleBrandId);
    }
    
    public PagedResponse<VehicleBrand> getVehicleBrand(VehicleBrand vehicleBrand,   Paging paging) {
        return vehicleBrandManager.getVehicleBrand(vehicleBrand, paging);
    }
    
    public List<VehicleBrand> findAll() {
        return vehicleBrandManager.findAll();
    }
    
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public VehicleBrand createVehicleBrand(VehicleBrand vehicleBrand) throws BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        VehicleBrand vehicleBrandPersisted = vehicleBrandManager.createVehicleBrand(vehicleBrand);
        vehicleBrandLogManager.createVehicleBrandLog(convertLog(vehicleBrandPersisted,null,Definitions.LOG_CREATE));
        return getById(vehicleBrandPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public VehicleBrand updateVehicleBrand(UUID vehicleBrandId,VehicleBrand vehicleBrand) throws BusinessLogicException, EntityNotExistentException, ExistentEntityException {
        VehicleBrand vehicleBrandPersisted = vehicleBrandManager.updateVehicleBrand(vehicleBrandId, vehicleBrand);
        vehicleBrandLogManager.createVehicleBrandLog(convertLog(vehicleBrandPersisted,null,Definitions.LOG_UPDATE));
        return getById(vehicleBrandPersisted.getId());
    }
    @Transactional(rollbackFor = {BusinessLogicException.class,Exception.class})
    public void deleteVehicleBrand(UUID vehicleBrandId, String updateUser) throws EntityNotExistentException, BusinessLogicException {
        VehicleBrand vehicleBrandPersisted = vehicleBrandManager.deleteVehicleBrand(vehicleBrandId, updateUser);
        vehicleBrandLogManager.createVehicleBrandLog(convertLog(vehicleBrandPersisted,null,Definitions.LOG_DELETE));
    }  
    
    public Boolean initialize() {
        try{
            createVehicleBrands();
        } catch (BusinessLogicException | EntityNotFoundException | ExistentEntityException | EntityNotExistentException e) {
            logger.error(e.getMessage(), e);
            return false;
        }
        return true;
    }

    private void createVehicleBrands() throws EntityNotFoundException, BusinessLogicException, ExistentEntityException, EntityNotExistentException {
        List<VehicleBrand> vehicleBrandes = findAll();
        if(vehicleBrandes.isEmpty()){
            VehicleBrand vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Abarth");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Acura");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Aeolus");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Aion");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Aiways");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("AKT");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Alfa Romeo");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("AMG");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Aprilia");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Arcfox");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Aston Martin");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Auto");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Avatr");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("BAO");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("BAC");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Baojun");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("BAW");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Beijing");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Beijing Off-road");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Bentley");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Beztune");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("BJEV/Beijing");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("BMW");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Bugatti");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Buick");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("BYD");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Cadillac");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Caterham");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Changan");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Changhe");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Chery");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Chevrolet");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Chrysler");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Citroën");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("CUPRA");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Dallara");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Daihatsu");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Datsun");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Dayun");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Denza");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("DFM");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("DFSK");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Dodge");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Dongfeng");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Dongfeng eπ");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Dongfeng Nammi");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Dorcen");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Ducati");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("DS");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("EV");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Everus");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Exceed");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Fang Cheng");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Farizon Xingxiang V");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Ferrari");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("FIAT");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Foday");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Ford");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Forthing");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Foton");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("GAC Motor");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Geely");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Geely Borui");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Geely Emgrand");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Geely Xingyue");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Genesis");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Geometry");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("GMC");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Great Wall");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("GWM");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Harley Davidson");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Haval");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Hawtai");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Hengrun");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Henteng");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Hipi");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Hispano Suiza");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Honda");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Hongyan");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Honqi");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Huanghai");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Huawei");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Hycan");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Hyundai");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Ikco");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("IM");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Indian");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Infiniti");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Isuzu");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Italika");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("JAC");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("JAC Refine");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("JAC Yiwei");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Jaecco");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Jaguar");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Jeep");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Jetta");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Jettour");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Jiangnan");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Jianyuan");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Jinbei");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Kaicene");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("KAIVI");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Karry");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("KTM");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Kawasaki");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Kawei");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("KG Mobility");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("KIA");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("King Long");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Lada");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Lamborghini");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Lancia");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Land Rover");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("LeapMotor");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("LEVC");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Lexus");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Li Auto");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Lifan");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Lincoln");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Lingbox");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Lingtu");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Livan");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Lotus");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Lucid");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Luxeed");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Luxgen");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("LYNK&CO");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Mahindra");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Maruti Suzuki");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Maserati");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Maxus");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Mazda");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("McLaren");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Mercedes-Benz");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("MG");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("M-Hero");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Minan");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Mini");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Mitsubishi");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Modern");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Morgan");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Neta");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Nevo");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("NIO");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Nissan");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Omoda");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Opel");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("ORA");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Oshan");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Ouling Auto");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Pagani");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Perodua");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Peugeot");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Pocco");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Porsche");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Proton");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Qlin");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Qoros");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Quant");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Radar");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("RAM");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Rimac");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Rising Auto");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Rivian");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Roewe");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Rolls-Royce");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Ruili");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Saipa");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Seat");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Sehol");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Shanghai Maple");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Shenlan");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Sinogold");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Skoda");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Skuworth Skywell");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Smart");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Soueast");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("SRM");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Subaru");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Sunra");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Sunwin");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Suzuki");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("SWM");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Tank");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Tata");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Tesla");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Toyota");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Triumph");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Troller");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("UAZ");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Ultima");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Vauxhall");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Vento");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("VGV");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Vinfast");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Volkswagen");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Volvo");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Voyah");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Wei Ao");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("WEY");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Wuling");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Wuling Baojun");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Xenia");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("XEV");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Xiali");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Xinkai");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Xpeng");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Yamaha");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Yangwang");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Yema");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Yudo");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Yusheng");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("Zeekr");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            vehicleBrand = new VehicleBrand();
            vehicleBrand.setName("ZX AutoTogg");
            vehicleBrand.setUpdateUser(Definitions.USER_DEFAULT);
            createVehicleBrand(vehicleBrand);
            
            logger.info("Las VehicleBrands no existen, inicialización ejecutada");
        } else {
            logger.info("Las VehicleBrands ya existen, inicialización no ejecutada");
        }
    }
    
    public VehicleBrandLog convertLog (VehicleBrand vehicleBrand, UUID transactionId, String action){
        Gson gson= new Gson();
        String tmp = gson.toJson(vehicleBrand);
        VehicleBrandLog vehicleBrandLog = gson.fromJson(tmp,VehicleBrandLog.class);
        vehicleBrandLog.setId(null);
        vehicleBrandLog.setUpdateDate(null);
        vehicleBrandLog.setTransactionId(transactionId);
        vehicleBrandLog.setVehicleBrandId(vehicleBrand.getId());
        vehicleBrandLog.setAction(action);
        vehicleBrandLog.setActiveObject(vehicleBrand.getActive());
        return vehicleBrandLog;
    }
}


