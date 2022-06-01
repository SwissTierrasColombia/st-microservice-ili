package com.ai.st.microservice.ili.dto;

import java.io.Serializable;

public class QueryDto implements Serializable {

    private static final long serialVersionUID = 6521590774707274487L;

    private Long id;
    private String query;
    private QueryTypeDto queryType;

    public QueryDto() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public QueryTypeDto getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryTypeDto queryType) {
        this.queryType = queryType;
    }

}
