package com.ai.st.microservice.ili.dto;

import java.io.Serializable;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Ili2pgIntegrationCadastreRegistrationDto", description = "Ili2pg Integration Cadastre-Registration")
public class Ili2pgIntegrationCadastreRegistrationDto implements Serializable {

	private static final long serialVersionUID = -2379043053790590513L;

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

	@ApiModelProperty(required = true, notes = "Cadastre - File XTF")
	private MultipartFile cadastreFileXTF;

	@ApiModelProperty(required = true, notes = "Registration - File XTF")
	private MultipartFile registrationFileXTF;

	@ApiModelProperty(required = false, notes = "Model version")
	private String versionModel;

	public Ili2pgIntegrationCadastreRegistrationDto() {
		this.versionModel = "2.9.4";
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

	public MultipartFile getCadastreFileXTF() {
		return cadastreFileXTF;
	}

	public void setCadastreFileXTF(MultipartFile cadastreFileXTF) {
		this.cadastreFileXTF = cadastreFileXTF;
	}

	public MultipartFile getRegistrationFileXTF() {
		return registrationFileXTF;
	}

	public void setRegistrationFileXTF(MultipartFile registrationFileXTF) {
		this.registrationFileXTF = registrationFileXTF;
	}

	public String getVersionModel() {
		return versionModel;
	}

	public void setVersionModel(String versionModel) {
		this.versionModel = versionModel;
	}

}
