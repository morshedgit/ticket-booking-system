package yar.sam.util;


import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.DefaultValue;

public class PaginationParams {
    @QueryParam("page")
    @DefaultValue("1")
    private int page;

    @QueryParam("size")
    @DefaultValue("10")
    private int size;

    @QueryParam("sort")
    @DefaultValue("created_at,desc")
    private String sort;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }    

}

