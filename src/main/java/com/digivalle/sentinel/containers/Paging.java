package com.digivalle.sentinel.containers;

public class Paging {
    private Integer page;
    private Integer pageSize;
    private Integer all;

    public Paging(Integer page, Integer pageSize){
        this.page = page;
        this.pageSize = pageSize;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getAll() {
        return all;
    }

    public void setAll(Integer all) {
        this.all = all;
    }
    
    
}
