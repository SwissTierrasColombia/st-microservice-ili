package com.ai.st.microservice.ili.dto;

import java.io.Serializable;

public class VersionDto implements Serializable {

	private static final long serialVersionUID = -953611847535475293L;

	private Long id;
	private String name;
	private String url;
	private String models;

	public VersionDto() {

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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getModels() {
		return models;
	}

	public void setModels(String models) {
		this.models = models;
	}

}
