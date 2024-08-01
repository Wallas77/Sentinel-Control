/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.repositories;

import com.digivalle.sentinel.models.User;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Waldir.Valle
 */
public interface UserRepository extends JpaRepository<User, UUID>{
    @Override
    User getById(UUID id);
    User getBySerial(Integer serial);
    User getByName(String name);
    User getByNameAndDeleted(String name, Boolean deleted);
    User getByNameAndDeletedAndActive(String name, Boolean deleted, Boolean active);
    List<User> getByEmail(String email);
    List<User> getByEmailOrName(String email, String name);
    List<User> findByNameIgnoreCaseContaining(String name);
    List<User> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted);
    Page<User> findByNameIgnoreCaseContaining(String name, Pageable pageRequest);
    Page<User> findByNameIgnoreCaseContainingAndProfile_NameIgnoreCaseContaining(String name,String nameProfile, Pageable pageRequest);
    Page<User> findByNameIgnoreCaseContainingAndDeleted(String name, Boolean deleted, Pageable pageRequest);
    Page<User> findByNameIgnoreCaseContainingAndDeletedAndProfile_NameIgnoreCaseContaining(String name, Boolean deleted, String nameProfile, Pageable pageRequest);
    List<User> findByEmailIgnoreCaseContaining(String email);
    List<User> findByEmailIgnoreCaseContainingAndDeleted(String email, Boolean deleted);
    List<User> findByEmailIgnoreCaseContainingAndDeletedAndActive(String email, Boolean deleted, Boolean Active);
    Page<User> findByEmailIgnoreCaseContaining(String email, Pageable pageRequest);
    Page<User> findByEmailIgnoreCaseContainingAndDeleted(String email, Boolean deleted, Pageable pageRequest);
}
