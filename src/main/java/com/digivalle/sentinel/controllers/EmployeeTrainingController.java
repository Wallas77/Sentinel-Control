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
import com.digivalle.sentinel.models.EmployeeTraining;
import com.digivalle.sentinel.models.EmployeeTrainingLog;
import com.digivalle.sentinel.services.EmployeeTrainingLogService;
import com.digivalle.sentinel.services.EmployeeTrainingService;
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
@RequestMapping("employee/training")

public class EmployeeTrainingController {
    
    @Autowired
    private EmployeeTrainingService employeeTrainingService;
    
    @Autowired
    private EmployeeTrainingLogService employeeTrainingLogService;
    
    @Autowired
    private SecurityService securityService;
    
    @Operation(summary = "Search EmployeeTraining by EmployeeTraining Attributes", description = "This service retrieve EmployeeTraining information filter by EmployeeTraining Attributes", tags = { "employeeTraining" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeTraining.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    ResponseEntity<PagedResponse<EmployeeTraining>> getEmployeeTraining(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
            @RequestBody(required = true) @Parameter(description="EmployeeTraining object - json") EmployeeTraining employeeTraining,
        @RequestParam(value = "page", required = false, defaultValue = "0") @Parameter(description="Page to retrieve") Integer page,
        @RequestParam(value = "pageSize", required = false, defaultValue = "10")  @Parameter(description="Page size to retrieve") Integer pageSize) throws BadRequestException, EntityNotExistentException, NoAccessGrantedException  {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_ACCESS);
            }
            Paging paging = new Paging(page, pageSize);
            return new ResponseEntity<>(employeeTrainingService.getEmployeeTraining(employeeTraining,paging), HttpStatus.OK);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }
    
