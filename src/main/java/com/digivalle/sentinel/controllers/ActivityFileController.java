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
import com.digivalle.sentinel.models.Activity;
import com.digivalle.sentinel.models.ActivityFile;
import com.digivalle.sentinel.models.ActivityFileLog;
import com.digivalle.sentinel.services.ActivityFileService;
import com.digivalle.sentinel.services.ActivityFileLogService;
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
@RequestMapping("activityFile")

public class ActivityFileController {
    
    @Autowired
    private ActivityFileService activityFileService;
    
    @Autowired
    private ActivityFileLogService activityFileLogService;
    
    @Autowired
    private SecurityService securityService;
    
    @Operation(summary = "Search ActivityFile by ActivityFile Attributes", description = "This service retrieve ActivityFile information filter by ActivityFile Attributes", tags = { "activityFile" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActivityFile.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    ResponseEntity<PagedResponse<ActivityFile>> getActivityFile(@RequestHeader(value = "token", required = true) @Parameter(description="zwarkForce Token - UUID") String token,
            @RequestBody(required = true) @Parameter(description="ActivityFile object - json") ActivityFile activityFile,
        @RequestParam(value = "page", required = false, defaultValue = "0") @Parameter(description="Page to retrieve") Integer page,
        @RequestParam(value = "pageSize", required = false, defaultValue = "10")  @Parameter(description="Page size to retrieve") Integer pageSize) throws BadRequestException, EntityNotExistentException, NoAccessGrantedException  {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_ACCESS);
            }
            Paging paging = new Paging(page, pageSize);
            return new ResponseEntity<>(activityFileService.getActivityFile(activityFile,paging), HttpStatus.OK);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }
    
