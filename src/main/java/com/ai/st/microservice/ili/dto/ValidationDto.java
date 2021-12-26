package com.ai.st.microservice.ili.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "ValidationModel")
public class ValidationDto implements Serializable {

    private static final long serialVersionUID = -1404342333733043427L;

    private Boolean isValid;
    private String log;
    private Long requestId;
    private Long supplyRequestedId;
    private String filenameTemporal;
    private Long userCode;
    private String observations;
    private List<String> errors;
    private Boolean isGeometryValidated;
    private Boolean skipErrors;
    private String referenceId;

    public ValidationDto() {
        this.errors = new ArrayList<>();
    }

    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Long getSupplyRequestedId() {
        return supplyRequestedId;
    }

    public void setSupplyRequestedId(Long supplyRequestedId) {
        this.supplyRequestedId = supplyRequestedId;
    }

    public String getFilenameTemporal() {
        return filenameTemporal;
    }

    public void setFilenameTemporal(String filenameTemporal) {
        this.filenameTemporal = filenameTemporal;
    }

    public Long getUserCode() {
        return userCode;
    }

    public void setUserCode(Long userCode) {
        this.userCode = userCode;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public Boolean getGeometryValidated() {
        return isGeometryValidated;
    }

    public void setGeometryValidated(Boolean geometryValidated) {
        isGeometryValidated = geometryValidated;
    }

    public Boolean getSkipErrors() {
        return skipErrors;
    }

    public void setSkipErrors(Boolean skipErrors) {
        this.skipErrors = skipErrors;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

}
