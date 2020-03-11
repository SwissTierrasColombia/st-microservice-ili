package com.ai.st.microservice.ili.dto;

import java.util.ArrayList;
import java.util.List;

public class VersionDataDto {

	private String version;
	private String models;
	private String url;
	private List<QueryDto> queries;

	public VersionDataDto() {
		this.queries = new ArrayList<>();
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getModels() {
		return models;
	}

	public void setModels(String models) {
		this.models = models;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public List<QueryDto> getQueries() {
		return queries;
	}

	public void setQueries(List<QueryDto> queries) {
		this.queries = queries;
	}

}
