package com.ai.st.microservice.ili.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ExecuteQueryUpdateToRevisionDto")
public class ExecuteQueryUpdateToRevisionDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@ApiModelProperty(required = true, notes = "Database host")
	private String databaseHost;

	@ApiModelProperty(required = true, notes = "Database port")
	private String databasePort;

	@ApiModelProperty(required = true, notes = "Database schema")
	private String databaseSchema;

	@ApiModelProperty(required = true, notes = "Database username")
	private String databaseUsername;

	@ApiModelProperty(required = true, notes = "Database password")
	private String databasePassword;

	@ApiModelProperty(required = true, notes = "Database name")
	private String databaseName;

	@ApiModelProperty(required = true, notes = "Model version")
	private String versionModel;

	@ApiModelProperty(required = true, notes = "Concept (operation, integration, etc)")
	private Long conceptId;

	@ApiModelProperty(required = true, notes = "Boundary ID")
	private Long boundarySpaceId;

	@ApiModelProperty(required = true, notes = "Url File")
	private String urlFile;

	@ApiModelProperty(required = true, notes = "Entity ID")
	private Long entityId;

	@ApiModelProperty(required = true, notes = "Namespace")
	private String namespace;

	public ExecuteQueryUpdateToRevisionDto() {

	}

	public String getDatabaseHost() {
		return databaseHost;
	}

	public void setDatabaseHost(String databaseHost) {
		this.databaseHost = databaseHost;
	}

	public String getDatabasePort() {
		return databasePort;
	}

	public void setDatabasePort(String databasePort) {
		this.databasePort = databasePort;
	}

	public String getDatabaseSchema() {
		return databaseSchema;
	}

	public void setDatabaseSchema(String databaseSchema) {
		this.databaseSchema = databaseSchema;
	}

	public String getDatabaseUsername() {
		return databaseUsername;
	}

	public void setDatabaseUsername(String databaseUsername) {
		this.databaseUsername = databaseUsername;
	}

	public String getDatabasePassword() {
		return databasePassword;
	}

	public void setDatabasePassword(String databasePassword) {
		this.databasePassword = databasePassword;
	}

	public String getDatabaseName() {
		return databaseName;
	}

	public void setDatabaseName(String databaseName) {
		this.databaseName = databaseName;
	}

	public String getVersionModel() {
		return versionModel;
	}

	public void setVersionModel(String versionModel) {
		this.versionModel = versionModel;
	}

	public Long getConceptId() {
		return conceptId;
	}

	public void setConceptId(Long conceptId) {
		this.conceptId = conceptId;
	}

	public Long getBoundarySpaceId() {
		return boundarySpaceId;
	}

	public void setBoundarySpaceId(Long boundarySpaceId) {
		this.boundarySpaceId = boundarySpaceId;
	}

	public String getUrlFile() {
		return urlFile;
	}

	public void setUrlFile(String urlFile) {
		this.urlFile = urlFile;
	}

	public Long getEntityId() {
		return entityId;
	}

	public void setEntityId(Long entityId) {
		this.entityId = entityId;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

}
