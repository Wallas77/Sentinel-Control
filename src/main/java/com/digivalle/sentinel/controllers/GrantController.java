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
import com.digivalle.sentinel.models.Grant;
import com.digivalle.sentinel.models.GrantLog;
import com.digivalle.sentinel.services.GrantLogService;
import com.digivalle.sentinel.services.GrantService;
import com.digivalle.sentinel.services.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.UUID;
import javax.validation.Valid;
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
@RequestMapping("grant")

public class GrantController {
    
    @Autowired
    private GrantService grantService;
    
    @Autowired
    private GrantLogService grantLogService;
    
    @Autowired
    private SecurityService securityService;
    
    @Operation(summary = "Search Grant by Grant Attributes", description = "This service retrieve Grant information filter by Grant Attributes", tags = { "grant" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Grant.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    ResponseEntity<PagedResponse<Grant>> getGrant(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
            @RequestBody(required = true) @Parameter(description="Grant object - json") Grant grant,
        @RequestParam(value = "page", required = false, defaultValue = "0") @Parameter(description="Page to retrieve") Integer page,
        @RequestParam(value = "pageSize", required = false, defaultValue = "10")  @Parameter(description="Page size to retrieve") Integer pageSize) throws BadRequestException, EntityNotExistentException, NoAccessGrantedException  {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_GRANTS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_GRANTS,Definitions.GRANT_ACCESS);
            }
            Paging paging = new Paging(page, pageSize);
            return new ResponseEntity<>(grantService.getGrant(grant,paging), HttpStatus.OK);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }
    
    @Operation(summary = "Search Grant by Grant Id", description = "This service retrieve Grant information filter by Grant Id", tags = { "grant" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Grant.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Grant getById(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                     @PathVariable(value = "id") @Parameter(description="Grant Id - UUID") UUID grantId) throws EntityNotExistentException, BadRequestException, NoAccessGrantedException  {
       
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_GRANTS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_GRANTS,Definitions.GRANT_ACCESS);
            }
            return grantService.getById(grantId);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        } 
        
    }
    
    @Operation(summary = "Create Grant", description = "This service create a new Grant Object", tags = { "grant" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Grant.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Grant> createGrant(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
            @Valid @RequestBody(required = true) @Parameter(description="Grant object - json") Grant grant) throws BusinessLogicException, ExistentEntityException, BadRequestException, EntityNotExistentException, NoAccessGrantedException {
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_GRANTS, Definitions.GRANT_CREATE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_GRANTS,Definitions.GRANT_CREATE);
            }
            if(grant.getUpdateUser()==null){
                grant.setUpdateUser(securityService.getUserByToken(token).getName());
            }
            return new ResponseEntity<>(grantService.createGrant(grant), HttpStatus.CREATED);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        } 
        
    }
    
    @Operation(summary = "Update Grant", description = "This service updates a persited Grant Object", tags = { "grant" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Grant.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<Grant> updateGrant(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                 @PathVariable(value = "id") @Parameter(description="Grant Id - UUID") UUID grantId,
                                                 @Valid @RequestBody(required = true) @Parameter(description="Grant object - json") Grant grant, BindingResult bindingResult) throws BusinessLogicException, BadRequestException, EntityNotExistentException, ExistentEntityException, NoAccessGrantedException {
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_GRANTS, Definitions.GRANT_UPDATE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_GRANTS,Definitions.GRANT_UPDATE);
            }
            if(grant.getUpdateUser()==null){
                grant.setUpdateUser(securityService.getUserByToken(token).getName());
            }
            return new ResponseEntity<>(grantService.updateGrant(grantId, grant), HttpStatus.OK);
        }catch (Exception ble) {
            throw new BadRequestException(ble.getMessage());
            //throw ble;
        }
        
 
    }
    
    @Operation(summary = "Delete Grant", description = "This service deletes (Logically) a persited Grant Object", tags = { "grant" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Grant.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteGrant(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                 @PathVariable(value = "id") @Parameter(description="Grant Id - UUID") UUID grantId,
                                                 @RequestParam(value = "updateUser") @Parameter(description="name of update User") String updateUser) throws Exception, BusinessLogicException, EntityNotExistentException, ExistentEntityException, NoAccessGrantedException {
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_GRANTS, Definitions.GRANT_DELETE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_GRANTS,Definitions.GRANT_DELETE);
            }
            if(updateUser==null){
                updateUser =securityService.getUserByToken(token).getName();
            }
            grantService.deleteGrant(grantId,updateUser);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (RuntimeException ex){
            throw new BadRequestException(ex.getMessage());
        } 
    }
    
    @Operation(summary = "Search GrantLog by GrantLog Attributes", description = "This service retrieve GrantLog information filter by Grant Attributes", tags = { "grant" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Grant.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/log/search", method = RequestMethod.POST)
    ResponseEntity<PagedResponse<GrantLog>> getGrantLog(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
        @RequestBody(required = true) @Parameter(description="GrantLog object - json") GrantLog grantLog,
        @RequestParam(value = "page", required = false, defaultValue = "0") @Parameter(description="Page to retrieve") Integer page,
        @RequestParam(value = "pageSize", required = false, defaultValue = "10") @Parameter(description="Page size to retrieve") Integer pageSize) throws BadRequestException, EntityNotExistentException, NoAccessGrantedException  {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_GRANTS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_GRANTS,Definitions.GRANT_ACCESS);
            }
            Paging paging = new Paging(page, pageSize);
            return new ResponseEntity<>(grantLogService.getGrantLog(grantLog,paging), HttpStatus.OK);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }
    
    @Operation(summary = "Search GrantLog by GrantLog Id", description = "This service retrieve GrantLog information filter by GrantLog Id", tags = { "grant" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Grant.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/log/{id}", method = RequestMethod.GET)
    public GrantLog getLogById(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                     @PathVariable(value = "id") @Parameter(description="Grant Id - UUID") UUID grantLogId) throws Exception, EntityNotExistentException, NoAccessGrantedException {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_GRANTS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_GRANTS,Definitions.GRANT_ACCESS);
            }
            return grantLogService.getById(grantLogId);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        }  
    }

    
    
}
