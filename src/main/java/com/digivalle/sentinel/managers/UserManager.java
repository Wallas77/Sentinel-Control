/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.managers;

import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.models.User;
import com.digivalle.sentinel.repositories.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 *
 * @author Waldir.Valle
 */
@Component
public class UserManager {
    
    @Autowired
    private UserRepository userRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public User getById(UUID id) throws EntityNotExistentException {
        Optional<User> user = userRepository.findById(id);
        if (!user.isEmpty()) {
            //user.get().setPassword(null);
            return user.get();
        }
        throw new EntityNotExistentException(User.class,id.toString());
    }
    
    public PagedResponse<User> getUser(User filter, Paging paging){
        
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> root = cq.from(User.class);
        //cq.orderBy(cb.asc(root.get("id")));

        List<Predicate> predicates = new ArrayList<Predicate>();
        cq.orderBy(cb.desc(root.get("creationDate")));
        if(filter.getCreationDate()!=null && filter.getCreationDate2()!=null){
            predicates.add(cb.between(root.get("creationDate"), filter.getCreationDate(),filter.getCreationDate2()));
        }
        if(filter.getUpdateDate()!=null && filter.getUpdateDate2()!=null){
            predicates.add(cb.between(root.get("updateDate"), filter.getUpdateDate(),filter.getUpdateDate2()));
            cq.orderBy(cb.desc(root.get("updateDate")));
        }
        if(filter.getSerial()!=null){
            predicates.add(cb.equal(root.get("serial"), filter.getSerial()));
        }
        if(filter.getName()!=null){
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + filter.getName().toLowerCase()+ "%"));
        }
        if(filter.getEmail()!=null){
            predicates.add(cb.like(cb.lower(root.get("email")), "%" + filter.getEmail().toLowerCase()+ "%"));
        }
        if(filter.getActive()!=null){
            predicates.add(cb.equal(root.get("active"), filter.getActive()));
        }
        if(filter.getDeleted()!=null){
            predicates.add(cb.equal(root.get("deleted"), filter.getDeleted()));
        }
        if(filter.getUpdateUser()!=null){
            predicates.add(cb.equal(root.get("updateUser"), filter.getUpdateUser()));
        }
        if(filter.getProfile()!=null && filter.getProfile().getId()!=null){
            predicates.add(cb.equal(root.get("profile").get("id"), filter.getProfile().getId()));
        }
        if(filter.getProfile()!=null && filter.getProfile().getName()!=null){
            predicates.add(cb.like(cb.lower(root.get("profile").get("name")), "%" + filter.getProfile().getName().toLowerCase()+ "%"));
        }
        
        cq.select(root);
        if(predicates.size()>0){
            cq.where(predicates.toArray(new Predicate[0]));
        }
        
        TypedQuery<User> query = entityManager.createQuery(cq);
        
        int iTotal = query.getResultList().size();

        
        
        List<User> result = query.setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        
        Page<User> page = new PageImpl<>(result, pageable, iTotal);
        for(User user: page.getContent()){
            user.setPassword(null);
        }
        
        return new PagedResponse<User>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }

    public User createUser(User user) throws BusinessLogicException, ExistentEntityException {
        validateUser(user);
        validateUnique(user);
        return userRepository.save(user);
    }

    private void validateUser(User user) throws BusinessLogicException {
        if (StringUtils.isEmpty(user.getName())) {
            throw new BusinessLogicException("El campo Name es requerido para el objeto User");
        } else if (StringUtils.isEmpty(user.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto User");
        } else if (StringUtils.isEmpty(user.getEmail())) {
            throw new BusinessLogicException("El campo Email es requerido para el objeto User");
        } else if(user.getProfile()==null){
            throw new BusinessLogicException("El campo Profile es requerido para el objeto User");
        }
        
    }
    
    private void validateUnique(User user) throws ExistentEntityException {
        List<User> users = userRepository.findByEmailIgnoreCaseContaining(user.getEmail());
        if (users!=null && !users.isEmpty()) {
            throw new ExistentEntityException(User.class,"email="+user.getEmail());
        } 
    }

    public User updateUser(UUID userId, User user) throws EntityNotExistentException, BusinessLogicException {
        if (StringUtils.isEmpty(user.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto User");
        }
        User persistedUser = getById(userId);
        if (persistedUser != null) {
            if(user.getName()!=null){
                persistedUser.setName(user.getName());
            }
            if(user.getActive()!=null){
                persistedUser.setActive(user.getActive());
            }
            if(user.getProfile()!=null){
                persistedUser.setProfile(user.getProfile());
            }
            if(user.getPassword()!=null){
                System.out.println("user.getPassword()=>"+user.getPassword());
                persistedUser.password=user.getPassword();
            }
            persistedUser.setUpdateUser(user.getUpdateUser());
            return userRepository.save(persistedUser);
        } else {
            throw new EntityNotExistentException(User.class,userId.toString());
        }
    }

    public User deleteUser(UUID userId) throws EntityNotExistentException {
        User user = getById(userId);
        user.setDeleted(Boolean.TRUE);
        user.setActive(Boolean.FALSE);
        return userRepository.save(user);
    }

    public List<User> findAll(){
        return userRepository.findAll();
    }
    
    public List<User> getByEmail(String name){
        return userRepository.getByEmail(name);
    }
    
    public List<User> findByNameIgnoreCaseContaining(String name){
        return userRepository.findByNameIgnoreCaseContaining(name);
    }
    
    public List<User> findByNameIgnoreCaseContainingAndDeleted(String name,Boolean deleted){
        return userRepository.findByNameIgnoreCaseContainingAndDeleted(name,deleted);
    }
    
    public User getBySerial(Integer serial) {
        return userRepository.getBySerial(serial);
    }
    
    public List<User> findByEmailIgnoreCaseContainingAndDeletedAndActive(String name,Boolean deleted, Boolean active){
        return userRepository.findByEmailIgnoreCaseContainingAndDeletedAndActive(name,deleted,active);
    }
    
}