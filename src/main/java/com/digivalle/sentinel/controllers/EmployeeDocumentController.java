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
import com.digivalle.sentinel.models.EmployeeDocument;
import com.digivalle.sentinel.models.EmployeeDocumentLog;
import com.digivalle.sentinel.services.EmployeeDocumentLogService;
import com.digivalle.sentinel.services.EmployeeDocumentService;
import com.digivalle.sentinel.services.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("employee/document")

public class EmployeeDocumentController {
    
    @Autowired
    private EmployeeDocumentService employeeDocumentService;
    
    @Autowired
    private EmployeeDocumentLogService employeeDocumentLogService;
    
    @Autowired
    private SecurityService securityService;
    
    
    
    @Operation(summary = "Search EmployeeDocument by EmployeeDocument Attributes", description = "This service retrieve EmployeeDocument information filter by EmployeeDocument Attributes", tags = { "employeeDocument" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeDocument.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    ResponseEntity<PagedResponse<EmployeeDocument>> getEmployeeDocument(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
            @RequestBody(required = true) @Parameter(description="EmployeeDocument object - json") EmployeeDocument employeeDocument,
        @RequestParam(value = "page", required = false, defaultValue = "0") @Parameter(description="Page to retrieve") Integer page,
        @RequestParam(value = "pageSize", required = false, defaultValue = "10")  @Parameter(description="Page size to retrieve") Integer pageSize) throws BadRequestException, EntityNotExistentException, NoAccessGrantedException  {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_ACCESS);
            }
            Paging paging = new Paging(page, pageSize);
            return new ResponseEntity<>(employeeDocumentService.getEmployeeDocument(employeeDocument,paging), HttpStatus.OK);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }
    
