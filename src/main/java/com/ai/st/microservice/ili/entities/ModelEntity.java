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
@Table(name = "models", schema = "ili")
public class ModelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "version_concept_id", referencedColumnName = "id", nullable = false)
    private VersionConceptEntity versionConcept;

    public ModelEntity() {

    }

    public ModelEntity(String name, VersionConceptEntity versionConcept) {
        this.name = name;
        this.versionConcept = versionConcept;
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

    public VersionConceptEntity getVersionConcept() {
        return versionConcept;
    }

    public void setVersionConcept(VersionConceptEntity versionConcept) {
        this.versionConcept = versionConcept;
    }

}
