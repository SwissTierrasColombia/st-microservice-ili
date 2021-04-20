package com.ai.st.microservice.ili.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IliExportResultDto implements Serializable {

    private static final long serialVersionUID = 3333909202329876777L;

    private boolean status;
    private Long integrationId;
    private String pathFile;
    private IntegrationStatDto stats;
    private String modelVersion;
    private List<String> errors;

    public IliExportResultDto() {
        this.errors = new ArrayList<>();
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Long getIntegrationId() {
        return integrationId;
    }

    public void setIntegrationId(Long integrationId) {
        this.integrationId = integrationId;
    }

    public String getPathFile() {
        return pathFile;
    }

    public void setPathFile(String pathFile) {
        this.pathFile = pathFile;
    }

    public IntegrationStatDto getStats() {
        return stats;
    }

    public void setStats(IntegrationStatDto stats) {
        this.stats = stats;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }
}
