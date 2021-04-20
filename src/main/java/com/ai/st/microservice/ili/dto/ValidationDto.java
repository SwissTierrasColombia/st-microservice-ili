package com.ai.st.microservice.ili.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "ValidationModel")
public class ValidationDto implements Serializable {

    private static final long serialVersionUID = -1404342333733043427L;

    private String resultId;
    private String transfer;
    private Boolean isValid;
    private Boolean log;
    private Boolean xtfLog;
    private Long requestId;
    private Long supplyRequestedId;
    private String filenameTemporal;
    private Long userCode;
    private String observations;
    private List<String> fullLog;
    private List<String> errors;
    private Boolean isGeometryValidated;
    private String geom;

    public ValidationDto() {
        this.errors = new ArrayList<>();
    }

    public ValidationDto(String resultId, String transfer, Boolean isValid, Boolean log, Boolean xtfLog) {
        super();
        this.resultId = resultId;
        this.transfer = transfer;
        this.isValid = isValid;
        this.log = log;
        this.xtfLog = xtfLog;
        this.errors = new ArrayList<>();
    }

    public ValidationDto(String resultId, String transfer, Boolean isValid, Boolean log, Boolean xtfLog, List<String> fullLog, String geom) {
        super();
        this.resultId = resultId;
        this.transfer = transfer;
        this.isValid = isValid;
        this.log = log;
        this.xtfLog = xtfLog;
        this.fullLog = fullLog;
        this.geom = geom;
        this.errors = new ArrayList<>();
    }

    @ApiModelProperty(required = true, notes = "Result ID")
    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    @ApiModelProperty(required = true, notes = "Transfer")
    public String getTransfer() {
        return transfer;
    }

    public void setTransfer(String transfer) {
        this.transfer = transfer;
    }

    @ApiModelProperty(required = true, notes = "Is valid xtf ?")
    public Boolean getIsValid() {
        return isValid;
    }

    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }

    @ApiModelProperty(required = true, notes = "Has log ?")
    public Boolean getLog() {
        return log;
    }

    public void setLog(Boolean log) {
        this.log = log;
    }

    @ApiModelProperty(required = true, notes = "Has xtf log ?")
    public Boolean getXtfLog() {
        return xtfLog;
    }

    public void setXtfLog(Boolean xtfLog) {
        this.xtfLog = xtfLog;
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

    public List<String> getFullLog() {
        return fullLog;
    }

    public void setFullLog(List<String> fullLog) {
        this.fullLog = fullLog;
    }

    public String getGeom() {
        return geom;
    }

    public void setGeom(String geom) {
        this.geom = geom;
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
}
