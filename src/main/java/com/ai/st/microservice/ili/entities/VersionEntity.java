package com.ai.st.microservice.ili.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "versions", schema = "ili")
public class VersionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Long id;

	@Column(name = "name", nullable = false, length = 20)
	private String name;

	@Column(name = "url", nullable = false, length = 255)
	private String url;

	@OneToMany(mappedBy = "version", cascade = CascadeType.ALL)
	private List<ModelEntity> models = new ArrayList<ModelEntity>();

	public VersionEntity() {

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

	public List<ModelEntity> getModels() {
		return models;
	}

	public void setModels(List<ModelEntity> models) {
		this.models = models;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
