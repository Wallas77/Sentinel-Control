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
import com.digivalle.sentinel.models.EmployeeWorkExperience;
import com.digivalle.sentinel.models.EmployeeWorkExperienceLog;
import com.digivalle.sentinel.services.EmployeeWorkExperienceLogService;
import com.digivalle.sentinel.services.EmployeeWorkExperienceService;
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
@RequestMapping("employee/workExperience")

public class EmployeeWorkExperienceController {
    
    @Autowired
    private EmployeeWorkExperienceService employeeWorkExperienceService;
    
    @Autowired
    private EmployeeWorkExperienceLogService employeeWorkExperienceLogService;
    
    @Autowired
    private SecurityService securityService;
    
    @Operation(summary = "Search EmployeeWorkExperience by EmployeeWorkExperience Attributes", description = "This service retrieve EmployeeWorkExperience information filter by EmployeeWorkExperience Attributes", tags = { "employeeWorkExperience" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeWorkExperience.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    ResponseEntity<PagedResponse<EmployeeWorkExperience>> getEmployeeWorkExperience(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
            @RequestBody(required = true) @Parameter(description="EmployeeWorkExperience object - json") EmployeeWorkExperience employeeWorkExperience,
        @RequestParam(value = "page", required = false, defaultValue = "0") @Parameter(description="Page to retrieve") Integer page,
        @RequestParam(value = "pageSize", required = false, defaultValue = "10")  @Parameter(description="Page size to retrieve") Integer pageSize) throws BadRequestException, EntityNotExistentException, NoAccessGrantedException  {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_ACCESS);
            }
            Paging paging = new Paging(page, pageSize);
            return new ResponseEntity<>(employeeWorkExperienceService.getEmployeeWorkExperience(employeeWorkExperience,paging), HttpStatus.OK);
        } catch (EntityNotExistentException | NoAccessGrantedException ex) {
            throw ex;
        }
    }
    
    @Operation(summary = "Search EmployeeWorkExperience by EmployeeWorkExperience Id", description = "This service retrieve EmployeeWorkExperience information filter by EmployeeWorkExperience Id", tags = { "employeeWorkExperience" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeWorkExperience.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public EmployeeWorkExperience getById(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                     @PathVariable(value = "id") @Parameter(description="EmployeeWorkExperience Id - UUID") UUID employeeWorkExperienceId) throws EntityNotExistentException, BadRequestException, NoAccessGrantedException  {
       
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_ACCESS);
            }
            return employeeWorkExperienceService.getById(employeeWorkExperienceId);
        } catch (EntityNotExistentException | NoAccessGrantedException ex) {
            throw ex;
        } 
        
    }
    
    @Operation(summary = "Create EmployeeWorkExperience", description = "This service create a new EmployeeWorkExperience Object", tags = { "employeeWorkExperience" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeWorkExperience.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<EmployeeWorkExperience> createEmployeeWorkExperience(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
            @Valid @RequestBody(required = true) @Parameter(description="EmployeeWorkExperience object - json") EmployeeWorkExperience employeeWorkExperience) throws BusinessLogicException, ExistentEntityException, BadRequestException, EntityNotExistentException, NoAccessGrantedException {
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_CREATE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_CREATE);
            }
            if(employeeWorkExperience.getUpdateUser()==null){
                employeeWorkExperience.setUpdateUser(securityService.getUserByToken(token).getEmail());
            }
            return new ResponseEntity<>(employeeWorkExperienceService.createEmployeeWorkExperience(employeeWorkExperience), HttpStatus.CREATED);
        } catch (BusinessLogicException | EntityNotExistentException | ExistentEntityException | NoAccessGrantedException ex) {
            throw ex;
        } 
        
    }
    
    @Operation(summary = "Update EmployeeWorkExperience", description = "This service updates a persited EmployeeWorkExperience Object", tags = { "employeeWorkExperience" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeWorkExperience.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<EmployeeWorkExperience> updateEmployeeWorkExperience(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                 @PathVariable(value = "id") @Parameter(description="EmployeeWorkExperience Id - UUID") UUID employeeWorkExperienceId,
                                                 @Valid @RequestBody(required = true) @Parameter(description="EmployeeWorkExperience object - json") EmployeeWorkExperience employeeWorkExperience, BindingResult bindingResult) throws BusinessLogicException, BadRequestException, EntityNotExistentException, ExistentEntityException, NoAccessGrantedException {
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_UPDATE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_UPDATE);
            }
            if(employeeWorkExperience.getUpdateUser()==null){
                employeeWorkExperience.setUpdateUser(securityService.getUserByToken(token).getEmail());
            }
            return new ResponseEntity<>(employeeWorkExperienceService.updateEmployeeWorkExperience(employeeWorkExperienceId, employeeWorkExperience), HttpStatus.OK);
        }catch (BusinessLogicException | EntityNotExistentException | ExistentEntityException | NoAccessGrantedException ex) {
            throw ex;
        }
        
 
    }
    
    @Operation(summary = "Delete EmployeeWorkExperience", description = "This service deletes (Logically) a persited EmployeeWorkExperience Object", tags = { "employeeWorkExperience" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeWorkExperience.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteEmployeeWorkExperience(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                 @PathVariable(value = "id") @Parameter(description="EmployeeWorkExperience Id - UUID") UUID employeeWorkExperienceId,
                                                 @RequestParam(value = "updateUser",required = false) @Parameter(description="name of update User") String updateUser) throws Exception, BusinessLogicException, EntityNotExistentException, ExistentEntityException, NoAccessGrantedException {
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_DELETE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_DELETE);
            }
            if(updateUser==null){
                updateUser=securityService.getUserByToken(token).getEmail();
            }
            employeeWorkExperienceService.deleteEmployeeWorkExperience(employeeWorkExperienceId,updateUser);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (RuntimeException ex){
            throw ex;
        } 
    }
    
    @Operation(summary = "Search EmployeeWorkExperienceLog by EmployeeWorkExperienceLog Attributes", description = "This service retrieve EmployeeWorkExperienceLog information filter by EmployeeWorkExperience Attributes", tags = { "employeeWorkExperience" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeWorkExperience.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/log/search", method = RequestMethod.POST)
    ResponseEntity<PagedResponse<EmployeeWorkExperienceLog>> getEmployeeWorkExperienceLog(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
        @RequestBody(required = true) @Parameter(description="EmployeeWorkExperienceLog object - json") EmployeeWorkExperienceLog employeeWorkExperienceLog,
        @RequestParam(value = "page", required = false, defaultValue = "0") @Parameter(description="Page to retrieve") Integer page,
        @RequestParam(value = "pageSize", required = false, defaultValue = "10") @Parameter(description="Page size to retrieve") Integer pageSize) throws BadRequestException, EntityNotExistentException, NoAccessGrantedException  {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_ACCESS);
            }
            Paging paging = new Paging(page, pageSize);
            return new ResponseEntity<>(employeeWorkExperienceLogService.getEmployeeWorkExperienceLog(employeeWorkExperienceLog,paging), HttpStatus.OK);
        } catch (EntityNotExistentException | NoAccessGrantedException ex) {
            throw ex;
        }
    }
    
    @Operation(summary = "Search EmployeeWorkExperienceLog by EmployeeWorkExperienceLog Id", description = "This service retrieve EmployeeWorkExperienceLog information filter by EmployeeWorkExperienceLog Id", tags = { "employeeWorkExperience" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = EmployeeWorkExperience.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/log/{id}", method = RequestMethod.GET)
    public EmployeeWorkExperienceLog getLogById(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                     @PathVariable(value = "id") @Parameter(description="EmployeeWorkExperience Id - UUID") UUID employeeWorkExperienceLogId) throws Exception, EntityNotExistentException, NoAccessGrantedException {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_ACCESS);
            }
            return employeeWorkExperienceLogService.getById(employeeWorkExperienceLogId);
        } catch (EntityNotExistentException | NoAccessGrantedException ex) {
            throw ex;
        }  
    }

    
    
}
