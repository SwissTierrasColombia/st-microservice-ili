package com.ai.st.microservice.ili.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "queries", schema = "ili")
public class QueryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "version_concept_id", referencedColumnName = "id", nullable = false)
    private VersionConceptEntity versionConcept;

    @ManyToOne
    @JoinColumn(name = "query_type_id", nullable = false)
    private QueryTypeEntity queryType;

    @Column(name = "query", nullable = false, length = 2000)
    private String query;

    public QueryEntity() {

    }

    public QueryEntity(VersionConceptEntity versionConcept, QueryTypeEntity queryType, String query) {
        this.versionConcept = versionConcept;
        this.queryType = queryType;
        this.query = query;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VersionConceptEntity getVersionConcept() {
        return versionConcept;
    }

    public void setVersionConcept(VersionConceptEntity versionConcept) {
        this.versionConcept = versionConcept;
    }

    public QueryTypeEntity getQueryType() {
        return queryType;
    }

    public void setQueryType(QueryTypeEntity queryType) {
        this.queryType = queryType;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

}
