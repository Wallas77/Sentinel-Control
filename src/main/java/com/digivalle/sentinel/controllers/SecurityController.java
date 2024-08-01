/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.controllers;

import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.LoginRequest;
import com.digivalle.sentinel.exceptions.AuthenticationException;
import com.digivalle.sentinel.exceptions.BadRequestException;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.EntityNotFoundException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.exceptions.handler.model.ErrorDetails;
import com.digivalle.sentinel.models.Module;
import com.digivalle.sentinel.models.ProfileModuleGrant;
import com.digivalle.sentinel.models.Token;
import com.digivalle.sentinel.models.User;
import com.digivalle.sentinel.services.SecurityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("security")
public class SecurityController {
    
    @Autowired
    private SecurityService securityService;
    
    @Operation(summary = "Initialize Sentinel Control", description = "This service initialize the Zwark PA Catalogues application", tags = { "security" })
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Module.class)))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/initialize", method = RequestMethod.GET)
    public String initialize(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token) throws Exception, EntityNotFoundException, ExistentEntityException {
        try {
            return securityService.initialize();
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        }  
    }
    
    @Operation(summary = "Login Sentinel Control", description = "This service login to the Zwark PA application", tags = { "security" })
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Module.class)))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Token login(@RequestBody(required = true) @Parameter(description="LoginRequest object - json") LoginRequest loginRequest) throws Exception, ExistentEntityException, BusinessLogicException, EntityNotExistentException {
        try {
            if(loginRequest.getSessionTime()==null){
                loginRequest.setSessionTime(Definitions.DEFAULT_SESSION_TIME);
            }
            return securityService.login(loginRequest.getUsername(), loginRequest.getPassword(), loginRequest.getSessionTime());
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        }  
    } 
    
    @Operation(summary = "getGrantAnModule Sentinel Control", description = "This service returns if a User has the module and grant combination assigned in the Sentinel Control application", tags = { "security" })
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Module.class)))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/getGrantAndModule", method = RequestMethod.POST)
    public Boolean getGrantAndModule(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
            @RequestParam(value = "module", required = true) @Parameter(description="Sentinel Control Module name") String module,
            @RequestParam(value = "grant", required = true) @Parameter(description="Sentinel Control Grant name") String grant) throws Exception, ExistentEntityException, BusinessLogicException, EntityNotExistentException, AuthenticationException {
        try {
            
            return securityService.getGrantAndModule(token, module, grant);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        }  
    }
    
    @Operation(summary = "getUserByToken Sentinel Control", description = "This service returns the User by token assigned in the Sentinel Control application", tags = { "security" })
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Module.class)))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/getUserByToken", method = RequestMethod.GET)
    public User getUserByToken(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token) throws Exception, ExistentEntityException, BusinessLogicException, EntityNotExistentException, AuthenticationException {
        try {
            
            return securityService.getUserByToken(token);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        }  
    }
    
    @Operation(summary = "getGrantsByApplication Sentinel Control", description = "This service returns the User grants assigned in the Sentinel Control application by application", tags = { "security" })
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Module.class)))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/getGrantsByApplication", method = RequestMethod.POST)
    public List<ProfileModuleGrant> getGrantsByApplication(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
            @RequestParam(value = "application", required = true) @Parameter(description="Sentinel Control Application name") String application) throws Exception, ExistentEntityException, BusinessLogicException, EntityNotExistentException, AuthenticationException {
        try {
            
            return securityService.getGrantsByApplication(token,application);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        }  
    }
    
        
}
