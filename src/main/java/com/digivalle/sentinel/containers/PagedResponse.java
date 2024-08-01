/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.digivalle.sentinel.containers;

 

import java.util.List;

public class PagedResponse<T> {
    private Integer total;
    private Integer totalPages;
    private Integer currentPage;
    private Integer pageSize;
    private List<T> elements;

    public PagedResponse(Integer total, Integer totalPages, Integer currentPage, Integer pageSize, List<T> elements){
        this.total = total;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.elements = elements;
        this.totalPages = totalPages;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<T> getElements() {
        return elements;
    }

    public void setElements(List<T> elements) {
        this.elements = elements;
    }
}
