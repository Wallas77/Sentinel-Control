/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.controllers;

import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BadRequestException;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.exceptions.NoAccessGrantedException;
import com.digivalle.sentinel.exceptions.handler.model.ErrorDetails;
import com.digivalle.sentinel.models.Employee;
import com.digivalle.sentinel.models.EmployeeHealthCheckup;
import com.digivalle.sentinel.models.EmployeeHealthCheckupLog;
import com.digivalle.sentinel.services.EmployeeHealthCheckupLogService;
import com.digivalle.sentinel.services.EmployeeHealthCheckupService;
import com.digivalle.sentinel.services.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Waldir.Valle
 */
@RestController
@RequestMapping("employee/healthCheckup")

public class EmployeeHealthCheckupController {
    
    @Autowired
    private EmployeeHealthCheckupService employeeHealthCheckupService;
    
    @Autowired
    private EmployeeHealthCheckupLogService employeeHealthCheckupLogService;
    
    @Autowired
    private SecurityService securityService;
    
    @Operation(summary = "Search EmployeeHealthCheckup by EmployeeHealthCheckup Attributes", description = "This service retrieve EmployeeHealthCheckup information filter by EmployeeHealthCheckup Attributes", tags = { "employeeHealthCheckup" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeHealthCheckup.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    ResponseEntity<PagedResponse<EmployeeHealthCheckup>> getEmployeeHealthCheckup(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
            @RequestBody(required = true) @Parameter(description="EmployeeHealthCheckup object - json") EmployeeHealthCheckup employeeHealthCheckup,
        @RequestParam(value = "page", required = false, defaultValue = "0") @Parameter(description="Page to retrieve") Integer page,
        @RequestParam(value = "pageSize", required = false, defaultValue = "10")  @Parameter(description="Page size to retrieve") Integer pageSize) throws BadRequestException, EntityNotExistentException, NoAccessGrantedException  {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_ACCESS);
            }
            Paging paging = new Paging(page, pageSize);
            return new ResponseEntity<>(employeeHealthCheckupService.getEmployeeHealthCheckup(employeeHealthCheckup,paging), HttpStatus.OK);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }
    
