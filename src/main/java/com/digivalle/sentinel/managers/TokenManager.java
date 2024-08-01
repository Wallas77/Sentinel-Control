/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.digivalle.sentinel.managers;

import com.digivalle.sentinel.Definitions;
import com.digivalle.sentinel.containers.PagedResponse;
import com.digivalle.sentinel.containers.Paging;
import com.digivalle.sentinel.exceptions.BusinessLogicException;
import com.digivalle.sentinel.exceptions.EntityNotExistentException;
import com.digivalle.sentinel.exceptions.ExistentEntityException;
import com.digivalle.sentinel.models.Token;
import com.digivalle.sentinel.models.User;
import com.digivalle.sentinel.repositories.TokenRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
public class TokenManager {
    
    @Autowired
    private TokenRepository tokenRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public Token getById(UUID id) throws EntityNotExistentException {
        Optional<Token> token = tokenRepository.findById(id);
        if (!token.isEmpty()) {
            return token.get();
        }
        throw new EntityNotExistentException(Token.class,id.toString());
    }
    
    public Token getByToken(String token){
        return tokenRepository.getByToken(token);
    }
    
    public PagedResponse<Token> getToken(Token filter, Paging paging){
        
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<Token> cq = cb.createQuery(Token.class);
        Root<Token> root = cq.from(Token.class);
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
        if(filter.getToken()!=null){
            predicates.add(cb.equal(root.get("token"), filter.getToken()));
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
        
        cq.select(root);
        if(predicates.size()>0){
            cq.where(predicates.toArray(new Predicate[0]));
        }
        
        TypedQuery<Token> query = entityManager.createQuery(cq);
        
        int iTotal = query.getResultList().size();

        
        
        List<Token> result = query.setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        
        Page<Token> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<Token>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }


    public Token createToken(Token token) throws BusinessLogicException, ExistentEntityException {
        validateToken(token);
        //validateUnique(token);
        return tokenRepository.save(token);
    }
    
    public Token createToken(User user, Integer sessionTime) {
        Token token = new Token();
        Calendar date = Calendar.getInstance();
        long t= date.getTimeInMillis();
        Date afterAddingTenMins=new Date(t + (sessionTime * Definitions.ONE_MINUTE_IN_MILLIS));
        token.setValidUntil(afterAddingTenMins);
        token.setToken(UUID.randomUUID().toString());
        token.setActive(Boolean.TRUE);
        token.setUser(user);
        token.setUserProfile(user.getProfile().getName());
        token.setUpdateUser(user.getEmail());
        token.setUserIdentifier(user.getId());
         
        token = tokenRepository.save(token);

        return token;
    }

    private void validateToken(Token token) throws BusinessLogicException {
        if (StringUtils.isEmpty(token.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto Token");
        } 
    }
    
    /*private void validateUnique(Token token) throws ExistentEntityException {
        List<Token> tokens = tokenRepository.findByEmailIgnoreCaseContaining(token.getEmail());
        if (tokens!=null && !tokens.isEmpty()) {
            throw new ExistentEntityException(Token.class,"email="+token.getEmail());
        } 
    }*/

    

    public void deleteToken(UUID tokenId) throws EntityNotExistentException {
       
        tokenRepository.delete(getById(tokenId));
    }

    public List<Token> findAll(){
        return tokenRepository.findAll();
    }
    
    public Token getByUser(User user) {
        Token retToken = null;
        List<Token> tokens = tokenRepository.findByUser(user);
        if(tokens!=null && !tokens.isEmpty()){
            for(Token token: tokens){
                if(token.getValidUntil().after(Calendar.getInstance().getTime())){
                    retToken =token;
                }
            }
        }
        return retToken;
    }
    
    public void deleteActiveTokensByUser(User user) {
        final List<Token> tokens = tokenRepository.findByUser(user);
        for(Token token : tokens){
            tokenRepository.delete(token);
        }
    }
    
    public List<Token> findByUser(User user){
        return tokenRepository.findByUser(user);
    }
    
}