    @Operation(summary = "Search EmployeeDocument by EmployeeDocument Id", description = "This service retrieve EmployeeDocument information filter by EmployeeDocument Id", tags = { "employeeDocument" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeDocument.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public EmployeeDocument getById(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                     @PathVariable(value = "id") @Parameter(description="EmployeeDocument Id - UUID") UUID employeeDocumentId) throws EntityNotExistentException, BadRequestException, NoAccessGrantedException  {
       
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_ACCESS);
            }
            return employeeDocumentService.getById(employeeDocumentId);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        } 
        
    }
    
    @Operation(summary = "Create EmployeeDocument", description = "This service create a new EmployeeDocument Object", tags = { "employeeDocument" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeDocument.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<EmployeeDocument> createEmployeeDocument(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
            @RequestParam(value = "file", required = true) @Parameter(description="File to create") MultipartFile file,
            @RequestParam(value = "employeeId", required = true) @Parameter(description="EmployeeId to relate the created Document") UUID employeeId,
            @RequestParam(value = "name", required = true) @Parameter(description="Name of the Document to create") String name,
            @RequestParam(value = "description", required = false) @Parameter(description="Description of the Document to create") String description) throws BusinessLogicException, ExistentEntityException, BadRequestException, EntityNotExistentException, NoAccessGrantedException {
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_CREATE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_CREATE);
            }
            Employee employee = new Employee();
            employee.setId(employeeId);
            EmployeeDocument employeeDocument = new EmployeeDocument(employee, name, description, file.getOriginalFilename(), file.getBytes());
            employeeDocument.setUpdateUser(securityService.getUserByToken(token).getName());
            return new ResponseEntity<>(employeeDocumentService.createEmployeeDocument(employeeDocument), HttpStatus.CREATED);
        } catch (IOException ex) {
            throw new BadRequestException(ex.getMessage());
        } 
        
    }
    
    
    
    @Operation(summary = "Update EmployeeDocument", description = "This service updates a persited EmployeeDocument Object", tags = { "employeeDocument" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeDocument.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<EmployeeDocument> updateEmployeeDocument(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
            @PathVariable(value = "id") @Parameter(description="EmployeeDocument Id - UUID") UUID employeeDocumentId,
            @RequestParam(value = "file", required = true) @Parameter(description="File to create") MultipartFile file,
            @RequestParam(value = "employeeId", required = true) @Parameter(description="EmployeeId to relate the created Document") UUID employeeId,
            @RequestParam(value = "name", required = true) @Parameter(description="Name of the Document to create") String name,
            @RequestParam(value = "description", required = false) @Parameter(description="Description of the Document to create") String description,
            @RequestParam(value = "active", required = false) @Parameter(description="active value for Document") Boolean active) throws BusinessLogicException, BadRequestException, EntityNotExistentException, ExistentEntityException, NoAccessGrantedException {
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_UPDATE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_UPDATE);
            }
            Employee employee = new Employee();
            employee.setId(employeeId);
            EmployeeDocument employeeDocument = new EmployeeDocument(employee, name, description, file.getOriginalFilename(), file.getBytes());
            employeeDocument.setUpdateUser(securityService.getUserByToken(token).getName());
            if(active==null){
                active=Boolean.TRUE;
            }
            employeeDocument.setActive(active);
            return new ResponseEntity<>(employeeDocumentService.updateEmployeeDocument(employeeDocumentId, employeeDocument), HttpStatus.OK);
        }catch (IOException ble) {
            throw new BadRequestException(ble.getMessage());
            //throw ble;
        }
        
 
    }
    
    @Operation(summary = "Delete EmployeeDocument", description = "This service deletes (Logically) a persited EmployeeDocument Object", tags = { "employeeDocument" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeDocument.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteEmployeeDocument(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                 @PathVariable(value = "id") @Parameter(description="EmployeeDocument Id - UUID") UUID employeeDocumentId,
                                                 @RequestParam(value = "updateUser", required = false) @Parameter(description="name of update User") String updateUser) throws Exception, BusinessLogicException, EntityNotExistentException, ExistentEntityException, NoAccessGrantedException {
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_DELETE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_DELETE);
            }
            if(updateUser==null){
                updateUser=securityService.getUserByToken(token).getName();
            }
            employeeDocumentService.deleteEmployeeDocument(employeeDocumentId,updateUser);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (RuntimeException ex){
            throw new BadRequestException(ex.getMessage());
        } 
    }
    
    @Operation(summary = "Search EmployeeDocumentLog by EmployeeDocumentLog Attributes", description = "This service retrieve EmployeeDocumentLog information filter by EmployeeDocument Attributes", tags = { "employeeDocument" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeDocument.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/log/search", method = RequestMethod.POST)
    ResponseEntity<PagedResponse<EmployeeDocumentLog>> getEmployeeDocumentLog(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
        @RequestBody(required = true) @Parameter(description="EmployeeDocumentLog object - json") EmployeeDocumentLog employeeDocumentLog,
        @RequestParam(value = "page", required = false, defaultValue = "0") @Parameter(description="Page to retrieve") Integer page,
        @RequestParam(value = "pageSize", required = false, defaultValue = "10") @Parameter(description="Page size to retrieve") Integer pageSize) throws BadRequestException, EntityNotExistentException, NoAccessGrantedException  {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_ACCESS);
            }
            Paging paging = new Paging(page, pageSize);
            return new ResponseEntity<>(employeeDocumentLogService.getEmployeeDocumentLog(employeeDocumentLog,paging), HttpStatus.OK);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }
    
    @Operation(summary = "Search EmployeeDocumentLog by EmployeeDocumentLog Id", description = "This service retrieve EmployeeDocumentLog information filter by EmployeeDocumentLog Id", tags = { "employeeDocument" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeDocument.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/log/{id}", method = RequestMethod.GET)
    public EmployeeDocumentLog getLogById(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                     @PathVariable(value = "id") @Parameter(description="EmployeeDocument Id - UUID") UUID employeeDocumentLogId) throws Exception, EntityNotExistentException, NoAccessGrantedException {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_ACCESS);
            }
            return employeeDocumentLogService.getById(employeeDocumentLogId);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        }  
    }

    
    
}
