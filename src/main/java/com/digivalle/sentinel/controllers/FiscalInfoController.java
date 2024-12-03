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
import com.digivalle.sentinel.models.FiscalInfo;
import com.digivalle.sentinel.models.FiscalInfoLog;
import com.digivalle.sentinel.services.FiscalInfoLogService;
import com.digivalle.sentinel.services.FiscalInfoService;
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
@RequestMapping("fiscalInfo")

public class FiscalInfoController {
    
    @Autowired
    private FiscalInfoService fiscalInfoService;
    
    @Autowired
    private FiscalInfoLogService fiscalInfoLogService;
    
    @Autowired
    private SecurityService securityService;
    
    
    
    @Operation(summary = "Search FiscalInfo by FiscalInfo Attributes", description = "This service retrieve FiscalInfo information filter by FiscalInfo Attributes", tags = { "fiscalInfo" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = FiscalInfo.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    ResponseEntity<PagedResponse<FiscalInfo>> getFiscalInfo(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
            @RequestBody(required = true) @Parameter(description="FiscalInfo object - json") FiscalInfo fiscalInfo,
        @RequestParam(value = "page", required = false, defaultValue = "0") @Parameter(description="Page to retrieve") Integer page,
        @RequestParam(value = "pageSize", required = false, defaultValue = "10")  @Parameter(description="Page size to retrieve") Integer pageSize) throws BadRequestException, EntityNotExistentException, NoAccessGrantedException  {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_FISCAL_INFO, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_FISCAL_INFO,Definitions.GRANT_ACCESS);
            }
            Paging paging = new Paging(page, pageSize);
            return new ResponseEntity<>(fiscalInfoService.getFiscalInfo(fiscalInfo,paging), HttpStatus.OK);
        } catch (EntityNotExistentException | NoAccessGrantedException ex) {
            throw ex;
        }
    }
    
    @Operation(summary = "Search FiscalInfo by FiscalInfo Id", description = "This service retrieve FiscalInfo information filter by FiscalInfo Id", tags = { "fiscalInfo" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = FiscalInfo.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public FiscalInfo getById(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                     @PathVariable(value = "id") @Parameter(description="FiscalInfo Id - UUID") UUID fiscalInfoId) throws EntityNotExistentException, BadRequestException, NoAccessGrantedException  {
       
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_FISCAL_INFO, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_FISCAL_INFO,Definitions.GRANT_ACCESS);
            }
            return fiscalInfoService.getById(fiscalInfoId);
        } catch (EntityNotExistentException | NoAccessGrantedException ex) {
            throw ex;
        } 
        
    }
    
    @Operation(summary = "Create FiscalInfo", description = "This service create a new FiscalInfo Object", tags = { "fiscalInfo" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = FiscalInfo.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<FiscalInfo> createFiscalInfo(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
            @Valid @RequestBody(required = true) @Parameter(description="FiscalInfo object - json") FiscalInfo fiscalInfo) throws BusinessLogicException, ExistentEntityException, BadRequestException, EntityNotExistentException, NoAccessGrantedException {
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_FISCAL_INFO, Definitions.GRANT_CREATE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_FISCAL_INFO,Definitions.GRANT_CREATE);
            }
            if(fiscalInfo.getUpdateUser()==null){
                fiscalInfo.setUpdateUser(securityService.getUserByToken(token).getEmail());
            }
            return new ResponseEntity<>(fiscalInfoService.createFiscalInfo(fiscalInfo), HttpStatus.CREATED);
        } catch (BusinessLogicException | EntityNotExistentException | ExistentEntityException | NoAccessGrantedException ex) {
            throw ex;
        } 
        
    }
    
    @Operation(summary = "Update FiscalInfo", description = "This service updates a persited FiscalInfo Object", tags = { "fiscalInfo" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = FiscalInfo.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<FiscalInfo> updateFiscalInfo(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                 @PathVariable(value = "id") @Parameter(description="FiscalInfo Id - UUID") UUID fiscalInfoId,
                                                 @Valid @RequestBody(required = true) @Parameter(description="FiscalInfo object - json") FiscalInfo fiscalInfo, BindingResult bindingResult) throws BusinessLogicException, BadRequestException, EntityNotExistentException, ExistentEntityException, NoAccessGrantedException {
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_FISCAL_INFO, Definitions.GRANT_UPDATE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_FISCAL_INFO,Definitions.GRANT_UPDATE);
            }
            if(fiscalInfo.getUpdateUser()==null){
                fiscalInfo.setUpdateUser(securityService.getUserByToken(token).getEmail());
            }
            return new ResponseEntity<>(fiscalInfoService.updateFiscalInfo(fiscalInfoId, fiscalInfo), HttpStatus.OK);
        }catch (BusinessLogicException | EntityNotExistentException | ExistentEntityException | NoAccessGrantedException ex) {
            throw ex;
        }
        
 
    }
    
    @Operation(summary = "Delete FiscalInfo", description = "This service deletes (Logically) a persited FiscalInfo Object", tags = { "fiscalInfo" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = FiscalInfo.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteFiscalInfo(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                 @PathVariable(value = "id") @Parameter(description="FiscalInfo Id - UUID") UUID fiscalInfoId,
                                                 @RequestParam(value = "updateUser") @Parameter(description="name of update User") String updateUser) throws Exception, BusinessLogicException, EntityNotExistentException, ExistentEntityException, NoAccessGrantedException {
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_FISCAL_INFO, Definitions.GRANT_DELETE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_FISCAL_INFO,Definitions.GRANT_DELETE);
            }
            if(updateUser==null){
                updateUser =securityService.getUserByToken(token).getEmail();
            }
            fiscalInfoService.deleteFiscalInfo(fiscalInfoId,updateUser);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (RuntimeException ex){
            throw ex;
        } 
    }
    
    @Operation(summary = "Search FiscalInfoLog by FiscalInfoLog Attributes", description = "This service retrieve FiscalInfoLog information filter by FiscalInfo Attributes", tags = { "fiscalInfo" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = FiscalInfo.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/log/search", method = RequestMethod.POST)
    ResponseEntity<PagedResponse<FiscalInfoLog>> getFiscalInfoLog(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
        @RequestBody(required = true) @Parameter(description="FiscalInfoLog object - json") FiscalInfoLog fiscalInfoLog,
        @RequestParam(value = "page", required = false, defaultValue = "0") @Parameter(description="Page to retrieve") Integer page,
        @RequestParam(value = "pageSize", required = false, defaultValue = "10") @Parameter(description="Page size to retrieve") Integer pageSize) throws BadRequestException, EntityNotExistentException, NoAccessGrantedException  {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_FISCAL_INFO, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_FISCAL_INFO,Definitions.GRANT_ACCESS);
            }
            Paging paging = new Paging(page, pageSize);
            return new ResponseEntity<>(fiscalInfoLogService.getFiscalInfoLog(fiscalInfoLog,paging), HttpStatus.OK);
        } catch (EntityNotExistentException | NoAccessGrantedException ex) {
            throw ex;
        }
    }
    
    @Operation(summary = "Search FiscalInfoLog by FiscalInfoLog Id", description = "This service retrieve FiscalInfoLog information filter by FiscalInfoLog Id", tags = { "fiscalInfo" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = FiscalInfo.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/log/{id}", method = RequestMethod.GET)
    public FiscalInfoLog getLogById(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                     @PathVariable(value = "id") @Parameter(description="FiscalInfo Id - UUID") UUID fiscalInfoLogId) throws Exception, EntityNotExistentException, NoAccessGrantedException {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_FISCAL_INFO, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_FISCAL_INFO,Definitions.GRANT_ACCESS);
            }
            return fiscalInfoLogService.getById(fiscalInfoLogId);
        } catch (EntityNotExistentException | NoAccessGrantedException ex) {
            throw ex;
        }  
    }

    
    
}
