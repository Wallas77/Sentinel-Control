/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.controllers;

import javax.validation.Valid;
import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BadRequestException;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.exceptions.NoAccessGrantedException;
import com.digivalle.sentinel.exceptions.handler.model.ErrorDetails;
import com.digivalle.sentinel.models.AccessControl;
import com.digivalle.sentinel.models.AccessControlLog;
import com.digivalle.sentinel.services.AccessControlLogService;
import com.digivalle.sentinel.services.AccessControlService;
import com.digivalle.sentinel.services.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Waldir.Valle
 */
@RestController
@RequestMapping("accessControl")

public class AccessControlController {
    
    @Autowired
    private AccessControlService accessControlService;
    
    @Autowired
    private AccessControlLogService accessControlLogService;
    
    @Autowired
    private SecurityService securityService;
    
    
    
    @Operation(summary = "Search AccessControl by AccessControl Attributes", description = "This service retrieve AccessControl information filter by AccessControl Attributes", tags = { "accessControl" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = AccessControl.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    ResponseEntity<PagedResponse<AccessControl>> getAccessControl(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
            @RequestBody(required = true) @Parameter(description="AccessControl object - json") AccessControl accessControl,
        @RequestParam(value = "page", required = false, defaultValue = "0") @Parameter(description="Page to retrieve") Integer page,
        @RequestParam(value = "pageSize", required = false, defaultValue = "10")  @Parameter(description="Page size to retrieve") Integer pageSize) throws BadRequestException, EntityNotExistentException, NoAccessGrantedException  {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_ACCESS);
            }
            Paging paging = new Paging(page, pageSize);
            return new ResponseEntity<>(accessControlService.getAccessControl(accessControl,paging), HttpStatus.OK);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }
    
    @Operation(summary = "Search AccessControl by AccessControl Id", description = "This service retrieve AccessControl information filter by AccessControl Id", tags = { "accessControl" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = AccessControl.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public AccessControl getById(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                     @PathVariable(value = "id") @Parameter(description="AccessControl Id - UUID") UUID accessControlId) throws EntityNotExistentException, BadRequestException, NoAccessGrantedException  {
       
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_ACCESS);
            }
            return accessControlService.getById(accessControlId);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        } 
        
    }
    
    @Operation(summary = "Create AccessControl", description = "This service create a new AccessControl Object", tags = { "accessControl" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = AccessControl.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<AccessControl> createAccessControl(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
            @Valid @RequestBody(required = true) @Parameter(description="AccessControl object - json") AccessControl accessControl) throws BusinessLogicException, ExistentEntityException, BadRequestException, EntityNotExistentException, NoAccessGrantedException {
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_CREATE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_CREATE);
            }
            if(accessControl.getUpdateUser()==null){
                accessControl.setUpdateUser(securityService.getUserByToken(token).getName());
            }
            return new ResponseEntity<>(accessControlService.createAccessControl(accessControl), HttpStatus.CREATED);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        } 
        
    }
    
    @Operation(summary = "Update AccessControl", description = "This service updates a persited AccessControl Object", tags = { "accessControl" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = AccessControl.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<AccessControl> updateAccessControl(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                 @PathVariable(value = "id") @Parameter(description="AccessControl Id - UUID") UUID accessControlId,
                                                 @Valid @RequestBody(required = true) @Parameter(description="AccessControl object - json") AccessControl accessControl, BindingResult bindingResult) throws BusinessLogicException, BadRequestException, EntityNotExistentException, ExistentEntityException, NoAccessGrantedException {
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_UPDATE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_UPDATE);
            }
            if(accessControl.getUpdateUser()==null){
                accessControl.setUpdateUser(securityService.getUserByToken(token).getName());
            }
            return new ResponseEntity<>(accessControlService.updateAccessControl(accessControlId, accessControl), HttpStatus.OK);
        }catch (Exception ble) {
            throw new BadRequestException(ble.getMessage());
            //throw ble;
        }
        
 
    }
    
    @Operation(summary = "Delete AccessControl", description = "This service deletes (Logically) a persited AccessControl Object", tags = { "accessControl" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = AccessControl.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteAccessControl(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                 @PathVariable(value = "id") @Parameter(description="AccessControl Id - UUID") UUID accessControlId,
                                                 @RequestParam(value = "updateUser") @Parameter(description="name of update User") String updateUser) throws Exception, BusinessLogicException, EntityNotExistentException, ExistentEntityException, NoAccessGrantedException {
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_DELETE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_DELETE);
            }
            if(updateUser==null){
                updateUser = securityService.getUserByToken(token).getName();
            }
            accessControlService.deleteAccessControl(accessControlId,updateUser);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (RuntimeException ex){
            throw new BadRequestException(ex.getMessage());
        } 
    }
    
    @Operation(summary = "Search AccessControlLog by AccessControlLog Attributes", description = "This service retrieve AccessControlLog information filter by AccessControl Attributes", tags = { "accessControl" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = AccessControl.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/log/search", method = RequestMethod.POST)
    ResponseEntity<PagedResponse<AccessControlLog>> getAccessControlLog(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
        @RequestBody(required = true) @Parameter(description="AccessControlLog object - json") AccessControlLog accessControlLog,
        @RequestParam(value = "page", required = false, defaultValue = "0") @Parameter(description="Page to retrieve") Integer page,
        @RequestParam(value = "pageSize", required = false, defaultValue = "10") @Parameter(description="Page size to retrieve") Integer pageSize) throws BadRequestException, EntityNotExistentException, NoAccessGrantedException  {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_ACCESS);
            }
            Paging paging = new Paging(page, pageSize);
            return new ResponseEntity<>(accessControlLogService.getAccessControlLog(accessControlLog,paging), HttpStatus.OK);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }
    
    @Operation(summary = "Search AccessControlLog by AccessControlLog Id", description = "This service retrieve AccessControlLog information filter by AccessControlLog Id", tags = { "accessControl" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = AccessControl.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/log/{id}", method = RequestMethod.GET)
    public AccessControlLog getLogById(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                     @PathVariable(value = "id") @Parameter(description="AccessControl Id - UUID") UUID accessControlLogId) throws Exception, EntityNotExistentException, NoAccessGrantedException {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_ACCESS);
            }
            return accessControlLogService.getById(accessControlLogId);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        }  
    }

    
    
}