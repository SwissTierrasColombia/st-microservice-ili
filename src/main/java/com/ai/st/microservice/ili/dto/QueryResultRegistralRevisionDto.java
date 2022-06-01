package com.ai.st.microservice.ili.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "QueryResultRegistralRevisionDto")
public class QueryResultRegistralRevisionDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(required = true, notes = "Current page")
    private int currentPage;

    @ApiModelProperty(required = true, notes = "Current page")
    private int totalPages;

    @ApiModelProperty(required = true, notes = "Items")
    private List<ItemRegistralRevisionDto> records;

    public QueryResultRegistralRevisionDto() {
        this.records = new ArrayList<>();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public List<ItemRegistralRevisionDto> getRecords() {
        return records;
    }

    public void setRecords(List<ItemRegistralRevisionDto> records) {
        this.records = records;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

}
