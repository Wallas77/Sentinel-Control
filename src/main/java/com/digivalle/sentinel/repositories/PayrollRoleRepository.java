/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.Payroll;
import com.digivalle.sentinel.models.PayrollRole;
import com.digivalle.sentinel.models.Role;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface PayrollRoleRepository extends JpaRepository<PayrollRole, UUID>{
    
    List<PayrollRole> findByPayrollAndRoleAndActiveAndDeleted(Payroll payroll, Role role, Boolean Active, Boolean deleted);
    List<PayrollRole> findByPayrollAndActiveAndDeleted(Payroll payroll, Boolean Active, Boolean deleted);
   
}
