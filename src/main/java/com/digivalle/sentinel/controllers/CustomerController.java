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
import com.digivalle.sentinel.models.Customer;
import com.digivalle.sentinel.models.CustomerLog;
import com.digivalle.sentinel.services.CustomerLogService;
import com.digivalle.sentinel.services.CustomerService;
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
@RequestMapping("customer")

public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private CustomerLogService customerLogService;
    
    @Autowired
    private SecurityService securityService;
    
    
    
    @Operation(summary = "Search Customer by Customer Attributes", description = "This service retrieve Customer information filter by Customer Attributes", tags = { "customer" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Customer.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/search", method = RequestMethod.POST)
    ResponseEntity<PagedResponse<Customer>> getCustomer(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
            @RequestBody(required = true) @Parameter(description="Customer object - json") Customer customer,
        @RequestParam(value = "page", required = false, defaultValue = "0") @Parameter(description="Page to retrieve") Integer page,
        @RequestParam(value = "pageSize", required = false, defaultValue = "10")  @Parameter(description="Page size to retrieve") Integer pageSize) throws BadRequestException, EntityNotExistentException, NoAccessGrantedException  {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_CUSTOMERS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_CUSTOMERS,Definitions.GRANT_ACCESS);
            }
            Paging paging = new Paging(page, pageSize);
            return new ResponseEntity<>(customerService.getCustomer(customer,paging), HttpStatus.OK);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }
    
    @Operation(summary = "Search Customer by Customer Id", description = "This service retrieve Customer information filter by Customer Id", tags = { "customer" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Customer.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Customer getById(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                     @PathVariable(value = "id") @Parameter(description="Customer Id - UUID") UUID customerId) throws EntityNotExistentException, BadRequestException, NoAccessGrantedException  {
       
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_CUSTOMERS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_CUSTOMERS,Definitions.GRANT_ACCESS);
            }
            return customerService.getById(customerId);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        } 
        
    }
    
    @Operation(summary = "Create Customer", description = "This service create a new Customer Object", tags = { "customer" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Customer.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Customer> createCustomer(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
            @Valid @RequestBody(required = true) @Parameter(description="Customer object - json") Customer customer) throws BusinessLogicException, ExistentEntityException, BadRequestException, EntityNotExistentException, NoAccessGrantedException {
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_CUSTOMERS, Definitions.GRANT_CREATE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_CUSTOMERS,Definitions.GRANT_CREATE);
            }
            if(customer.getUpdateUser()==null){
                customer.setUpdateUser(securityService.getUserByToken(token).getName());
            }
            return new ResponseEntity<>(customerService.createCustomer(customer), HttpStatus.CREATED);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        } 
        
    }
    
    @Operation(summary = "Update Customer", description = "This service updates a persited Customer Object", tags = { "customer" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Customer.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<Customer> updateCustomer(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                 @PathVariable(value = "id") @Parameter(description="Customer Id - UUID") UUID customerId,
                                                 @Valid @RequestBody(required = true) @Parameter(description="Customer object - json") Customer customer, BindingResult bindingResult) throws BusinessLogicException, BadRequestException, EntityNotExistentException, ExistentEntityException, NoAccessGrantedException {
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_CUSTOMERS, Definitions.GRANT_UPDATE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_CUSTOMERS,Definitions.GRANT_UPDATE);
            }
            if(customer.getUpdateUser()==null){
                customer.setUpdateUser(securityService.getUserByToken(token).getName());
            }
            return new ResponseEntity<>(customerService.updateCustomer(customerId, customer), HttpStatus.OK);
        }catch (Exception ble) {
            throw new BadRequestException(ble.getMessage());
            //throw ble;
        }
        
 
    }
    
    @Operation(summary = "Delete Customer", description = "This service deletes (Logically) a persited Customer Object", tags = { "customer" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Customer.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Boolean> deleteCustomer(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                 @PathVariable(value = "id") @Parameter(description="Customer Id - UUID") UUID customerId,
                                                 @RequestParam(value = "updateUser") @Parameter(description="name of update User") String updateUser) throws Exception, BusinessLogicException, EntityNotExistentException, ExistentEntityException, NoAccessGrantedException {
        try{
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_CUSTOMERS, Definitions.GRANT_DELETE)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_CUSTOMERS,Definitions.GRANT_DELETE);
            }
            if(updateUser==null){
                updateUser =securityService.getUserByToken(token).getName();
            }
            customerService.deleteCustomer(customerId,updateUser);
            return new ResponseEntity<>(HttpStatus.ACCEPTED);
        } catch (RuntimeException ex){
            throw new BadRequestException(ex.getMessage());
        } 
    }
    
    @Operation(summary = "Search CustomerLog by CustomerLog Attributes", description = "This service retrieve CustomerLog information filter by Customer Attributes", tags = { "customer" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Customer.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/log/search", method = RequestMethod.POST)
    ResponseEntity<PagedResponse<CustomerLog>> getCustomerLog(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
        @RequestBody(required = true) @Parameter(description="CustomerLog object - json") CustomerLog customerLog,
        @RequestParam(value = "page", required = false, defaultValue = "0") @Parameter(description="Page to retrieve") Integer page,
        @RequestParam(value = "pageSize", required = false, defaultValue = "10") @Parameter(description="Page size to retrieve") Integer pageSize) throws BadRequestException, EntityNotExistentException, NoAccessGrantedException  {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_CUSTOMERS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_CUSTOMERS,Definitions.GRANT_ACCESS);
            }
            Paging paging = new Paging(page, pageSize);
            return new ResponseEntity<>(customerLogService.getCustomerLog(customerLog,paging), HttpStatus.OK);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }
    
    @Operation(summary = "Search CustomerLog by CustomerLog Id", description = "This service retrieve CustomerLog information filter by CustomerLog Id", tags = { "customer" })
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "successful operation", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Customer.class)))),
        @ApiResponse(responseCode = "400", description = "bad request", content = @Content(array = @ArraySchema(schema = @Schema(implementation = ErrorDetails.class))))
    })
    @RequestMapping(value = "/log/{id}", method = RequestMethod.GET)
    public CustomerLog getLogById(@RequestHeader(value = "token", required = true) @Parameter(description="Sentinel Control Token - UUID") String token,
                                                     @PathVariable(value = "id") @Parameter(description="Customer Id - UUID") UUID customerLogId) throws Exception, EntityNotExistentException, NoAccessGrantedException {
        try {
            if(!securityService.getGrantAndModule(token, Definitions.MODULE_SENTINEL_CUSTOMERS, Definitions.GRANT_ACCESS)){
                throw new NoAccessGrantedException(Definitions.MODULE_SENTINEL_CUSTOMERS,Definitions.GRANT_ACCESS);
            }
            return customerLogService.getById(customerLogId);
        } catch (Exception ex) {
            throw new BadRequestException(ex.getMessage());
        }  
    }

    
    
}
