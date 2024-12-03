/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel;

/**
 *
 * @author Waldir.Valle
 */
public class Definitions {
   
    public static final long ONE_MINUTE_IN_MILLIS=60000;
    public final static String USER_DEFAULT ="waldir.valle";
    /*REQUEST*/
    public final static String DEFAULT_PAGE_SIZE = "10";
    public final static Integer DEFAULT_SESSION_TIME = 720;
    public final static String XML_CONTENT_TYPE = "application/xml";
    public final static String JSON_CONTENT_TYPE = "application/json";
    public static final int NUM_THREADS = 10; // Define the number of threads in the pool
    /*Profile*/
    public final static String PROFILE_ADMINISTRATOR_SENTINEL ="Administrador";
    public final static String PROFILE_EMPLOYEE_SENTINEL ="Empleado";
    
    /*Apps*/
    public final static String APPLICATION_SENTINEL ="Sentinel Control Application";
    public final static String APPLICATION_SENTINEL_DESC ="Sentinel Control Application Ver. 1.0";
    
    /*Modules*/
    public final static String MODULE_SENTINEL_APPLICATIONS ="Aplicaciones";
    public final static String MODULE_SENTINEL_BRANCHES ="Sucursales";
    public final static String MODULE_SENTINEL_COUNTRIES ="Paises";
    public final static String MODULE_SENTINEL_CUSTOMERS ="Clientes";
    public final static String MODULE_SENTINEL_EMPLOYEES ="Empleados";
    public final static String MODULE_SENTINEL_FISCAL_INFO ="Información Fiscal";
    public final static String MODULE_SENTINEL_INCIDENT_TYPES ="Tipos Incidentes";
    public final static String MODULE_SENTINEL_MODULES ="Modulos";
    public final static String MODULE_SENTINEL_PROFILES ="Perfiles";
    public final static String MODULE_SENTINEL_GRANTS ="Permisos";
    public final static String MODULE_SENTINEL_SUPPLIERS ="Proveedores";
    public final static String MODULE_SENTINEL_USERS ="Usuarios";
    
    /*Grants*/
    public final static String GRANT_ACCESS ="Acceso";
    public final static String GRANT_CREATE ="Agregar";
    public final static String GRANT_UPDATE ="Modificar";
    public final static String GRANT_DELETE ="Eliminar";
    
    /*LOG*/
   public final static String LOG_CREATE ="CREATE";
   public final static String LOG_UPDATE ="UPDATE";
   public final static String LOG_DELETE ="DELETE";
    
   /*USER*/
    public final static String USER_ADMINISTRATOR_SENTINEL_NAME ="Waldir Valle";
    public final static String USER_ADMINISTRATOR_SENTINEL_EMAIL ="w.valle@digivalle.com.mx";
    public final static String USER_ADMINISTRATOR_SENTINEL_PASSWORD ="1qazxsw2";
    
    
    
}
