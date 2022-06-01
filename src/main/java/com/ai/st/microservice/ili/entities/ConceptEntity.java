package com.ai.st.microservice.ili.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "concepts", schema = "ili")
public class ConceptEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @OneToMany(mappedBy = "concept")
    private List<VersionConceptEntity> versionsConcepts = new ArrayList<>();

    public ConceptEntity() {

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

    public List<VersionConceptEntity> getVersionsConcepts() {
        return versionsConcepts;
    }

    public void setVersionsConcepts(List<VersionConceptEntity> versionsConcepts) {
        this.versionsConcepts = versionsConcepts;
    }

}
