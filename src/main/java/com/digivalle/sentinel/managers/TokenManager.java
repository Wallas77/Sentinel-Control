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
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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

        // Building predicates
        List<Predicate> predicates = buildPredicates(filter, cb, root);

        // Applying predicates
        if (!predicates.isEmpty()) {
            cq.where(predicates.toArray(Predicate[]::new));
        }

        // Apply sorting
        applySorting(cq, cb, root, filter);

        // Query for paginated results
        TypedQuery<Token> query = entityManager.createQuery(cq)
                                               .setFirstResult((int) pageable.getOffset())
                                               .setMaxResults(pageable.getPageSize());

        // Fetch the total count using a separate query to avoid loading all results into memory
        long iTotal = countTotal(cb, filter);

        // Execute the query to get the results
        List<Token> result = query.getResultList();
        
        Page<Token> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }
    
    private List<Predicate> buildPredicates(Token filter, CriteriaBuilder cb, Root<Token> root) {
        List<Predicate> predicates = new ArrayList<>();

        if(filter.getCreationDate()!=null && filter.getCreationDate2()!=null){
            Calendar cal = Calendar.getInstance();
            cal.setTime(filter.getCreationDate());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            filter.setCreationDate(cal.getTime());
            
            cal = Calendar.getInstance();
            cal.setTime(filter.getCreationDate2());
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            filter.setCreationDate2(cal.getTime());
            predicates.add(cb.between(root.get("creationDate"), filter.getCreationDate(),filter.getCreationDate2()));
        }
        if(filter.getUpdateDate()!=null && filter.getUpdateDate2()!=null){
            Calendar cal = Calendar.getInstance();
            cal.setTime(filter.getUpdateDate());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            filter.setUpdateDate(cal.getTime());
            
            cal = Calendar.getInstance();
            cal.setTime(filter.getUpdateDate2());
            cal.set(Calendar.HOUR_OF_DAY, 23);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);
            filter.setUpdateDate2(cal.getTime());
            predicates.add(cb.between(root.get("updateDate"), filter.getUpdateDate(),filter.getUpdateDate2()));
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

        return predicates;
    }

    private void applySorting(CriteriaQuery<Token> cq, CriteriaBuilder cb, Root<Token> root, Token filter) {
        List<Order> orderList = new ArrayList<>();

        if (filter.getUpdateDate() != null && filter.getUpdateDate2() != null) {
            orderList.add(cb.desc(root.get("updateDate")));
        } else {
            orderList.add(cb.desc(root.get("creationDate")));
        }

        cq.orderBy(orderList);
    }

    private long countTotal(CriteriaBuilder cb, Token filter) {
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Token> countRoot = countQuery.from(Token.class);

        countQuery.select(cb.count(countRoot));
        List<Predicate> countPredicates = buildPredicates(filter, cb, countRoot);
        if (!countPredicates.isEmpty()) {
            countQuery.where(countPredicates.toArray(Predicate[]::new));
        }

        return entityManager.createQuery(countQuery).getSingleResult();
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
