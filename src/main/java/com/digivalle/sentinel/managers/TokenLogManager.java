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
import com.digivalle.sentinel.models.TokenLog;
import com.digivalle.sentinel.repositories.TokenLogRepository;
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
public class TokenLogManager {
    
    @Autowired
    private TokenLogRepository tokenLogRepository;
    
     @Autowired
    private EntityManager entityManager;
    

    public TokenLog getById(UUID id) throws EntityNotExistentException {
        Optional<TokenLog> tokenLog = tokenLogRepository.findById(id);
        if (!tokenLog.isEmpty()) {
            return tokenLog.get();
        }
        throw new EntityNotExistentException(TokenLog.class,id.toString());
    }
    
    public PagedResponse<TokenLog> getTokenLog(TokenLog filter, Paging paging){
        
        Pageable pageable = PageRequest.of(paging.getPage(), paging.getPageSize());
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        
        CriteriaQuery<TokenLog> cq = cb.createQuery(TokenLog.class);
        Root<TokenLog> root = cq.from(TokenLog.class);
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
        if(filter.getTokenId()!=null){
            predicates.add(cb.equal(root.get("tokenId"), filter.getTokenId()));
        }
        if(filter.getTransactionId()!=null){
            predicates.add(cb.equal(root.get("transactionId"), filter.getTransactionId()));
        }
        if(filter.getAction()!=null){
            predicates.add(cb.equal(root.get("action"), filter.getAction()));
        }
        
        cq.select(root);
        if(predicates.size()>0){
            cq.where(predicates.toArray(new Predicate[0]));
        }
        
        TypedQuery<TokenLog> query = entityManager.createQuery(cq);
        
        int iTotal = query.getResultList().size();

        
        
        List<TokenLog> result = query.setFirstResult((int) pageable.getOffset())
                                    .setMaxResults(pageable.getPageSize())
                                    .getResultList();
        
        
        Page<TokenLog> page = new PageImpl<>(result, pageable, iTotal);
        
        return new PagedResponse<TokenLog>((int) page.getTotalElements(),page.getTotalPages(), paging.getPage(), paging.getPageSize(), page.getContent());   
    }

    public TokenLog createTokenLog(TokenLog tokenLog) throws BusinessLogicException {
        //validateTokenLog(tokenLog);
        //validateUnique(tokenLog);
        return tokenLogRepository.save(tokenLog);
    }

    private void validateTokenLog(TokenLog tokenLog) throws BusinessLogicException {
        if (StringUtils.isEmpty(tokenLog.getUpdateUser())) {
            throw new BusinessLogicException("El campo UpdateUser es requerido para el objeto TokenLog");
        } 
    }
    
    public void deleteTokenLog(UUID tokenLogId) throws EntityNotExistentException {
        TokenLog tokenLog = getById(tokenLogId);
        tokenLog.setDeleted(Boolean.TRUE);
        tokenLog.setActive(Boolean.FALSE);
        tokenLogRepository.save(tokenLog);
    }

    public List<TokenLog> findAll(){
        return tokenLogRepository.findAll();
    }
   
}