    @Operation(summary = "Search EmployeeHealthCheckup by EmployeeHealthCheckup Id", description = "This service retrieve EmployeeHealthCheckup information filter by EmployeeHealthCheckup Id", tags = { "employeeHealthCheckup" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeHealthCheckup.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public EmployeeHealthCheckup getById(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                     @PathVariable(value = "id") @Parameter(description="EmployeeHealthCheckup Id - UUID") UUID employeeHealthCheckupId) throws EntityNotExistentException, BadRequestException, NoAccessGrantedException  {
       
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_ACCESS);
            }
            return employeeHealthCheckupService.getById(employeeHealthCheckupId);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        } 
        
    }
    
    @Operation(summary = "Create EmployeeHealthCheckup", description = "This service create a new EmployeeHealthCheckup Object", tags = { "employeeHealthCheckup" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeHealthCheckup.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<EmployeeHealthCheckup> createEmployeeHealthCheckup(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
            @RequestParam(value = "file", required = true) @Parameter(description="File to create") MultipartFile file,
            @RequestParam(value = "employeeId", required = true) @Parameter(description="EmployeeId to relate the created Document") UUID employeeId,
            @RequestParam(value = "name", required = true) @Parameter(description="Name of the Document to create") String name,
            @RequestParam(value = "notes", required = false) @Parameter(description="Notes of the Document to create") String notes,
            @RequestParam(value = "issuedDate", required = true) @Parameter(description="Issued Date of the Document to create") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date issuedDate) throws BusinessLogicException, ExistentEntityException, BadRequestException, EntityNotExistentException, NoAccessGrantedException {
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_CREATE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_CREATE);
            }
            Employee employee = new Employee();
            employee.setId(employeeId);
            EmployeeHealthCheckup employeeHealthCheckup = new EmployeeHealthCheckup(employee, name, notes, file.getOriginalFilename(), file.getBytes(), issuedDate,null);
            employeeHealthCheckup.setUpdateUser(securityService.getUserByToken(token).getName());
            return new ResponseEntity<>(employeeHealthCheckupService.createEmployeeHealthCheckup(employeeHealthCheckup), HttpStatus.CREATED);
        } catch (IOException ex) {
            throw new BadRequestException(ex.getMessage());
        } 
        
    }
    
    @Operation(summary = "Update EmployeeHealthCheckup", description = "This service updates a persited EmployeeHealthCheckup Object", tags = { "employeeHealthCheckup" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeHealthCheckup.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<EmployeeHealthCheckup> updateEmployeeHealthCheckup(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                 @PathVariable(value = "id") @Parameter(description="EmployeeHealthCheckup Id - UUID") UUID employeeHealthCheckupId,
                                                 @RequestParam(value = "file", required = true) @Parameter(description="File to create") MultipartFile file,
            @RequestParam(value = "employeeId", required = true) @Parameter(description="EmployeeId to relate the created Document") UUID employeeId,
            @RequestParam(value = "name", required = true) @Parameter(description="Name of the Document to create") String name,
            @RequestParam(value = "notes", required = false) @Parameter(description="Notes of the Document to create") String notes,
            @RequestParam(value = "issuedDate", required = true) @Parameter(description="Issued Date of the Document to create") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date issuedDate,
            @RequestParam(value = "active", required = false) @Parameter(description="active value for Document") Boolean active) throws BusinessLogicException, BadRequestException, EntityNotExistentException, ExistentEntityException, NoAccessGrantedException {
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_UPDATE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_UPDATE);
            }
            Employee employee = new Employee();
            employee.setId(employeeId);
            EmployeeHealthCheckup employeeHealthCheckup = new EmployeeHealthCheckup(employee, name, notes, file.getOriginalFilename(), file.getBytes(), issuedDate,null);
            employeeHealthCheckup.setUpdateUser(securityService.getUserByToken(token).getName());
            if(active==null){
                active=Boolean.TRUE;
            }
            employeeHealthCheckup.setActive(active);
            return new ResponseEntity<>(employeeHealthCheckupService.updateEmployeeHealthCheckup(employeeHealthCheckupId, employeeHealthCheckup), HttpStatus.OK);
        }catch (IOException ble) {
            throw new BadRequestException(ble.getMessage());
            //throw ble;
        }
        
 
    }
    
    @Operation(summary = "Delete EmployeeHealthCheckup", description = "This service deletes (Logically) a persited EmployeeHealthCheckup Object", tags = { "employeeHealthCheckup" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeHealthCheckup.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteEmployeeHealthCheckup(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                 @PathVariable(value = "id") @Parameter(description="EmployeeHealthCheckup Id - UUID") UUID employeeHealthCheckupId,
                                                 @RequestParam(value = "updateUser", required = false) @Parameter(description="name of update User") String updateUser) throws Exception, BusinessLogicException, EntityNotExistentException, ExistentEntityException, NoAccessGrantedException {
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_DELETE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_DELETE);
            }
            if(updateUser==null){
                updateUser=securityService.getUserByToken(token).getName();
            }
            employeeHealthCheckupService.deleteEmployeeHealthCheckup(employeeHealthCheckupId,updateUser);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (RuntimeException ex){
            throw new BadRequestException(ex.getMessage());
        } 
    }
    
    @Operation(summary = "Search EmployeeHealthCheckupLog by EmployeeHealthCheckupLog Attributes", description = "This service retrieve EmployeeHealthCheckupLog information filter by EmployeeHealthCheckup Attributes", tags = { "employeeHealthCheckup" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeHealthCheckup.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/log/search", method = RequestMethod.POST)
    ResponseEntity<PagedResponse<EmployeeHealthCheckupLog>> getEmployeeHealthCheckupLog(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
        @RequestBody(required = true) @Parameter(description="EmployeeHealthCheckupLog object - json") EmployeeHealthCheckupLog employeeHealthCheckupLog,
        @RequestParam(value = "page", required = false, defaultValue = "0") @Parameter(description="Page to retrieve") Integer page,
        @RequestParam(value = "pageSize", required = false, defaultValue = "10") @Parameter(description="Page size to retrieve") Integer pageSize) throws BadRequestException, EntityNotExistentException, NoAccessGrantedException  {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_ACCESS);
            }
            Paging paging = new Paging(page, pageSize);
            return new ResponseEntity<>(employeeHealthCheckupLogService.getEmployeeHealthCheckupLog(employeeHealthCheckupLog,paging), HttpStatus.OK);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }
    
    @Operation(summary = "Search EmployeeHealthCheckupLog by EmployeeHealthCheckupLog Id", description = "This service retrieve EmployeeHealthCheckupLog information filter by EmployeeHealthCheckupLog Id", tags = { "employeeHealthCheckup" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeHealthCheckup.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/log/{id}", method = RequestMethod.GET)
    public EmployeeHealthCheckupLog getLogById(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                     @PathVariable(value = "id") @Parameter(description="EmployeeHealthCheckup Id - UUID") UUID employeeHealthCheckupLogId) throws Exception, EntityNotExistentException, NoAccessGrantedException {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_ACCESS);
            }
            return employeeHealthCheckupLogService.getById(employeeHealthCheckupLogId);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        }  
    }

    
    
}