    @Operation(summary = "Search EmployeeTraining by EmployeeTraining Id", description = "This service retrieve EmployeeTraining information filter by EmployeeTraining Id", tags = { "employeeTraining" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeTraining.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public EmployeeTraining getById(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                     @PathVariable(value = "id") @Parameter(description="EmployeeTraining Id - UUID") UUID employeeTrainingId) throws EntityNotExistentException, BadRequestException, NoAccessGrantedException  {
       
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_ACCESS);
            }
            return employeeTrainingService.getById(employeeTrainingId);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        } 
        
    }
    
    @Operation(summary = "Create EmployeeTraining", description = "This service create a new EmployeeTraining Object", tags = { "employeeTraining" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeTraining.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<EmployeeTraining> createEmployeeTraining(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
            @RequestParam(value = "file", required = false) @Parameter(description="File to create") MultipartFile file,
            @RequestParam(value = "employeeId", required = true) @Parameter(description="EmployeeId to relate the created Document") UUID employeeId,
            @RequestParam(value = "name", required = true) @Parameter(description="Name of the Document to create") String name,
            @RequestParam(value = "description", required = false) @Parameter(description="Description of the Document to create") String description,
            @RequestParam(value = "evaluation", required = false) @Parameter(description="Evaluation of the Document to create") String evaluation,
            @RequestParam(value = "issuedDate", required = true) @Parameter(description="Issued Date of the Document to create") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date issuedDate) throws BusinessLogicException, ExistentEntityException, BadRequestException, EntityNotExistentException, NoAccessGrantedException {
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_CREATE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_CREATE);
            }
            Employee employee = new Employee();
            employee.setId(employeeId);
            String fileName = null;
            byte[] fileBytes = null;
            if(file!=null){
                fileName = file.getOriginalFilename();
                fileBytes = file.getBytes();
            }
            EmployeeTraining employeeTraining = new EmployeeTraining(employee, name, description, fileName, fileBytes, issuedDate,null,evaluation);
            employeeTraining.setUpdateUser(securityService.getUserByToken(token).getName());
            return new ResponseEntity<>(employeeTrainingService.createEmployeeTraining(employeeTraining), HttpStatus.CREATED);
        } catch (IOException ex) {
            throw new BadRequestException(ex.getMessage());
        } 
        
    }
    
    @Operation(summary = "Update EmployeeTraining", description = "This service updates a persited EmployeeTraining Object", tags = { "employeeTraining" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeTraining.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<EmployeeTraining> updateEmployeeTraining(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                 @PathVariable(value = "id") @Parameter(description="EmployeeTraining Id - UUID") UUID employeeTrainingId,
                                                 @RequestParam(value = "file", required = false) @Parameter(description="File to create") MultipartFile file,
            @RequestParam(value = "employeeId", required = true) @Parameter(description="EmployeeId to relate the created Document") UUID employeeId,
            @RequestParam(value = "name", required = true) @Parameter(description="Name of the Document to create") String name,
            @RequestParam(value = "description", required = false) @Parameter(description="Notes of the Document to create") String description,
            @RequestParam(value = "evaluation", required = false) @Parameter(description="Evaluation of the Document to create") String evaluation,
            @RequestParam(value = "issuedDate", required = true) @Parameter(description="Issued Date of the Document to create") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date issuedDate,
            @RequestParam(value = "active", required = false) @Parameter(description="active value for Document") Boolean active) throws BusinessLogicException, BadRequestException, EntityNotExistentException, ExistentEntityException, NoAccessGrantedException {
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_UPDATE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_UPDATE);
            }
            Employee employee = new Employee();
            employee.setId(employeeId);
            String fileName = null;
            byte[] fileBytes = null;
            if(file!=null){
                fileName = file.getOriginalFilename();
                fileBytes = file.getBytes();
            }
            EmployeeTraining employeeTraining = new EmployeeTraining(employee, name, description, fileName, fileBytes, issuedDate,null,evaluation);
            employeeTraining.setUpdateUser(securityService.getUserByToken(token).getName());
            if(active==null){
                active=Boolean.TRUE;
            }
            employeeTraining.setActive(active);
            return new ResponseEntity<>(employeeTrainingService.updateEmployeeTraining(employeeTrainingId, employeeTraining), HttpStatus.OK);
        }catch (IOException ble) {
            throw new BadRequestException(ble.getMessage());
            //throw ble;
        }
        
 
    }
    
    @Operation(summary = "Delete EmployeeTraining", description = "This service deletes (Logically) a persited EmployeeTraining Object", tags = { "employeeTraining" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeTraining.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteEmployeeTraining(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                 @PathVariable(value = "id") @Parameter(description="EmployeeTraining Id - UUID") UUID employeeTrainingId,
                                                 @RequestParam(value = "updateUser", required = false) @Parameter(description="name of update User") String updateUser) throws Exception, BusinessLogicException, EntityNotExistentException, ExistentEntityException, NoAccessGrantedException {
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_DELETE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_DELETE);
            }
            if(updateUser==null){
                updateUser=securityService.getUserByToken(token).getName();
            }
            employeeTrainingService.deleteEmployeeTraining(employeeTrainingId,updateUser);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (RuntimeException ex){
            throw new BadRequestException(ex.getMessage());
        } 
    }
    
    @Operation(summary = "Search EmployeeTrainingLog by EmployeeTrainingLog Attributes", description = "This service retrieve EmployeeTrainingLog information filter by EmployeeTraining Attributes", tags = { "employeeTraining" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeTraining.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/log/search", method = RequestMethod.POST)
    ResponseEntity<PagedResponse<EmployeeTrainingLog>> getEmployeeTrainingLog(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
        @RequestBody(required = true) @Parameter(description="EmployeeTrainingLog object - json") EmployeeTrainingLog employeeTrainingLog,
        @RequestParam(value = "page", required = false, defaultValue = "0") @Parameter(description="Page to retrieve") Integer page,
        @RequestParam(value = "pageSize", required = false, defaultValue = "10") @Parameter(description="Page size to retrieve") Integer pageSize) throws BadRequestException, EntityNotExistentException, NoAccessGrantedException  {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_ACCESS);
            }
            Paging paging = new Paging(page, pageSize);
            return new ResponseEntity<>(employeeTrainingLogService.getEmployeeTrainingLog(employeeTrainingLog,paging), HttpStatus.OK);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }
    
    @Operation(summary = "Search EmployeeTrainingLog by EmployeeTrainingLog Id", description = "This service retrieve EmployeeTrainingLog information filter by EmployeeTrainingLog Id", tags = { "employeeTraining" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeTraining.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/log/{id}", method = RequestMethod.GET)
    public EmployeeTrainingLog getLogById(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                     @PathVariable(value = "id") @Parameter(description="EmployeeTraining Id - UUID") UUID employeeTrainingLogId) throws Exception, EntityNotExistentException, NoAccessGrantedException {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_ACCESS);
            }
            return employeeTrainingLogService.getById(employeeTrainingLogId);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        }  
    }

    
    
}
