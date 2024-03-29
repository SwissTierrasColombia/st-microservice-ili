package com.ai.st.microservice.ili.dto;

import java.io.Serializable;

public class QueryTypeDto implements Serializable {

    private static final long serialVersionUID = 4405613960817014090L;

    private Long id;
    private String name;

    public QueryTypeDto() {

    }

    public QueryTypeDto(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