    @Operation(summary = "Search ActivityFile by ActivityFile Id", description = "This service retrieve ActivityFile information filter by ActivityFile Id", tags = { "activityFile" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActivityFile.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ActivityFile getById(@RequestHeader(value = "token", required = true) @Parameter(description="zwarkForce Token - UUID") String token,
                                                     @PathVariable(value = "id") @Parameter(description="ActivityFile Id - UUID") UUID activityFileId) throws EntityNotExistentException, BadRequestException, NoAccessGrantedException  {
       
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_ACCESS);
            }
            return activityFileService.getById(activityFileId);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        } 
        
    }
    
    @Operation(summary = "Create ActivityFile", description = "This service create a new ActivityFile Object", tags = { "activityFile" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActivityFile.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<ActivityFile> createActivityFile(@RequestHeader(value = "token", required = true) @Parameter(description="zwarkForce Token - UUID") String token,
            @RequestParam(value = "file", required = true) @Parameter(description="File to create") MultipartFile file,
            @RequestParam(value = "activityId", required = true) @Parameter(description="Id of Activity to create") UUID activityId,
            @RequestParam(value = "name", required = true) @Parameter(description="Name of Activity to create") String name,
            @RequestParam(value = "active", required = false) @Parameter(description="active value for ActivityFile") Boolean active) throws BusinessLogicException, ExistentEntityException, BadRequestException, EntityNotExistentException, NoAccessGrantedException {
        try{
            if(active==null){
                active=Boolean.TRUE;
            }
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_CREATE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_CREATE);
            }
            Activity activity = new Activity();
            activity.setId(activityId);
            ActivityFile activityFile = new ActivityFile(activity, name, file.getBytes());
            activityFile.setActive(active);
            activityFile.setUpdateUser(securityService.getUserByToken(token).getName());
            return new ResponseEntity<>(activityFileService.createActivityFile(activityFile), HttpStatus.CREATED);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        } 
        
    }
    
    @Operation(summary = "Update ActivityFile", description = "This service updates a persited ActivityFile Object", tags = { "activityFile" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActivityFile.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<ActivityFile> updateActivityFile(@RequestHeader(value = "token", required = true) @Parameter(description="zwarkForce Token - UUID") String token,
         @PathVariable(value = "id") @Parameter(description="ActivityFile Id - UUID") UUID activityFileId,
         @RequestParam(value = "file", required = true) @Parameter(description="File to create") MultipartFile file,
         @RequestParam(value = "activityId", required = true) @Parameter(description="Id of Activity to create") UUID activityId,
         @RequestParam(value = "name", required = true) @Parameter(description="Name of Activity to create") String name,
         @RequestParam(value = "active", required = false) @Parameter(description="active value for ActivityFile") Boolean active) throws BusinessLogicException, BadRequestException, EntityNotExistentException, ExistentEntityException, NoAccessGrantedException {
        try{
            if(active==null){
                active=Boolean.TRUE;
            }
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_UPDATE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_UPDATE);
            }
            Activity activity = new Activity();
            activity.setId(activityId);
            ActivityFile activityFile = new ActivityFile(activity, name, file.getBytes());
            activityFile.setActive(active);
            activityFile.setUpdateUser(securityService.getUserByToken(token).getName());
            return new ResponseEntity<>(activityFileService.updateActivityFile(activityFileId, activityFile), HttpStatus.OK);
        }catch (Exception ble) {
            throw new BadRequestException(ble.getMessage());
            //throw ble;
        }
        
 
    }
    
    @Operation(summary = "Delete ActivityFile", description = "This service deletes (Logically) a persited ActivityFile Object", tags = { "activityFile" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActivityFile.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteActivityFile(@RequestHeader(value = "token", required = true) @Parameter(description="zwarkForce Token - UUID") String token,
                                                 @PathVariable(value = "id") @Parameter(description="ActivityFile Id - UUID") UUID activityFileId) throws Exception, BusinessLogicException, EntityNotExistentException, ExistentEntityException, NoAccessGrantedException {
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_DELETE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_DELETE);
            }
            activityFileService.deleteActivityFile(activityFileId,securityService.getUserByToken(token).getName());
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (RuntimeException ex){
            throw new BadRequestException(ex.getMessage());
        } 
    }
    
    @Operation(summary = "Search ActivityFileLog by ActivityFileLog Attributes", description = "This service retrieve ActivityFileLog information filter by ActivityFile Attributes", tags = { "activityFile" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActivityFile.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/log/search", method = RequestMethod.POST)
    ResponseEntity<PagedResponse<ActivityFileLog>> getActivityFileLog(@RequestHeader(value = "token", required = true) @Parameter(description="zwarkForce Token - UUID") String token,
        @RequestBody(required = true) @Parameter(description="ActivityFileLog object - json") ActivityFileLog activityFileLog,
        @RequestParam(value = "page", required = false, defaultValue = "0") @Parameter(description="Page to retrieve") Integer page,
        @RequestParam(value = "pageSize", required = false, defaultValue = "10") @Parameter(description="Page size to retrieve") Integer pageSize) throws BadRequestException, EntityNotExistentException, NoAccessGrantedException  {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_ACCESS);
            }
            Paging paging = new Paging(page, pageSize);
            return new ResponseEntity<>(activityFileLogService.getActivityFileLog(activityFileLog,paging), HttpStatus.OK);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }
    
    @Operation(summary = "Search ActivityFileLog by ActivityFileLog Id", description = "This service retrieve ActivityFileLog information filter by ActivityFileLog Id", tags = { "activityFile" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ActivityFile.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/log/{id}", method = RequestMethod.GET)
    public ActivityFileLog getLogById(@RequestHeader(value = "token", required = true) @Parameter(description="zwarkForce Token - UUID") String token,
                                                     @PathVariable(value = "id") @Parameter(description="ActivityFile Id - UUID") UUID activityFileLogId) throws Exception, EntityNotExistentException, NoAccessGrantedException {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_APPLICATIONS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_APPLICATIONS,Definitions.GRANT_ACCESS);
            }
            return activityFileLogService.getById(activityFileLogId);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        }  
    }

    
    
}
