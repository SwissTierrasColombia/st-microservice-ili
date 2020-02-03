package com.ai.st.microservice.ili.dto;

public class VersionDataDto {

	private String version;
	private String models;
	private String url;

	public VersionDataDto() {

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

}
