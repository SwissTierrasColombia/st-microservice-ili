package com.ai.st.microservice.ili.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Ili2pgImportReferenceDto")
public class Ili2pgImportReferenceDto implements Serializable {

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

    @ApiModelProperty(required = true, notes = "Path file XTF")
    private String pathXTF;

    @ApiModelProperty(required = true, notes = "Model version")
    private String versionModel;

    @ApiModelProperty(required = true, notes = "Concept (operation, integration, etc)")
    private Long conceptId;

    @ApiModelProperty(required = false, notes = "Reference")
    private String reference;

    public Ili2pgImportReferenceDto() {

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

    public String getPathXTF() {
        return pathXTF;
    }

    public void setPathXTF(String pathXTF) {
        this.pathXTF = pathXTF;
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

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @Override
    public String toString() {
        return "Ili2pgImportReferenceDto{" + "databaseHost='" + databaseHost + '\'' + ", databasePort='" + databasePort
                + '\'' + ", databaseSchema='" + databaseSchema + '\'' + ", databaseUsername='" + databaseUsername + '\''
                + ", databasePassword='" + databasePassword + '\'' + ", databaseName='" + databaseName + '\''
                + ", pathXTF='" + pathXTF + '\'' + ", versionModel='" + versionModel + '\'' + ", conceptId=" + conceptId
                + ", reference='" + reference + '\'' + '}';
    }
}
