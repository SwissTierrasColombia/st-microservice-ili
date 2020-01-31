package com.ai.st.microservice.ili.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "versions_x_concepts", schema = "ili")
public class VersionConceptEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "version_id", nullable = false)
	private VersionEntity version;

	@ManyToOne
	@JoinColumn(name = "concept_id", nullable = false)
	private ConceptEntity concept;

	@Column(name = "url", nullable = false, length = 500)
	private String url;

	@OneToMany(mappedBy = "versionConcept", cascade = CascadeType.ALL)
	private List<ModelEntity> models = new ArrayList<ModelEntity>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ConceptEntity getConcept() {
		return concept;
	}

	public void setConcept(ConceptEntity concept) {
		this.concept = concept;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public VersionEntity getVersion() {
		return version;
	}

	public void setVersion(VersionEntity version) {
		this.version = version;
	}

	public List<ModelEntity> getModels() {
		return models;
	}

	public void setModels(List<ModelEntity> models) {
		this.models = models;
	}

}
