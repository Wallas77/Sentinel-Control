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
import com.digivalle.sentinel.models.RoleResponsability;
import com.digivalle.sentinel.models.RoleResponsabilityLog;
import com.digivalle.sentinel.services.RoleResponsabilityLogService;
import com.digivalle.sentinel.services.RoleResponsabilityService;
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
@RequestMapping("roleResponsability")

public class RoleResponsabilityController {
    
    @Autowired
    private RoleResponsabilityService roleResponsabilityService;
    
    @Autowired
    private RoleResponsabilityLogService roleResponsabilityLogService;
    
    @Autowired
    private SecurityService securityService;
    
    
    
    @Operation(summary = "Search RoleResponsability by RoleResponsability Attributes", description = "This service retrieve RoleResponsability information filter by RoleResponsability Attributes", tags = { "roleResponsability" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RoleResponsability.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    ResponseEntity<PagedResponse<RoleResponsability>> getRoleResponsability(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
            @RequestBody(required = true) @Parameter(description="RoleResponsability object - json") RoleResponsability roleResponsability,
        @RequestParam(value = "page", required = false, defaultValue = "0") @Parameter(description="Page to retrieve") Integer page,
        @RequestParam(value = "pageSize", required = false, defaultValue = "10")  @Parameter(description="Page size to retrieve") Integer pageSize) throws BadRequestException, EntityNotExistentException, NoAccessGrantedException  {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_ACCESS);
            }
            Paging paging = new Paging(page, pageSize);
            return new ResponseEntity<>(roleResponsabilityService.getRoleResponsability(roleResponsability,paging), HttpStatus.OK);
        } catch (EntityNotExistentException | NoAccessGrantedException ex) {
            throw ex;
        }
    }
    
    @Operation(summary = "Search RoleResponsability by RoleResponsability Id", description = "This service retrieve RoleResponsability information filter by RoleResponsability Id", tags = { "roleResponsability" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RoleResponsability.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public RoleResponsability getById(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                     @PathVariable(value = "id") @Parameter(description="RoleResponsability Id - UUID") UUID roleResponsabilityId) throws EntityNotExistentException, BadRequestException, NoAccessGrantedException  {
       
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_ACCESS);
            }
            return roleResponsabilityService.getById(roleResponsabilityId);
        } catch (EntityNotExistentException | NoAccessGrantedException ex) {
            throw ex;
        } 
        
    }
    
    @Operation(summary = "Create RoleResponsability", description = "This service create a new RoleResponsability Object", tags = { "roleResponsability" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RoleResponsability.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<RoleResponsability> createRoleResponsability(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
            @Valid @RequestBody(required = true) @Parameter(description="RoleResponsability object - json") RoleResponsability roleResponsability) throws BusinessLogicException, ExistentEntityException, BadRequestException, EntityNotExistentException, NoAccessGrantedException {
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_CREATE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_CREATE);
            }
            if(roleResponsability.getUpdateUser()==null){
                roleResponsability.setUpdateUser(securityService.getUserByToken(token).getEmail());
            }
            return new ResponseEntity<>(roleResponsabilityService.createRoleResponsability(roleResponsability), HttpStatus.CREATED);
        } catch (BusinessLogicException | EntityNotExistentException | ExistentEntityException | NoAccessGrantedException ex) {
            throw ex;
        } 
        
    }
    
    @Operation(summary = "Update RoleResponsability", description = "This service updates a persited RoleResponsability Object", tags = { "roleResponsability" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RoleResponsability.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<RoleResponsability> updateRoleResponsability(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                 @PathVariable(value = "id") @Parameter(description="RoleResponsability Id - UUID") UUID roleResponsabilityId,
                                                 @Valid @RequestBody(required = true) @Parameter(description="RoleResponsability object - json") RoleResponsability roleResponsability, BindingResult bindingResult) throws BusinessLogicException, BadRequestException, EntityNotExistentException, ExistentEntityException, NoAccessGrantedException {
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_UPDATE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_UPDATE);
            }
            if(roleResponsability.getUpdateUser()==null){
                roleResponsability.setUpdateUser(securityService.getUserByToken(token).getEmail());
            }
            return new ResponseEntity<>(roleResponsabilityService.updateRoleResponsability(roleResponsabilityId, roleResponsability), HttpStatus.OK);
        }catch (BusinessLogicException | EntityNotExistentException | ExistentEntityException | NoAccessGrantedException ex) {
            throw ex;
        }
        
 
    }
    
    @Operation(summary = "Delete RoleResponsability", description = "This service deletes (Logically) a persited RoleResponsability Object", tags = { "roleResponsability" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RoleResponsability.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteRoleResponsability(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                 @PathVariable(value = "id") @Parameter(description="RoleResponsability Id - UUID") UUID roleResponsabilityId,
                                                 @RequestParam(value = "updateUser") @Parameter(description="name of update User") String updateUser) throws Exception, BusinessLogicException, EntityNotExistentException, ExistentEntityException, NoAccessGrantedException {
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_DELETE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_DELETE);
            }
            if(updateUser==null){
                updateUser =securityService.getUserByToken(token).getEmail();
            }
            roleResponsabilityService.deleteRoleResponsability(roleResponsabilityId,updateUser);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (RuntimeException ex){
            throw ex;
        } 
    }
    
    @Operation(summary = "Search RoleResponsabilityLog by RoleResponsabilityLog Attributes", description = "This service retrieve RoleResponsabilityLog information filter by RoleResponsability Attributes", tags = { "roleResponsability" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RoleResponsability.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/log/search", method = RequestMethod.POST)
    ResponseEntity<PagedResponse<RoleResponsabilityLog>> getRoleResponsabilityLog(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
        @RequestBody(required = true) @Parameter(description="RoleResponsabilityLog object - json") RoleResponsabilityLog roleResponsabilityLog,
        @RequestParam(value = "page", required = false, defaultValue = "0") @Parameter(description="Page to retrieve") Integer page,
        @RequestParam(value = "pageSize", required = false, defaultValue = "10") @Parameter(description="Page size to retrieve") Integer pageSize) throws BadRequestException, EntityNotExistentException, NoAccessGrantedException  {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_ACCESS);
            }
            Paging paging = new Paging(page, pageSize);
            return new ResponseEntity<>(roleResponsabilityLogService.getRoleResponsabilityLog(roleResponsabilityLog,paging), HttpStatus.OK);
        } catch (EntityNotExistentException | NoAccessGrantedException ex) {
            throw ex;
        }
    }
    
    @Operation(summary = "Search RoleResponsabilityLog by RoleResponsabilityLog Id", description = "This service retrieve RoleResponsabilityLog information filter by RoleResponsabilityLog Id", tags = { "roleResponsability" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = RoleResponsability.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/log/{id}", method = RequestMethod.GET)
    public RoleResponsabilityLog getLogById(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                     @PathVariable(value = "id") @Parameter(description="RoleResponsability Id - UUID") UUID roleResponsabilityLogId) throws Exception, EntityNotExistentException, NoAccessGrantedException {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_ACCESS);
            }
            return roleResponsabilityLogService.getById(roleResponsabilityLogId);
        } catch (EntityNotExistentException | NoAccessGrantedException ex) {
            throw ex;
        }  
    }

    
    
}
